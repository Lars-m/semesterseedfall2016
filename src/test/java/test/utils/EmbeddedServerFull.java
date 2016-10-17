package test.utils;

import java.nio.file.Path;
import java.util.logging.Level;

import java.util.logging.Logger;

import org.apache.catalina.Context;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;

import org.apache.catalina.startup.Tomcat;

/**
 * Example that starts an embedded Tomcat (in this case from within a
 * web-project, since purpose eventually will be testing a such),
 *
 * and deploys a war file including the web-project.
 *
 * If the port is set to '8888' and the context path to '/app', test via:
 *
 * http://localhost:8888/app To test the single web-page
 *
 * http://localhost:8888/app/api/hello To test the single (GET) REST service
 *
 * @author lam
 */
public class EmbeddedServerFull implements AutoCloseable {

    private final static Logger LOGGER = Logger.getLogger(EmbeddedServerFull.class.getName());
    //Important: only use the tmpdir is this server is mean for testing, tmpdirs could be deleted, and in diffent ways , depending on OS
    //In a test, this folder should be deleted, after tests has executed
    private final static String TMP_DIR = System.getProperty("java.io.tmpdir");

    private final Server server;

    private EmbeddedServerFull(Server server) {
        this.server = server;
    }

    /**
     * Shuts down the server.
     *
     * @throws LifecycleException If the server failed to shut down.
     */
    @Override
    public void close() throws LifecycleException {
        server.stop();
    }

    /**
     * Starts a new embedded Tomcat server.
     *
     * @param warPath The path to the tomcat application .war file.
     * @param port The port to expose the server on. Example: 8888
     * @param contextPath The URL path for the context application. Example:
     * /app.
     * @return An EmbeddedServerFull instance.
     * @throws LifecycleException If the tomcat server failed to start.
     */
    public static EmbeddedServerFull start(String warPath, int port, String contextPath)
            throws LifecycleException {
        final Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(TMP_DIR);
        tomcat.getHost().setAppBase(TMP_DIR);
        tomcat.getHost().setAutoDeploy(true);
        tomcat.getHost().setDeployOnStartup(true);

        try {
            tomcat.start();
        } catch (LifecycleException ex) {
            LOGGER.log(Level.SEVERE, "Failed to start Tomcat: {0}", ex.getMessage());
            throw ex;
        }

        LOGGER.log(Level.INFO, "Tomcat started on {0}", tomcat.getHost());

        final Context appContext = tomcat.addWebapp(tomcat.getHost(), contextPath, warPath);
        LOGGER.log(Level.INFO, "Deployed {0} as {1}",
                new Object[]{appContext.getBaseName(), appContext.getBaseName()});

        // This line blocks until the server is stopped
        // tomcat.getServer().await();
        // Return a new EmbeddedServerFull instance
        return new EmbeddedServerFull(tomcat.getServer());
    }

}
