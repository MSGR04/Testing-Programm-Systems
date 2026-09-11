package ru.ostrovok.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class SearchResultsPage extends BasePage {

    private final By hotelCard =
            By.xpath("//div[@data-testid='serp-hotelcard']");

    private final By firstHotelLink =
            By.xpath("(//a[@data-testid='hotel-card-name'])[1]");

    private final By threeStarsCheckbox =
            By.xpath("//input[contains(@class,'Default_control') and @value='3']");

    private final By ratingEightPlus =
            By.xpath("//input[contains(@class,'InputRadio_input') and @value='8']");

    private final By hotelTypeCheckbox =
            By.xpath("(//input[contains(@class,'Default_control') and @value='hotel'])[1]");

    private final By firstFavoriteButton =
            By.xpath("(//div[@role='button'][.//div[contains(@class,'Favorite_root')]])[1]");
    private final By firstFavoriteHeart =
            By.xpath("(//div[contains(@class,'HotelGallery_favorite')]//div[contains(@class,'Favorite_root')])[1]");
    private final By favoriteToggle =
            By.xpath("//input[contains(@class,'Switch_control')]");

    private final By anyResultsHeading = By.xpath("//h1");
    private final By resultsCounterTitle =
            By.xpath("//h1[contains(@class,'ResultTitle_title')]");

    private void waitForResultsUpdate() {
        waitForResultsUpdate(null);
    }

    private void waitForResultsUpdate(String urlParam) {
        WebDriverWait slow = new WebDriverWait(driver, Duration.ofSeconds(45));
        if (urlParam != null) {
            slow.until(ExpectedConditions.urlContains(urlParam));
        }
        slow.until(d -> {
            var h = d.findElements(anyResultsHeading);
            return !h.isEmpty() && !h.get(0).getText().contains("Ищем");
        });
        slow.until(d -> !d.findElements(hotelCard).isEmpty());
    }

    private void clickViaJs(WebElement el) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", el);
    }

    // UC-05:
    public SearchResultsPage addFirstHotelToFavorites() {
        closeFeedbackPopup();
        WebElement btn = waitVisible(firstFavoriteButton);
        scrollTo(btn);
        btn.click();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d ->
                d.findElement(firstFavoriteHeart).getAttribute("class").contains("Favorite_root_active"));
        return this;
    }

    public boolean isFirstHotelInFavorites() {
        return waitVisible(firstFavoriteHeart).getAttribute("class").contains("Favorite_root_active");
    }

    public boolean isFirstHotelNotInFavorites() {
        return !waitVisible(firstFavoriteHeart).getAttribute("class").contains("Favorite_root_active");
    }

    private final By firstFavoriteActive =
            By.xpath("(//div[@role='button'][.//div[contains(@class,'Favorite_root_active')]])[1]");

    private final By firstFavoriteInactive =
            By.xpath("(//div[@role='button'][.//div[contains(@class,'Favorite_root') " +
                    "and not(contains(@class,'Favorite_root_active'))]])[1]");

    public SearchResultsPage removeFirstHotelFromFavorites() {
        closeFeedbackPopup();
        WebElement btn = waitVisible(firstFavoriteButton);
        scrollTo(btn);
        btn.click();
        return this;
    }

    private void toggleFavoriteFilter() {
        WebElement toggle = waitPresent(favoriteToggle);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", toggle);
    }

    public SearchResultsPage enableFavoritesFilter() {
        toggleFavoriteFilter();
        waitForResultsUpdate("favorites=true");
        return this;
    }

    private final By sortDropdown =
            By.xpath("//label[@data-testid='sort-filter-field-container']" +
                    "//div[contains(@class,'control')]");

    private final By sortCheapestOption =
            By.xpath("//div[contains(@class,'Option_option')][contains(., 'дешёв')]");

    private final By feedbackPopupClose =
            By.xpath("//button[contains(@class,'uxs-1Rt7dOrmXX')]");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public SearchResultsPage waitForResults() {
        waitVisible(hotelCard);
        return this;
    }

    private void closeFeedbackPopup() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfElementLocated(feedbackPopupClose));
            var els = driver.findElements(feedbackPopupClose);
            if (!els.isEmpty()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", els.get(0));
            }
        } catch (Exception ignored) {
            // попапа нет — идём дальше
        }
    }

    public boolean hasResults() {
        return !driver.findElements(hotelCard).isEmpty();
    }

    public int getHotelCount() {
        return driver.findElements(hotelCard).size();
    }

    public HotelPage openFirstHotel() {
        closeFeedbackPopup();

        String original = driver.getWindowHandle();
        Set<String> before = driver.getWindowHandles();

        WebElement link = waitVisible(firstHotelLink);
        scrollTo(link);
        link.click();

        wait.until(d -> d.getWindowHandles().size() > before.size());

        String newTab = driver.getWindowHandles().stream()
                .filter(h -> !before.contains(h))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Новая вкладка не открылась"));
        driver.switchTo().window(newTab);

        return new HotelPage(driver).setParentHandle(original);
    }

    // UC-02
    public SearchResultsPage applyThreeStarsFilter() {
        closeFeedbackPopup();
        WebElement checkbox = waitPresent(threeStarsCheckbox);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", checkbox);
        waitForResultsUpdate();
        return this;
    }

    // UC-03
    public SearchResultsPage applyHotelTypeFilter() {
        closeFeedbackPopup();
        WebElement checkbox = waitPresent(hotelTypeCheckbox);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", checkbox);
        waitForResultsUpdate();
        return this;
    }

    public SearchResultsPage applyRatingEightPlusFilter() {
        closeFeedbackPopup();
        clickViaJs(waitPresent(ratingEightPlus));
        waitForResultsUpdate("reviews_rating=8");
        return this;
    }

    public List<Double> getRatings() {
        return driver.findElements(By.cssSelector("[data-testid='hotel-card-rating']"))
                .stream()
                .map(e -> e.getText().trim().replace(',', '.'))
                .filter(s -> s.matches("\\d+(\\.\\d+)?"))
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }

    // UC-04

    public SearchResultsPage sortByCheapest() {
        closeFeedbackPopup();
        WebElement dropdown = wait.until(
                ExpectedConditions.elementToBeClickable(sortDropdown));
        scrollTo(dropdown);
        dropdown.click();

        WebElement option = wait.until(
                ExpectedConditions.visibilityOfElementLocated(sortCheapestOption));
        option.click();

        waitForResultsUpdate();
        return this;
    }

    // UC-06
    private final By viewPricesButton =
            By.xpath("(//button[contains(.,'Показать все номера')])[1]");

    public HotelPage clickViewPrices() {
        closeFeedbackPopup();
        clickWhenClickable(viewPricesButton);
        return new HotelPage(driver);
    }

    // ── UC-13: Бесплатная отмена ──────────────────────────────────────────────
    private final By freeCancellation =
            By.xpath("//input[contains(@class,'Default_control') and @value='freecancellation']");

    public SearchResultsPage applyFreeCancellationFilter() {
        closeFeedbackPopup();
        clickViaJs(waitPresent(freeCancellation));
        waitForResultsUpdate("payment=freecancellation");
        return this;
    }

    public int getCardsWithFreeCancellationCount() {
        return driver.findElements(
                By.xpath("[data-testid='cancellation-value-add']")).size();
    }

    // ── UC-14: Название отеля ─────────────────────────────────────────────────
    private final By hotelNameInput =
            By.xpath("//input[@placeholder='Например, Hilton']");

    public SearchResultsPage filterByHotelName(String name) {
        closeFeedbackPopup();
        WebElement input = waitVisible(hotelNameInput);
        scrollTo(input);
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        input.sendKeys(name);
        waitForResultsUpdate("name=" + name);
        return this;
    }

    public List<String> getHotelNames() {
        return driver.findElements(By.cssSelector("[data-testid='hotel-card-name']"))
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public int getTotalFoundCount() {
        Integer count = new WebDriverWait(driver, Duration.ofSeconds(45))
                .ignoring(StaleElementReferenceException.class)
                .until(d -> {
                    var els = d.findElements(resultsCounterTitle);
                    if (els.isEmpty()) return null;
                    String text = els.get(0).getText()
                            .replaceAll("[\\s\\u00A0\\u202F]", "");
                    Matcher m = Pattern.compile("(\\d+)").matcher(text);
                    return m.find() ? Integer.valueOf(m.group(1)) : null;
                });

        return Objects.requireNonNull(count, "Не удалось прочитать счётчик результатов");
    }
}