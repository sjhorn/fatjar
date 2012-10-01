package go;


import java.io.IOException;
import java.util.HashMap;

import org.codehaus.groovy.grails.web.context.GrailsContextLoaderListener;
import org.codehaus.groovy.grails.web.filters.HiddenHttpMethodFilter;
import org.codehaus.groovy.grails.web.mapping.filter.UrlMappingsFilter;
import org.codehaus.groovy.grails.web.pages.GroovyPagesServlet;
import org.codehaus.groovy.grails.web.servlet.ErrorHandlingServlet;
import org.codehaus.groovy.grails.web.servlet.GrailsDispatcherServlet;
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequestFilter;
import org.codehaus.groovy.grails.web.sitemesh.GrailsPageFilter;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.resource.Resource;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Environment;

public class Go extends Service<GoConfiguration> {
    public static void main(String[] args) throws Exception {
        new Go().run(args);
    }

    private Go() {
        super("go");
    }

//    public ServletContainer getJerseyContainer(DropwizardResourceConfig resourceConfig, GoConfiguration serviceConfig) {
//        return null;
//    }
    
    @Override
    protected void initialize(GoConfiguration configuration,
                              Environment environment) throws ClassNotFoundException {
        try {
            environment.setBaseResource(Resource.newResource(".webapp"));
        } catch(IOException ioe) {
            
        }
        environment.setProtectedTargets(new String[] { "/web-inf", "/meta-inf"} );
        
        environment.addServlet(DefaultServlet.class, "/");
        environment
            .addFilter(DelegatingFilterProxy.class, "/*")
            .setName("charEncodingFilter")
            .addInitParams(new HashMap<String,String>() {{
                put("targetBeanName", "characterEncodingFilter"); 
                put("targetFilterLifecycle", "true"); 
            }});
        
        environment
            .addFilter(HiddenHttpMethodFilter.class, "/*")
            .setName("hiddenHttpMethod");
        
        environment
            .addFilter(GrailsWebRequestFilter.class, "/*")
            .setName("grailsWebRequest");
        
        environment
            .addFilter(GrailsPageFilter.class, "/*")
            .setName("sitemesh");
        
        environment
            .addFilter(UrlMappingsFilter.class, "/*")
            .setName("urlMapping");
        
        environment.addServletListeners(new SimpleEventListener());
        environment.addServletListeners(new GrailsContextLoaderListener());
        
        environment.addServlet(GroovyPagesServlet.class, "*.gsp");
        environment.addServlet(ErrorHandlingServlet.class, "/grails-errorhandler");
        environment.addServlet(GrailsDispatcherServlet.class, "*.dispatch");
        
        
        environment.addResource(new HelloWorldResource());
            
    }
}
