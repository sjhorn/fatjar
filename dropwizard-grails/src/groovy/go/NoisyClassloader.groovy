package go

import java.io.InputStream;
import java.net.URLClassLoader

class NoisyClassloader extends URLClassLoader {
    
    /*
     
     */
    
    public NoisyClassloader() {
        super(
            [] as URL[], 
            Thread.currentThread().getContextClassLoader() != null ? Thread.currentThread().getContextClassLoader() : ClassLoader.getSystemClassLoader()
        )
    }
    
    
    InputStream getResourceAsStream(String arg0) {
        println "resolving $arg0"
        return super.getResourceAsStream(arg0);
    }
}
