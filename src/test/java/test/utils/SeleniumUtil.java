package test.utils;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;

/**
 * Utilities for the Selenium framework.
 *
 * @author jens <jeep@cphbusiness.dk>
 */
public class SeleniumUtil {

    
    private SeleniumUtil() {
        // Should not be instantiated
    }

    /**
     * Waits 10 seconds until the test function gives the expected output. If it
     * does not return a value within 10 seconds, an exception is thrown.
     *
     * @param <T> The expected output.
     * @param driver The webdriver context running the website to test.
     * @param testFunction The test function which fetches a value from the
     * webdriver.
     * @return A value from the test function.
     */
    public static final <T> T fluentWait(final WebDriver driver,
            final Function<WebDriver, T> testFunction) {
        return new FluentWait<>(driver)
                .withTimeout(10, TimeUnit.SECONDS)
                .pollingEvery(500, TimeUnit.MILLISECONDS)
                .until(toGoogleFunction(testFunction));
    }

    /**
     * Waits for at most 10 seconds for the page to contain the given keyword.
     *
     * @param driver The driver running the webpage.
     * @param keyword The keyword to look for in the HTML page source.
     * @return <code>true</code> if the keyword could be found,
     * <code>false</code> otherwise.
     */
    public static final boolean waitUntilPageContains(WebDriver driver, String keyword) {
        try {
            return fluentWait(driver, d -> d.getPageSource().contains(keyword));
        } catch (TimeoutException timeout) {
            return false;
        }
    }

    /**
     * Converts a java 8 function to a Goole function.
     *
     * @param <T> The input parameter type.
     * @param <R> The output type.
     * @param function The function to convert.
     * @return A new google function.
     */
    private static <T, R> com.google.common.base.Function<T, R>
            toGoogleFunction(final Function<T, R> function) {
        return (final T input) -> function.apply(input);
    }

}