package ru.ostrovok.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.ostrovok.pages.MainPage;
import ru.ostrovok.pages.SearchResultsPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UC-05: Работа с избранным")
class UC05_FavoriteTest extends BaseTest {

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
    @DisplayName("UC-05: Добавление и удаление отеля из избранного")
    void testFavorite(String browser) {
        SearchResultsPage page = openResults(browser);

        // Шаг 1 — добавляем в избранное
        page.addFirstHotelToFavorites();

        // Шаг 2 — проверяем что сердечко стало активным
        assertTrue(page.isFirstHotelInFavorites(),
                "После добавления сердечко должно быть активным");

        // Шаг 3 — включаем фильтр «Избранное» и проверяем что отель есть
        page.enableFavoritesFilter();
        assertTrue(page.getHotelCount() > 0,
                "После добавления в избранное фильтр должен показывать результаты");

        // Шаг 4 — удаляем из избранного
        page.removeFirstHotelFromFavorites();

        // Шаг 5 — проверяем что сердечко стало неактивным
        assertTrue(page.isFirstHotelNotInFavorites(),
                "После удаления сердечко должно быть неактивным");
    }
}