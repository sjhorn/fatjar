package go

import javax.servlet.DispatcherType
import javax.servlet.http.HttpServlet

import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ErrorPageErrorHandler
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.servlet.FilterMapping
import org.eclipse.jetty.servlet.Holder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.servlet.ServletMapping
import org.eclipse.jetty.servlet.ServletContextHandler.JspConfig
import org.eclipse.jetty.servlet.ServletContextHandler.TagLib
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.webapp.WebAppClassLoader
import org.eclipse.jetty.webapp.WebAppContext
import org.slf4j.LoggerFactory

import ch.qos.logback.classic.Logger

class SimpleJetty extends HttpServlet {
    
    static void addFilter(ServletContextHandler handler, String name, String className, Map initParams = null) {
        FilterHolder holder = handler.getServletHandler().newFilterHolder(Holder.Source.DESCRIPTOR)
        holder.setName(name)
        handler.getServletHandler().addFilter(holder)
        holder.setClassName(className)
        if(initParams) {
            holder.setInitParameters(initParams)
        }
    }
    
    static void addFilterMapping(ServletContextHandler handler, String filterName, String urlPattern, List<String> dispatchList = null) {
        FilterMapping mapping = new FilterMapping()
        mapping.setFilterName(filterName)
        mapping.setPathSpec(urlPattern)
        if(dispatchList) {
            List<DispatcherType> dispatches = new ArrayList<DispatcherType>()
            dispatchList.each { String d ->
                dispatches.add(FilterMapping.dispatch(d))
            }
            if (dispatches.size()>0) {
                mapping.setDispatcherTypes(EnumSet.copyOf(dispatches))
            }
        }
        handler.getServletHandler().addFilterMapping(mapping)
    }
    
    static void addServlet(ServletContextHandler handler, String name, String className, Map initParams = null, Integer loadOnStartup = null) {
        ServletHolder holder = handler.getServletHandler().newServletHolder(Holder.Source.DESCRIPTOR)
        holder.setName(name)
        handler.getServletHandler().addServlet(holder)
        holder.setClassName(className)
        if(initParams) {
            holder.setInitParameters(initParams)
        }
        if(loadOnStartup) {
            holder.setInitOrder(loadOnStartup)
        }
    }
    
    static void addServletMapping(ServletContextHandler handler, String servletName, String urlPattern) {
        ServletMapping mapping = new ServletMapping()
        mapping.setServletName(servletName)
        mapping.setPathSpec(urlPattern)
        handler.getServletHandler().addServletMapping(mapping)
    }
    
    
    static void addListener(ServletContextHandler handler, String className) {
        Class<? extends EventListener> listenerClass = (Class<? extends EventListener>)handler.loadClass(className)
        EventListener listener = handler.getServletContext().createListener(listenerClass)
        handler.addEventListener(listener)
    }
    
    static void addJspTaglib(ServletContextHandler handler, String uri, String location) {
        JspConfig config = (JspConfig)handler.getServletContext().getJspConfigDescriptor()
        if (config == null) {
           config = new JspConfig();
           handler.getServletContext().setJspConfigDescriptor(config);
        }
        //handler.setResourceAlias(uri, location)
        TagLib tl = new TagLib()
        tl.setTaglibLocation(location)
        tl.setTaglibURI(uri)
        config.addTaglibDescriptor(tl)
    }

