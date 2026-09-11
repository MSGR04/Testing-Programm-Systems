package ru.ostrovok.tests;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.ostrovok.pages.MainPage;
import ru.ostrovok.pages.TransferResultsPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UC-11: Фильтрация трансферов по вместимости")
class UC11TransferFiltersTest extends BaseTest {

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("UC-11: Фильтры багажа и мест сужают выдачу")
    void testCapacityFilters(String browser) {
        setUp(browser);

        TransferResultsPage results = new MainPage(driver)
                .open()
                .clickTransfers()
                .waitForLoad()
                .selectRoundTrip()
                .setTo("Домодедово", "Домодедово")
                .setFrom("Шереметьево", "Шереметьево")
                .clickSearch()
                .waitForResults()
                .waitForOffers();

        int before = results.getOfferCount();
        Assumptions.assumeTrue(before > 1,
                "По маршруту найдено меньше двух трансферов — фильтрацию проверить не на чем");
        assertTrue(before > 0, "Поиск должен вернуть хотя бы один трансфер");

        results.setLuggageRange(7, 14)
                .setSeatsRange(9, 13)
                .waitForSeatsWithin(9, 13);

        int after = results.getOfferCount();

        assertTrue(after > 0, "После фильтрации должны остаться варианты");
        assertTrue(after < before,
                "Выдача должна сузиться: было " + before + ", стало " + after);
        assertTrue(results.getOfferSeats().stream().allMatch(s -> s >= 9 && s <= 13),
                "Все предложения должны укладываться в 9–13 мест: " + results.getOfferSeats());
        assertTrue(results.getOfferLuggage().stream().allMatch(l -> l >= 7 && l <= 14),
                "Весь багаж должен укладываться в 7–14: " + results.getOfferLuggage());
    }
}