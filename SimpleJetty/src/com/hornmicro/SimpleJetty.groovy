package com.hornmicro

import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource

class SimpleJetty {

    static main(args) {
        try {
            Server server = new Server(8080)
            
            final ServletContextHandler handler = new ServletContextHandler() //ServletContextHandler.SESSIONS);
            //handler.setContextPath("/");
            println Resource.newClassPathResource(".")
            handler.setBaseResource(Resource.newClassPathResource("."))
            //Resource.newResource(new File(".")));

            handler.addServlet(new ServletHolder(new HelloServlet()), "/")
            
            handler.setInitParameter("contextConfigLocation", "WEB-INF/applicationContext.xml")
            handler.addEventListener(new SimpleEventListener())
            
            
            
            server.setHandler(handler)
            server.start()
            server.join()
        } catch(e) {
            StackTraceUtils.deepSanitize(e)
            e.printStackTrace()
        }
    }
}