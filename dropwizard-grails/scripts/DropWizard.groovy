import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

includeTargets << grailsScript("_GrailsSettings")
includeTargets << grailsScript("_GrailsWar")
includeTargets << grailsScript("_GrailsPackage")

target(run: "Run in dropwizard") {
    buildConfig.grails.war.exploded = true
    buildConfig.grails.project.war.exploded.dir = "target/war"
    
    depends(parseArguments, checkVersion, war)
    
    File warDir = new File("${basedir}/target/war")
    File libDir = new File(warDir, "WEB-INF/lib")
    List classPaths = []
    libDir.eachFile  {  File file ->
        classPaths.add("target/war/WEB-INF/lib/${file.name}")
    }
    classPaths.add("target/war")
    classPaths.add("target/war/WEB-INF/classes")
    
    Process proc = "java -cp ${classPaths.join(':')} go.Go server example.yml".execute()
    proc.consumeProcessOutput(System.out, System.err)
    proc.waitFor()
    
}

target(fatjar: "Create and run fatjar") {
    buildConfig.grails.war.exploded = true
    buildConfig.grails.project.war.exploded.dir = "target/war"
    
    depends(parseArguments, checkVersion, war)
    
    
    String jarName = "${metadata['app.name']}-${metadata['app.version'] ?: '0.1-SNAPSHOT'}.jar"
    println "Writing to target/$jarName"
    new File("target/${jarName}").withOutputStream { OutputStream os ->
        
        Manifest manifest = new Manifest()
        def mainAttributes = manifest.mainAttributes
        [
            'Manifest-Version' : '1.0',
            'Rsrc-Main-Class' : 'go.Go',
            'Main-Class' : 'org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader',
            'Grails-Version' : '2.1.1',
            'Class-Path' : '.'
        ].each { k,v ->
            mainAttributes.putValue(k, v)
        }
        
        List classPaths = ['./']
        new File("target/war/WEB-INF/lib").eachFile { File file -> 
            classPaths.add(file.name)
        }
        mainAttributes.putValue('Rsrc-Class-Path', classPaths.join(" "))
        
        JarOutputStream jar = new JarOutputStream(os, manifest)
        
        Map entries = [:]
        def addEntry = { File file, String name = null ->
            name = name ?: file.name
            if(file.isDirectory() && name[-1] != '/') {
                name += '/'
            }
            JarEntry jarEntry = new JarEntry(name)
            if(!entries[jarEntry.name]) {
                jarEntry.setTime(file.lastModified())
                jar.putNextEntry(jarEntry)
                if(!file.isDirectory()) {
                    jar.write(file.readBytes())
                }
                entries[jarEntry.name] = true
                jar.closeEntry()
            }
        }
        def addLibs = {
            new File("target/war/WEB-INF/lib").eachFileRecurse { file ->
                addEntry(file)
            }
        }
        def addClasses = { 
            new File("target/war/WEB-INF/classes").eachFileRecurse { file ->
                addEntry(file, file.path.replaceAll('target/war/WEB-INF/classes/', ''))
            }
        }
        def addFiles = {
            Map services = [:]
            new File("target/war").eachFileRecurse { file ->
                String name = file.path.replaceAll('target/war/', '')
                if ( name =~ /META-INF\/(.*(\.SF|\.RSA|\.DSA|\.INF)|MANIFEST.MF)/ ) {
                    println "Dropped $name"
                } else if ( name.startsWith("META-INF/services/") || name =~ /META-INF\/spring\.(handlers|schemas)/ ) {
                    String serviceItems = file.text?.trim()
                    if(serviceItems) {
                        if(!services[name]) {
                            services[name] = []
                        }
                        services[name].add(serviceItems)
                    }
                } else if( !name.startsWith("WEB-INF/lib") && !name.startsWith("WEB-INF/classes") ) {
                    addEntry(file, name)
                }
            }
            services.each { name, contents ->
                jar.putNextEntry(new JarEntry(name))
                jar << contents.join("\n")
                jar.closeEntry()
            }
        }
        
        addLibs()
        addClasses()
        addFiles()
        
        jar.close()
    }

}

setDefaultTarget('fatjar')