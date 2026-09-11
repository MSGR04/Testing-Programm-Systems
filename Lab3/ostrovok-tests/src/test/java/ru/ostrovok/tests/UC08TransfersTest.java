package ru.ostrovok.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.ostrovok.pages.MainPage;
import ru.ostrovok.pages.TransfersPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UC-08: Трансферы")
class UC08TransfersTest extends BaseTest {

    private TransfersPage openTransfers(String browser) {
        setUp(browser);
        return new MainPage(driver)
                .open()
                .clickTransfers()
                .waitForLoad();
    }

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("UC-08: Поиск трансфера — форма открылась")
    void testTransferSearch(String browser) {
        TransfersPage page = openTransfers(browser);
        assertTrue(page.isFormLoaded(),
                "Форма поиска трансфера должна быть загружена");
    }

}