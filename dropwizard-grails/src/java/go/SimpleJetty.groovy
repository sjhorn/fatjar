package go

import javax.servlet.DispatcherType

import org.codehaus.groovy.grails.web.context.GrailsContextLoaderListener
import org.codehaus.groovy.grails.web.filters.HiddenHttpMethodFilter
import org.codehaus.groovy.grails.web.mapping.filter.UrlMappingsFilter
import org.codehaus.groovy.grails.web.pages.GroovyPagesServlet
import org.codehaus.groovy.grails.web.servlet.ErrorHandlingServlet
import org.codehaus.groovy.grails.web.servlet.GrailsDispatcherServlet
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequestFilter
import org.codehaus.groovy.grails.web.sitemesh.GrailsPageFilter
import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.util.resource.ResourceCollection
import org.springframework.web.filter.DelegatingFilterProxy

class SimpleJetty {

    static main(args) {
        try {
            Server server = new Server(8080)
            final ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            handler.setContextPath("/woot");
            
            ResourceCollection resources = new ResourceCollection()
            List resourceList = [
                Resource.newResource("jar:file:/Users/shorn/dev/fatjar/dropwizard-grails/target/dropwizard-grails-0.1.war!/")
            ]
            resources.setResources( resourceList.toArray(new Resource[resourceList.size()] ))
            handler.setBaseResource(resources)
            
            handler.addServlet(GrailsDispatcherServlet.class, "*.dispatch");
            handler.addServlet(GroovyPagesServlet.class, "*.gsp");
            handler.addServlet(ErrorHandlingServlet.class, "/grails-errorhandler");
            
            handler.addEventListener(new GrailsContextLoaderListener())
            
            
            handler.addFilter(GrailsPageFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST))
            
            FilterHolder fh = new FilterHolder(DelegatingFilterProxy.class)
            fh.setInitParameters([
                "targetBeanName" : "characterEncodingFilter",
                "targetFilterLifecycle": "true"
            ])
            handler.addFilter(fh, "/*", EnumSet.of(DispatcherType.REQUEST))
            handler.addFilter(GrailsWebRequestFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ERROR))
            handler.addFilter(UrlMappingsFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD))
            handler.addFilter(HiddenHttpMethodFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD))
            
            handler.setConnectorNames(["main"] as String[]);
            
            server.setHandler(handler)
            server.start()
            server.join()
            //server.stop()
        } catch(e) {
            StackTraceUtils.deepSanitize(e)
            e.printStackTrace()
        }
    }
}



/*
            new JarFile("/Users/shorn/dev/fatjar/dropwizard-grails/target/dropwizard-grails-0.1.war").entries().each { JarEntry entry ->
                if(entry.name.endsWith(".jar")) {
                    println "adding ${entry.name}"
                    resourceList.add(Resource.newResource("jar:file:/Users/shorn/dev/fatjar/dropwizard-grails/target/dropwizard-grails-0.1.war!/${entry.name}"))
                }
            }
            */