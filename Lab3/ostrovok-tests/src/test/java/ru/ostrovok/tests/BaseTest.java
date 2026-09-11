package ru.ostrovok.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.provider.Arguments;
import org.openqa.selenium.WebDriver;
import ru.ostrovok.driver.DriverFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

public abstract class BaseTest {

    protected WebDriver driver;

    public static Stream<Arguments> browsers() throws IOException {
        Properties props = new Properties();
        try (InputStream is = BaseTest.class
                .getClassLoader()
                .getResourceAsStream("browsers.properties")) {
            props.load(is);
        }
        String browser = System.getProperty("browser",
                props.getProperty("browser", "chrome")).toLowerCase();

        if (browser.equals("all")) {
            return Stream.of(of("chrome"), of("firefox"));
        }
        return Stream.of(of(browser));
    }


    protected void setUp(String browser) {
        driver = DriverFactory.create(browser);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
