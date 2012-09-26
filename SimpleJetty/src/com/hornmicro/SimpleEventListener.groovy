package com.hornmicro

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener

class SimpleEventListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // TODO Auto-generated method stub
        sce.getServletContext().getInitParameterNames().each {
            println it
        }
        println getClass().getClassLoader().getURLs()
        
        
        println(sce.getServletContext().getResourceAsStream("/WEB-INF/applicationContext.xml"))
        //println(sce.getServletContext().getResourceAsStream("WEB-INF/applicationContext.xml"))
        println(sce.getServletContext().getResourceAsStream("/another/applicationContext.xml"))
        //println(sce.getServletContext().getResourceAsStream("another/applicationContext.xml"))
        
        //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //println(classLoader.getResourceAsStream("/another/applicationContext.xml"))
        
        
        println(getClass().getResourceAsStream("/another/applicationContext.xml"))
        
        println sce.getServletContext().getRealPath("/another/applicationContext.xml")
        
        
        //getResourceAsStream("/WEB-INF/applicationContext.xml")
        
    }

    
}
