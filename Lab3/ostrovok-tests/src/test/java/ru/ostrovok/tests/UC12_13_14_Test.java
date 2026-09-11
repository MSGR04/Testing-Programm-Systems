package ru.ostrovok.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.ostrovok.pages.MainPage;
import ru.ostrovok.pages.SearchResultsPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UC-12/13/14: Фильтры по оценке, отмене и названию")
class UC12_13_14_Test extends BaseTest {

    private SearchResultsPage openResults(String browser) {
        setUp(browser);
        return new MainPage(driver)
                .open()
                .enterDestination("Москва")
                .selectFirstSuggestion()
                .clickSearch()
                .waitForResults();
    }

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("UC-12: Фильтр «8 и выше» — все оценки не ниже 8,0")
    void testRatingEightPlusFilter(String browser) {
        SearchResultsPage page = openResults(browser).applyRatingEightPlusFilter();

        List<Double> ratings = page.getRatings();

        assertFalse(ratings.isEmpty(), "Должны остаться отели с оценками");
        assertTrue(ratings.stream().allMatch(r -> r >= 8.0),
                "Все оценки должны быть не ниже 8,0, получено: " + ratings);
    }

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("UC-13: Фильтр «Есть бесплатная отмена» — признак на каждой карточке")
    void testFreeCancellationFilter(String browser) {
        SearchResultsPage page = openResults(browser).applyFreeCancellationFilter();

        int cards = page.getHotelCount();
        int withBadge = page.getCardsWithFreeCancellationCount();

        assertTrue(cards > 0, "После фильтра должны остаться результаты");
        assertEquals(cards, withBadge,
                "У каждой карточки должен быть признак «Беспл. отмена»");
    }

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("UC-14: Поиск по названию «Radisson» — выдача сужается")
    void testFilterByHotelName(String browser) {
        SearchResultsPage page = openResults(browser);
        int before = page.getTotalFoundCount();

        page.filterByHotelName("Radisson");
        int after = page.getTotalFoundCount();

        assertTrue(after > 0, "Отели с таким названием должны найтись");
        assertTrue(after < before,
                "Выдача должна сузиться: было " + before + ", стало " + after);
        assertTrue(page.getHotelNames().stream()
                        .anyMatch(n -> n.toLowerCase().contains("radisson")),
                "В выдаче должен быть хотя бы один отель Radisson");
    }
}