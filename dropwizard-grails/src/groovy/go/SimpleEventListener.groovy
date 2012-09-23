package go

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
    }

    
}