    static main(args) {
        try {
            Logger logger = LoggerFactory.getLogger(SimpleJetty)
            logger.info("Hello world.")
        
            
            
            Server server = new Server(8080)
            boolean doit = true
            Handler webapp
            if(doit == true) {
                //WebXmlConfiguration config
                //WebAppContext 
                webapp = new WebAppContext()
                webapp.setContextPath("/")
                webapp.setWar("./target/out")
            } else {
                
                Thread current_thread = Thread.currentThread()
                //ClassLoader old_classloader = current_thread.getContextClassLoader()
                WebAppClassLoader wacl = new WebAppClassLoader(null, new SimpleContext())
                Resource war = Resource.newResource(new File("target/out").absolutePath)
                wacl.addClassPath(war)
                
                // Add war classpath stuff
                Resource web_inf = war.addPath("WEB-INF")
                if (web_inf != null && web_inf.isDirectory()) {
                    println "here"
                    // Look for classes directory
                    Resource classes= web_inf.addPath("classes/")
                    if (classes.exists())
                        wacl.addClassPath(classes);
        
                    // Look for jars
                    //Resource lib = web_inf.addPath("lib/")
                    //if (lib.exists() || lib.isDirectory())
                    //    wacl.addJars(lib)
                }
                
                
                
                current_thread.setContextClassLoader(wacl)
            
                //ServletContextHandler 
                webapp = new ServletContextHandler(ServletContextHandler.SESSIONS | ServletContextHandler.SECURITY)
                webapp.setErrorHandler(new ErrorPageErrorHandler())
                webapp.setContextPath("/")
                
                webapp.setBaseResource(Resource.newResource(new File("target/out").absolutePath))
                //webapp.setResourceBase(Resource.newResource("./target/out").getFile().getAbsolutePath())
                //webapp.setBaseResource(Resource.newResource("target/out"))
                
                webapp.setInitParameter("contextConfigLocation", "/WEB-INF/applicationContext.xml")
                webapp.setInitParameter("webAppRootKey", "dropwizard-grails-production-0.1")
                
                addFilter(webapp, "sitemesh", "org.codehaus.groovy.grails.web.sitemesh.GrailsPageFilter")
                addFilter(webapp, "charEncodingFilter", "org.springframework.web.filter.DelegatingFilterProxy", [
                    targetBeanName : "characterEncodingFilter",
                    targetFilterLifecycle : "true"
                ])
                addFilter(webapp, "urlMapping", "org.codehaus.groovy.grails.web.mapping.filter.UrlMappingsFilter")
                addFilter(webapp, "hiddenHttpMethod", "org.codehaus.groovy.grails.web.filters.HiddenHttpMethodFilter")
                addFilter(webapp, "grailsWebRequest", "org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequestFilter")
                
                
                addFilterMapping(webapp, "charEncodingFilter", "/*")
                addFilterMapping(webapp, "hiddenHttpMethod", "/*", ["REQUEST", "ERROR"])
                addFilterMapping(webapp, "grailsWebRequest", "/*", ["FORWARD", "REQUEST", "ERROR"])
                addFilterMapping(webapp, "sitemesh", "/*", ["REQUEST", "ERROR"])
                addFilterMapping(webapp, "urlMapping", "/*", ["FORWARD", "REQUEST"])
                
                addListener(webapp, "org.codehaus.groovy.grails.web.context.GrailsContextLoaderListener")
                
                addServlet(webapp, "grails", "org.codehaus.groovy.grails.web.servlet.GrailsDispatcherServlet", null, 1)
                addServlet(webapp, "gsp", "org.codehaus.groovy.grails.web.pages.GroovyPagesServlet")
                addServlet(webapp, "grails-errorhandler", "org.codehaus.groovy.grails.web.servlet.ErrorHandlingServlet")
                
                addServletMapping(webapp, "gsp", "*.gsp")
                addServletMapping(webapp, "grails-errorhandler", "/grails-errorhandler")
                addServletMapping(webapp, "grails", "*.dispatch")
                
                // session-timeout
                webapp.getSessionHandler().getSessionManager().setMaxInactiveInterval(30 * 60)
                
                webapp.setWelcomeFiles(["index.gsp"] as String[])
                
                
                ErrorPageErrorHandler errorHandler = (ErrorPageErrorHandler) webapp.getErrorHandler()
                errorHandler.addErrorPage(500, "/grails-errorhandler")
                
                addJspTaglib(webapp, "http://java.sun.com/jsp/jstl/core", "/WEB-INF/tld/c.tld")
                addJspTaglib(webapp, "http://java.sun.com/jsp/jstl/fmt", "/WEB-INF/tld/fmt.tld")
                addJspTaglib(webapp, "http://www.springframework.org/tags", "/WEB-INF/tld/spring.tld")
                addJspTaglib(webapp, "http://grails.codehaus.org/tags", "/WEB-INF/tld/grails.tld")
            }
            server.setHandler(webapp)
            server.start()
            println "Server started"
            server.join()
            //server.stop()
        } catch(e) {
            StackTraceUtils.deepSanitize(e)
            e.printStackTrace()
        }
    }
}