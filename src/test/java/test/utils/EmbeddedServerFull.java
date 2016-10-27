package test.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.http.fileupload.FileUtils;

/**
 * Example that starts an embedded Tomcat (in this case from within a
 * web-project, since purpose eventually will be testing a such),
 *
 * and deploys a war file including the web-project.
 *
 * If the port is set to '8888' and the context path to '/seed', test via:
 *
 * http://localhost:8888/seed To test the single web-page
 *
 * http://localhost:8888/seed/api/hello To test the single (GET) REST service
 *
 * @author lam
 */
public class EmbeddedServerFull implements AutoCloseable {

  private final static Logger LOGGER = Logger.getLogger(EmbeddedServerFull.class.getName());

  private static Server server;
  private final String tmp_dir;

  private EmbeddedServerFull(Server s, String tmp_dir) {
    server = s;
    this.tmp_dir = tmp_dir;

    server.addLifecycleListener((LifecycleEvent event) -> {
      if (event.getLifecycle().getState() == LifecycleState.STOPPED) {
        System.out.println("STOPPED");
      }
    });
  }

  /**
   * Shuts down the server.
   *
   * @throws LifecycleException If the server failed to shut down.
   */
  @Override
  public void close() throws LifecycleException {
    server.stop();
    server.destroy();
    server = null;
  }

  /**
   * Starts a new embedded Tomcat server.
   *
   * @param warPath The path to the tomcat application .war file.
   * @param port The port to expose the server on. Example: 8888
   * @param contextPath The URL path for the context application. Example: /app.
   * @param pathToAlternativeWeb_xml Path to an alternative web.xml file (use to
   * pass in information about Persistence Unit to use.
   * @return An EmbeddedServerFull instance.
   * @throws LifecycleException If the tomcat server failed to start.
   */
  public static EmbeddedServerFull start(String warPath, int port, String contextPath, String pathToAlternativeWeb_xml)
          throws LifecycleException {
    //Important: only use the tmpdir if this server is mean for testing, tmpdirs could be deleted, and in diffent ways , depending on OS
    //In a test, this folder should be deleted, after tests has executed
    //final String tmp_dir = System.getProperty("java.io.tmpdir") + "EMB_TOMCAT" + UUID.randomUUID().getLeastSignificantBits();
    /*
    I could not make this class delete the random temp directory (see above - files was locked, even after the Destroy event)
    So the class is changed to allow only one server instance running.
    This allows to delete the folder, before each start (if created)
    */
    if(server != null){
      throw new LifecycleException("A server is already started, this class only allows one server running!");
    }
    final String tmp_dir = System.getProperty("java.io.tmpdir") + "EMB_TOMCAT";
    try {
      FileUtils.deleteDirectory(new File(tmp_dir));
    } catch (IOException ex) {
      Logger.getLogger(EmbeddedServerFull.class.getName()).log(Level.SEVERE, null, ex);
    }
    final Tomcat tomcat = new Tomcat();
    tomcat.setPort(port);
    tomcat.setBaseDir(tmp_dir);
    tomcat.getHost().setAppBase(tmp_dir);
    tomcat.getHost().setAutoDeploy(true);
    tomcat.getHost().setDeployOnStartup(true);

    try {
      tomcat.start();
    } catch (LifecycleException ex) {
      LOGGER.log(Level.SEVERE, "Failed to start Tomcat: {0}", ex.getMessage());
      throw ex;
    }

    LOGGER.log(Level.INFO, "Tomcat started on {0}", tomcat.getHost());

    Context appContext = tomcat.addWebapp(tomcat.getHost(), contextPath, warPath);
    //pathToAlternativeWeb_xml = System.getProperty("user.dir")+"\\src\\test\\java\\test\\webForTesting.xml";
    if (pathToAlternativeWeb_xml != null) {
      appContext.setAltDDName(pathToAlternativeWeb_xml);
    }

    LOGGER.log(Level.INFO, "Deployed {0} as {1}",
            new Object[]{appContext.getBaseName(), appContext.getBaseName()});

    // This line blocks until the server is stopped, DO NOT UMCOMMENT WHEN USED WITH A TEST FRAMEWORK (SELINIUM)
    // tomcat.getServer().await();
    // Return a new EmbeddedServerFull instance
    return new EmbeddedServerFull(tomcat.getServer(), tmp_dir);
  }

}
