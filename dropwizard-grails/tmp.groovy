import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

InputStream is = new URL("file:/Users/shorn/dev/FatJar/dropwizard-grails/target/dropwizard-grails-0.1.jar").openStream()

jarIs = new JarInputStream(is);
JarEntry jarEntry;
while( (jarEntry = jarIs.getNextEntry() ) != null ) {
    String name = jarEntry.getName();
    System.out.println("Looking at "+name);
    
}