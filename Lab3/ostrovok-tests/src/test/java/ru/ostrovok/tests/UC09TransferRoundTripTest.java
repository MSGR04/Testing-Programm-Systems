package ru.ostrovok.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.ostrovok.pages.MainPage;
import ru.ostrovok.pages.TransfersPage;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UC-09: Трансфер туда-обратно")
class UC09TransferRoundTripTest extends BaseTest {

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
    @DisplayName("UC-09: Форма трансфера загружается")
    void testTransferFormLoaded(String browser) {
        TransfersPage page = openTransfers(browser);
        assertTrue(page.isFormLoaded(),
                "Форма поиска трансфера должна содержать поля отправления и назначения");
    }

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("UC-09: Поиск трансфера туда-обратно запускается")
    void testRoundTripSearch(String browser) {
        TransfersPage page = openTransfers(browser);

        page.selectRoundTrip();
        assertTrue(page.isRoundTripSelected(),
                "В режиме «туда-обратно» должны появиться поля даты и времени обратной подачи");

        page.setFrom("Шереметьево", "Шереметьево")
                .setTo("Внуково", "Внуково");

        assertNotEquals(page.getFromValue(), page.getToValue(),
                "Пункты отправления и назначения должны различаться");

        page.clickSearch();

        assertTrue(page.isSearchStarted(),
                "Должна открыться страница подбора трансферов");
    }

}
