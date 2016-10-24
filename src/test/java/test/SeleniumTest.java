package test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.util.List;
import org.apache.catalina.LifecycleException;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import test.utils.EmbeddedServerFull;
import test.utils.SeleniumUtil;

/**
 * Tests the HTML pages using the Selenium headless HtmlUnit driver.
 *
 * @author jegp <jeep@cphbusiness.dk>
 */
public class SeleniumTest {

    private static final int SERVER_PORT = 8888;
    private static final String APP_CONTEXT = "/app";
    private static final String BASE_URL = "http://localhost:" + SERVER_PORT + APP_CONTEXT;
    private static final String USER_DIR = System.getProperty("user.dir");

    private static HtmlUnitDriver driver;
    private static EmbeddedServerFull server;

    @BeforeClass
    public static void setUpBeforeAll() throws LifecycleException {
        // Start a server with the given .war file which runs on port 888 and
        // exposes the server on the URL path /app
        server = EmbeddedServerFull
                .start(USER_DIR + "/target/seedMaven-1.0-SNAPSHOT.war",
                        8888, "/app");
        // Start the Selenium framework and pretend we're Firefox
        // If we don't, Angular will fallback to IE and fail
        driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_38);
        // Enable JavaScript
        driver.setJavascriptEnabled(true);
    }

    @Test
    public void testBrowser() {
        // Access the url and assert that the title is as expected
        driver.get(BASE_URL);
        System.out.println(driver.findElementByName("body"));
        assertEquals("My AngularJS App", driver.getTitle());
    }

    @Test
    public void testLogin() throws InterruptedException {
        driver.get(BASE_URL);
        // Get the login input fields
        List<WebElement> loginInputs = driver.findElementsByTagName("input");
        // Insert data into the fields (user and password)
        loginInputs.get(0).sendKeys("admin");
        loginInputs.get(1).sendKeys("test");

        // Fetch the login form and submit
        driver.findElementByTagName("form").submit();

        // Test that the user is logged in
        // - This block uses a fluentWait which polls the website once in a 
        //     while, to allow it time to update.
        boolean isLoggedIn = SeleniumUtil.waitUntilPageContains(driver, "Logged on as: admin");
        assertTrue(isLoggedIn);
    }

    @AfterClass
    public static void close() throws LifecycleException {
        server.close();
        driver.close();
    }

}