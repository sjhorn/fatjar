package go

import java.security.ProtectionDomain

import org.eclipse.jetty.util.resource.Resource
import org.springframework.core.io.ClassPathResource

class Test {

    static main(args) {
        
        ProtectionDomain domain = Test.class.getProtectionDomain();
        URL location = domain.getCodeSource().getLocation();

        println location
        
//        URL url= Resource.class.getResource("rsrc:WEB-INF/applicationContext.xml");
        
        //Resource res = Resource.newClassPathResource("rsrc:WEB-INF/applicationContext.xml")
        
        //println new ClassPathResource("WEB-INF/applicationContext.xml").getURI().toString()
                
        //String webDir = Test.class.getClassLoader().getResource("rsrc:WEB-INF/applicationContext.xml").toExternalForm()
        
        
        //println "Hello World ${webDir} ${url} ${res}"
    }

}
