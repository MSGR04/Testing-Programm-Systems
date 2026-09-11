package ru.ostrovok.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.ostrovok.pages.MainPage;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UC-10: Переход на внешний сайт corp.ostrovok.ru")
class UC10CorpSiteTest extends BaseTest {

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("UC-10: Клик «Для командировок» открывает корпоративный сайт")
    void testBusinessTravelOpensCorpSite(String browser) {
        setUp(browser);

        String originalWindow = driver.getWindowHandle();
        int windowsBefore = driver.getWindowHandles().size();

        new MainPage(driver)
                .open()
                .clickBusinessTravel();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
        }

        Set<String> handles = driver.getWindowHandles();
        if (handles.size() > windowsBefore) {
            for (String handle : handles) {
                if (!handle.equals(originalWindow)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }
        }

        String currentUrl = driver.getCurrentUrl();
        assertTrue(
                currentUrl.contains("corp.ostrovok") ||
                        currentUrl.contains("business") ||
                        currentUrl.contains("corp") ||
                        currentUrl.contains("lastminute"),
                "После клика «Для командировок» должен открыться корпоративный сайт. " +
                        "Текущий URL: " + currentUrl
        );
    }
}
