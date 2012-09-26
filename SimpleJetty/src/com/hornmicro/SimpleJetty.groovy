package com.hornmicro

import java.util.jar.JarEntry
import java.util.jar.JarFile

import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.util.resource.ResourceCollection

class SimpleJetty {

    static main(args) {
        try {
            Server server = new Server(8080)
            
            final ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            handler.setContextPath("/");
            
            ResourceCollection resources = new ResourceCollection()
            
            List resourceList = [
                Resource.newResource("jar:file:/Users/shorn/dev/fatjar/dropwizard-grails/target/dropwizard-grails-0.1.war!/"),
                Resource.newResource("jar:file:/Users/shorn/dev/fatjar/dropwizard-grails/target/dropwizard-grails-0.1.war!/WEB-INF/classes")
            ]
            new JarFile("/Users/shorn/dev/fatjar/dropwizard-grails/target/dropwizard-grails-0.1.war").entries().each { JarEntry entry ->
                if(entry.name.endsWith(".jar")) {
                    println "adding ${entry.name}"
                    resourceList.add(Resource.newResource("jar:file:/Users/shorn/dev/fatjar/dropwizard-grails/target/dropwizard-grails-0.1.war!/${entry.name}"))
                }
            }
            resources.setResources( resourceList.toArray(new Resource[resourceList.size()] ))
            
            
            handler.addServlet(GrailsDispatcherServlet.class, "*.dispatch");
            handler.addServlet(GroovyPagesServlet.class, "*.gsp");
            handler.addServlet(ErrorHandlingServlet.class, "/grails-errorhandler");
            
            
            
            
            
            /*
             for (ImmutableMap.Entry<String, ServletHolder> entry : env.getServlets().entrySet()) {
            handler.addServlet(entry.getValue(), entry.getKey());
        }

        for (ImmutableMap.Entry<String, FilterHolder> entry : env.getFilters().entries()) {
            handler.addFilter(entry.getValue(), entry.getKey(), EnumSet.of(DispatcherType.REQUEST));
        }

        for (EventListener listener : env.getServletListeners()) {
            handler.addEventListener(listener);
        }

        for (Map.Entry<String, String> entry : config.getContextParameters().entrySet()) {
            handler.setInitParameter( entry.getKey(), entry.getValue() );
        }

        handler.setSessionHandler(env.getSessionHandler());

        handler.setConnectorNames(new String[]{"main"});
 
             */
            
            
            /*
            handler.setBaseResource(resources)
            handler.addServlet(new ServletHolder(new HelloServlet()), "/")
            handler.setInitParameter("contextConfigLocation", "WEB-INF/applicationContext.xml")
            handler.addEventListener(new SimpleEventListener())
            */
            
            
            server.setHandler(handler)
            server.start()
            //server.stop()
            server.join()
        } catch(e) {
            StackTraceUtils.deepSanitize(e)
            e.printStackTrace()
        }
    }
}