package com.hornmicro

import java.util.jar.Attributes
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

import org.codehaus.groovy.runtime.StackTraceUtils

class FatJar {
    Map services = [:]
    Map entries = [:]
    final byte[] BUFFER = new byte[4 * 1024 * 1024]
    
    void copyEntry(JarInputStream jis, JarOutputStream jos, JarEntry entry) {
        if(entry.name && !entries[entry.name]) {
            jos.putNextEntry(entry)
            if(!entry.isDirectory()) {
                int bytesRead = 0;
                while (-1 != (bytesRead = jis.read(BUFFER))) {
                    jos.write(BUFFER, 0, bytesRead);
                }
            }
            entries[entry.name] = true
            jos.closeEntry()
        } else if(!entry.isDirectory()){
            println "Duplicate dropped $entry.name"
        }
    }
    
    void moveEntry(JarInputStream jis, JarOutputStream jos, String name) {
        if(name && !entries[name]) {
            JarEntry entry = new JarEntry(name)
            jos.putNextEntry(entry)
            if(!entry.isDirectory()) {
                int bytesRead = 0;
                while (-1 != (bytesRead = jis.read(BUFFER))) {
                    jos.write(BUFFER, 0, bytesRead);
                }
            }
            entries[entry.name] = true
            jos.closeEntry()
        } else if(!entry.isDirectory()){
            println "Duplicate dropped $name"
        }
    }
    
    ByteArrayOutputStream getEntryAsOutputStream(JarInputStream jis, JarEntry entry) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(entry.size > 0 ? entry.size as int: 1024)
        int bytesRead = 0;
        while (-1 != (bytesRead = jis.read(BUFFER))) {
            baos.write(BUFFER, 0, bytesRead);
        }
        return baos
    }
    
    void walkJar(JarInputStream jis, JarOutputStream jos) {
        JarEntry entry
        while( (entry = jis.getNextJarEntry()) ) {
            String name = entry?.name
            
            if( entry.isDirectory()) {
                copyEntry(jis, jos, entry)
            } else if( name.endsWith(".jar") ) {
                flattenJarInEntry(jis, jos, entry)
            } else if ( name =~ /META-INF\/(.*(\.SF|\.RSA|\.DSA|\.INF)|MANIFEST.MF)/ ) {
                
                // Drop these
                println "Dropped $name"
            
            } else if ( name.startsWith("/META-INF/services/") || name =~ /META-INF\/spring\.(handlers|schemas)/ ) {
                
                // Group these and add last
                String serviceName = entry.name
                String serviceItems = getEntryAsOutputStream(jis, entry)?.toString()?.trim()
                if(serviceItems) {
                    if(!services[serviceName]) {
                        services[serviceName] = []
                    }
                    services[serviceName].add(serviceItems)
                }
            } else if ( name.startsWith("WEB-INF/classes/") ) {
            
                // Move these to root
                moveEntry(jis, jos, entry.name.replaceAll("WEB-INF/classes/", ""))
            
            } else {
                copyEntry(jis, jos, entry)
            }
        }
    }
    
    void flattenJarInEntry(JarInputStream jis, JarOutputStream jos, JarEntry entry) {
        
        //println "Flattening $entry.name"
        ByteArrayInputStream bis = new ByteArrayInputStream(getEntryAsOutputStream(jis, entry).toByteArray())
        JarInputStream innerJis = new JarInputStream(bis)
        walkJar(innerJis, jos)
    }
    
    static main(args) {
        try {
            new File("/Users/shorn/dev/fatjar/dropwizard-grails/target/dropwizard-grails-0.1.war").withInputStream { InputStream is ->
                JarInputStream jis = new JarInputStream(is)
                
                Manifest manifest = new Manifest()
                Attributes mainAttributes = manifest.mainAttributes
                [
                    'Manifest-Version' : "1.0",
                    'Main-Class' : "go.Go"
                ].each { k,v ->
                    mainAttributes.putValue(k, v)
                }
                
                new File("fat.jar").withOutputStream { OutputStream os ->
                    JarOutputStream jos = new JarOutputStream(os, manifest)
                    
                    FatJar fatjar = new FatJar()
                    fatjar.walkJar(jis, jos)
                    fatjar.services.each { name, contents ->
                        jos.putNextEntry(new JarEntry(name))
                        jos << contents.join("\n")
                        jos.closeEntry()
                    }
                    jos.close()
                }
                jis.close()
            }
        } catch(e) {
            StackTraceUtils.deepSanitize(e)
            e.printStackTrace()
        }
    }

}
