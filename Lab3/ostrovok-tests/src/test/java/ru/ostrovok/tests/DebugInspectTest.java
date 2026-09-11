package ru.ostrovok.tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import ru.ostrovok.driver.DriverFactory;

class DebugInspectTest {

    @Test
    void dumpDom() throws InterruptedException {
        var driver = DriverFactory.create("chrome");
        try {
            driver.get("https://ostrovok.ru");
            Thread.sleep(8000);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            Object testids = js.executeScript(
                    "return [...new Set([...document.querySelectorAll('[data-testid]')]" +
                            ".map(e=>e.getAttribute('data-testid')))].join('\\n');");
            System.out.println("=== DATA-TESTID ===\n" + testids);

            Object inputs = js.executeScript(
                    "return [...document.querySelectorAll('input')].map(e=>" +
                            "'name='+e.name+' ph='+e.placeholder+' testid='+e.getAttribute('data-testid')+' type='+e.type).join('\\n');");
            System.out.println("=== INPUTS ===\n" + inputs);

            Object tabs = js.executeScript(
                    "return [...document.querySelectorAll('button,a,[role=tab]')]" +
                            ".map(e=>e.innerText.trim()).filter(t=>t&&t.length<40).slice(0,80).join(' | ');");
            System.out.println("=== BUTTONS/TABS ===\n" + tabs);
        } finally {
            driver.quit();
        }
    }
}
