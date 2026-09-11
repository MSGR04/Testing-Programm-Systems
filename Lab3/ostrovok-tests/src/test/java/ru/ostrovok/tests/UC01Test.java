package ru.ostrovok.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.ostrovok.pages.HotelPage;
import ru.ostrovok.pages.MainPage;
import ru.ostrovok.pages.SearchResultsPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UC-01: Поиск жилья")
class UC01Test extends BaseTest {

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("Поиск отелей — результаты загружаются")
    void testSearchShowsResults(String browser) {
        setUp(browser);

        SearchResultsPage results = new MainPage(driver)
                .open()
                .enterDestination("Москва")
                .selectFirstSuggestion()
                .clickSearch()
                .waitForResults();

        assertTrue(results.hasResults(),
                "После поиска должны появиться карточки отелей");
    }

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("Открытие карточки отеля из результатов")
    void testOpenHotelCard(String browser) {
        setUp(browser);

        HotelPage hotel = new MainPage(driver)
                .open()
                .enterDestination("Москва")
                .selectFirstSuggestion()
                .clickSearch()
                .waitForResults()
                .openFirstHotel();

        hotel.waitForLoad();

        assertTrue(hotel.isLoaded(),
                "Страница отеля должна открыться");
        assertFalse(hotel.getHotelName().isBlank(),
                "Название отеля не должно быть пустым");
    }
}