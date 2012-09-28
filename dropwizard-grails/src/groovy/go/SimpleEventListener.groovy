package go

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

class SimpleEventListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        System.out.println(">>>>>>>>>>>>"+cl.hashCode());
        System.out.println("++++++++++++"+sce.getServletContext());
        
        
        //ServletContext sc = sce.getServletContext()
        //sc.getResourceAsStream("/WEB-INF/applicationContext.xml")
        //println "hello world"
    }

    
}
