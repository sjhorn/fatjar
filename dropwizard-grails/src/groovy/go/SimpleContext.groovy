package go

import java.io.IOException;
import java.security.PermissionCollection;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppClassLoader;

class SimpleContext implements WebAppClassLoader.Context {
    
    public Resource newResource(String urlOrPath) throws IOException {
        return Resource.newResource(urlOrPath)
    }

    public PermissionCollection getPermissions() {
        return null;
    }

    public boolean isSystemClass(String clazz) {
        return false;
    }

    public boolean isServerClass(String clazz) {
        return false;
    }

    public boolean isParentLoaderPriority() {
        return false;
    }

    public String getExtraClasspath() {
        return null;
    }
    
}
