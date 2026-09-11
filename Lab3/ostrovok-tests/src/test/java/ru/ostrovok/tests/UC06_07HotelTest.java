package ru.ostrovok.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.ostrovok.pages.HotelPage;
import ru.ostrovok.pages.MainPage;
import ru.ostrovok.pages.SearchResultsPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UC-06/07: Бронирование и отзывы")
class UC06_07HotelTest extends BaseTest {

    private SearchResultsPage openResults(String browser) {
        setUp(browser);
        return new MainPage(driver)
                .open()
                .enterDestination("Москва")
                .selectFirstSuggestion()
                .selectDates()
                .clickSearch()
                .waitForResults();
    }

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("UC-06: Переход к бронированию отеля")
    void testBooking(String browser) {
        SearchResultsPage results = openResults(browser);
        String originalHandle = driver.getWindowHandle();
        results.clickViewPrices();

        HotelPage hotelPage = new HotelPage(driver)
                .switchToNewTab(originalHandle)
                .waitForLoad();

        assertTrue(hotelPage.isBookButtonVisible(),
                "Кнопка «Забронировать» должна быть видна на странице отеля");
    }

    @ParameterizedTest(name = "Браузер: {0}")
    @MethodSource("browsers")
    @BrowserTest
    @DisplayName("UC-07: Просмотр отзывов об отеле")
    void testReviews(String browser) {
        SearchResultsPage results = openResults(browser);
        String originalHandle = driver.getWindowHandle();

        results.clickViewPrices();

        HotelPage hotelPage = new HotelPage(driver)
                .switchToNewTab(originalHandle)
                .waitForLoad();

        // Проверяем что кнопка «Читать все отзывы» видна и кликаем
        assertTrue(hotelPage.isReviewsButtonVisible(),
                "Кнопка «Читать все отзывы» должна быть видна");

        hotelPage.clickReviews();

        // Проверяем что URL изменился (прокрутка к отзывам или переход)
        assertTrue(driver.getCurrentUrl().contains("review") ||
                        driver.getCurrentUrl().contains("ostrovok"),
                "После клика должны быть на странице с отзывами");
    }
}