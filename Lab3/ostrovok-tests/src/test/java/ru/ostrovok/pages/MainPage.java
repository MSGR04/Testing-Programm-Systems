package ru.ostrovok.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class MainPage extends BasePage {
    public static final String URL = "https://ostrovok.ru";
    private final By checkInInput =
            By.xpath("//div[@data-testid='date-start-input']");


    private final By datepickerCalendar =
            By.xpath("//div[@data-testid='datepicker-calendar']");
    private static final DateTimeFormatter DAY_KEY =
            DateTimeFormatter.ofPattern("EEE MMM dd yyyy", Locale.ENGLISH);


    private void pickDay(LocalDate date) {
        String key = date.format(DAY_KEY);
        By day = By.xpath(
                "//div[starts-with(@data-day,'" + key + "')]" +
                        "[contains(@class,'Day_inner')]" +
                        "[not(contains(@class,'Day_inner_locked'))]");
        clickWhenClickable(day);
    }


    public MainPage selectDates() {
        return selectDates(7, 2);
    }

    public MainPage selectDates(int daysAhead, int nights) {
        LocalDate checkIn = LocalDate.now().plusDays(daysAhead);
        clickWhenClickable(checkInInput);
        waitVisible(datepickerCalendar);
        pickDay(checkIn);
        pickDay(checkIn.plusDays(nights));
        return this;
    }

    private final By destinationInput =
            By.xpath("//input[@data-testid='destination-input']");

    private final By firstSuggestion =
            By.xpath("(//div[contains(@class,'Suggest-module__destination') or " +
                    "contains(@class,'Suggest_destination')])[1]");

    private final By searchButton =
            By.xpath("//button[@data-testid='search-button']");

    private final By cookieButton =
            By.xpath("//button[.//div[text()='Хорошо']]");

    private final By apartmentsPopupClose =
            By.xpath("//*[contains(@class,'ApartmentsFilter') and contains(@class,'cross')]");


    public TransfersPage clickTransfers() {
        closeApartmentsPopup();

        By transfersButton = By.xpath(
                "//button[contains(@class,'Tabs_tab') and contains(.,'Трансферы')] | " +
                        "//div[contains(@class,'Tabs-module__control') and contains(.,'Трансферы')]"
        );
        By fromField = By.xpath("//*[@data-testid='transfer-destination-from']");

        for (int attempt = 0; attempt < 10; attempt++) {
            WebElement btn = waitPresent(transfersButton);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", btn);

            try {
                new WebDriverWait(driver, Duration.ofSeconds(3))
                        .until(ExpectedConditions.visibilityOfElementLocated(fromField));
                return new TransfersPage(driver);
            } catch (TimeoutException e) {
                // не открылась — пробуем ещё
            }
        }
        throw new IllegalStateException("Форма трансферов не открылась после 10 попыток");
    }

    public MainPage(WebDriver driver) {
        super(driver);
    }

    public MainPage open() {
        try {
            driver.get(URL);
        } catch (org.openqa.selenium.TimeoutException e) {
            System.out.println("Предупреждение: страница догружалась дольше лимита, продолжаем");
        }
        System.out.println("Текущий URL: " + driver.getCurrentUrl());
        System.out.println("Заголовок страницы: " + driver.getTitle());
        closeCookieBanner();
        closeApartmentsPopup();
        waitVisible(destinationInput);
        return this;
    }

    private void closeCookieBanner() {
        try {
            WebElement btn = wait
                    .until(d -> {
                        var els = d.findElements(cookieButton);
                        return els.isEmpty() ? null : els.get(0);
                    });
            btn.click();
        } catch (Exception e) {
            // попап не появился — идём дальше
        }
    }

    private void closeApartmentsPopup() {
        try {
            WebElement btn = wait
                    .until(d -> {
                        var els = d.findElements(apartmentsPopupClose);
                        return els.isEmpty() ? null : els.get(0);
                    });
            btn.click();
        } catch (Exception e) {
            // попап не появился — идём дальше
        }
    }

    private final By emptySuggest =
            By.xpath("//div[contains(@class,'EmptySuggest_emptySuggest')]");

    public MainPage enterDestination(String city) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            WebElement input = waitVisible(destinationInput);
            input.click();
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
            input.sendKeys(city);

            try {
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .ignoring(StaleElementReferenceException.class)
                        .until(d -> {
                            var elements = d.findElements(firstSuggestion);
                            return !elements.isEmpty() && elements.get(0).isDisplayed();
                        });
                return this;
            } catch (TimeoutException e) {
                if (!driver.findElements(emptySuggest).isEmpty()) {
                    throw new IllegalStateException(
                            "Сайт не знает направление «" + city + "» — проверьте тестовые данные");
                }
                System.out.println("Подсказки не появились, попытка " + attempt + " из 3");
            }
        }
        throw new IllegalStateException(
                "Подсказки для «" + city + "» не появились после 3 попыток");
    }

    public MainPage selectFirstSuggestion() {
        wait.ignoring(StaleElementReferenceException.class)
                .until(d -> {
                    var elements = d.findElements(firstSuggestion);
                    if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                        elements.get(0).click();
                        return true;
                    }
                    return false;
                });
        return this;
    }

    public SearchResultsPage clickSearch() {
        clickWhenClickable(searchButton);
        return new SearchResultsPage(driver);
    }

    // ── UC-10: Кнопка «Для командировок» ──────────────────────────────────────

    private final By businessTravelButton =
            By.xpath("//a[contains(.,'командировок') or contains(@href,'corp')] | " +
                    "//button[contains(.,'командировок')]");

    public void clickBusinessTravel() {
        clickWhenClickable(businessTravelButton);
    }

    // ── UC-11: Кнопка «Войти» ─────────────────────────────────────────────────

    private final By loginButton =
            By.xpath("//button[contains(.,'Войти') or contains(@data-testid,'login')] | " +
                    "//a[contains(.,'Войти')]");

}