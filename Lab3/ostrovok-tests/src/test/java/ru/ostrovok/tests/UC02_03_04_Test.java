package ru.ostrovok.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.ostrovok.pages.MainPage;
import ru.ostrovok.pages.SearchResultsPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UC-02/03/04: Фильтры и сортировка")
class UC02_03_04_Test extends BaseTest {
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
    @DisplayName("UC-02: Фильтр «3 звезды» — результаты обновляются")
    void testThreeStarsFilter(String browser) {
        SearchResultsPage page = openResults(browser);

        page.applyThreeStarsFilter();

        assertTrue(page.hasResults(),
                "После фильтра «3 звезды» должны быть результаты");
    }

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("UC-03: Фильтр «Тип жилья — Отели» — результаты обновляются")
    void testHotelTypeFilter(String browser) {
        SearchResultsPage page = openResults(browser);

        page.applyHotelTypeFilter();

        assertTrue(page.hasResults(),
                "После фильтра «Отели» должны быть результаты");
    }

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("UC-04: Сортировка «Сначала дешёвые» — результаты обновляются")
    void testSortByCheapest(String browser) {
        SearchResultsPage page = openResults(browser);

        page.sortByCheapest();

        assertTrue(page.hasResults(),
                "После сортировки по цене должны быть результаты");
    }
}