package ru.ostrovok.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransferResultsPage extends BasePage {

    private final By offerCard = By.cssSelector("[data-testid='choose_button']");
    private final By seatsBadge = By.cssSelector("[data-testid='capacity-seats']");
    private final By luggageBadge = By.cssSelector("[data-testid='capacity-luggage']");

    private final By luggageFilter = By.cssSelector("[data-testid='transfers_luggage_places_filter']");
    private final By seatsFilter = By.cssSelector("[data-testid='transfers_seats_filter']");

    private static final By THUMB = By.cssSelector("[role='slider']");

    public TransferResultsPage(WebDriver driver) {
        super(driver);
    }

    public TransferResultsPage waitForOffers() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(120))
                    .until(d -> !d.findElements(offerCard).isEmpty());
        } catch (TimeoutException e) {
            String body = driver.findElement(By.tagName("body")).getText().replace('\n', ' ');
            throw new IllegalStateException("Предложения не появились за 120 с. Страница: "
                    + body.substring(0, Math.min(300, body.length())));
        }
        dismissAdOverlays();
        return this;
    }

    public int getOfferCount() {
        return driver.findElements(offerCard).size();
    }

    public List<Integer> getOfferSeats() {
        return readCapacities(seatsBadge);
    }

    public List<Integer> getOfferLuggage() {
        return readCapacities(luggageBadge);
    }

    private List<Integer> readCapacities(By badge) {
        return driver.findElements(badge).stream()
                .map(WebElement::getText)
                .map(t -> t.replaceAll("\\D+", ""))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public TransferResultsPage setLuggageRange(int from, int to) {
        setRange(luggageFilter, from, to);
        return this;
    }

    public TransferResultsPage setSeatsRange(int from, int to) {
        setRange(seatsFilter, from, to);
        return this;
    }

    private void setRange(By filterContainer, int from, int to) {
        WebElement container = waitVisible(filterContainer);
        scrollTo(container);

        List<WebElement> thumbs = container.findElements(THUMB);
        if (thumbs.size() < 2) {
            throw new IllegalStateException(
                    "Ожидались два ползунка, найдено: " + thumbs.size());
        }
        moveThumb(thumbs.get(1), to);
        moveThumb(thumbs.get(0), from);
    }

    private void moveThumb(WebElement thumb, int target) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", thumb);

        int current = valueOf(thumb);
        int guard = Math.abs(target - current) + 10;

        while (current != target && guard-- > 0) {
            new Actions(driver)
                    .sendKeys(target > current ? Keys.ARROW_RIGHT : Keys.ARROW_LEFT)
                    .perform();

            int previous = current;
            current = valueOf(thumb);
            if (current == previous) break;
        }

        if (valueOf(thumb) != target) {
            throw new IllegalStateException(
                    "Не удалось выставить " + target + ": ползунок остановился на " + valueOf(thumb) +
                            " (допустимый диапазон " + thumb.getAttribute("aria-valuemin") +
                            "–" + thumb.getAttribute("aria-valuemax") + ")");
        }
    }

    private int valueOf(WebElement thumb) {
        return Integer.parseInt(thumb.getAttribute("aria-valuenow"));
    }


    public TransferResultsPage waitForSeatsWithin(int from, int to) {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> {
                    List<Integer> seats = getOfferSeats();
                    return !seats.isEmpty()
                            && seats.stream().allMatch(s -> s >= from && s <= to);
                });
        return this;
    }
}