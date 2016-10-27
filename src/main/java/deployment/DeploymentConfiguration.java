package deployment;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class DeploymentConfiguration implements ServletContextListener {
    public static String PU_NAME = "pu_development"; //USE the RIGHT name here
    @Override
    public void contextInitialized(ServletContextEvent sce) {
      PU_NAME = sce.getServletContext().getInitParameter("pu-name");    
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
