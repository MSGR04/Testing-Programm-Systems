package ru.ostrovok.driver;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.time.Duration;

public class DriverFactory {

    private static final Dimension WINDOW = new Dimension(1600, 1000);

    public static WebDriver create(String browser) {
        WebDriver driver = browser.equals("firefox")
                ? createFirefox()
                : createChrome();

        driver.manage().window().setSize(WINDOW);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        return driver;
    }

    private static WebDriver createChrome() {
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--disable-geolocation");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return new ChromeDriverBuilder().build(options);
    }

    private static WebDriver createFirefox() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.privatebrowsing.autostart", true);
        profile.setPreference("geo.enabled", false);
        profile.setPreference("permissions.default.geo", 2);
        profile.setPreference("permissions.default.desktop-notification", 2);

        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setProfile(profile);
        return new FirefoxDriver(options);
    }
}