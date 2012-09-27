includeTargets << grailsScript("_GrailsSettings")
includeTargets << grailsScript("_GrailsWar")
includeTargets << grailsScript("_GrailsPackage")

target(dropwMain: "Run in dropwizard") {
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

setDefaultTarget('dropwMain')