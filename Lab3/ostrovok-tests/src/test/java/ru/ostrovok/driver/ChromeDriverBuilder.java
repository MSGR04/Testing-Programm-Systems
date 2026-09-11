package ru.ostrovok.driver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverBuilder {

    public ChromeDriver build(ChromeOptions options) {
        return new ChromeDriver(options);
    }
}