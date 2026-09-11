package ru.ostrovok.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.stream.Collectors;

public class TransfersPage extends BasePage {

    private final By roundtripSwitch = By.xpath("//*[@data-testid='roundtrip_switch']");
    private final By fromField = By.xpath("//*[@data-testid='transfer-destination-from']");
    private final By toField = By.xpath("//*[@data-testid='transfer-destination-to']");
    private final By dateFields = By.xpath("//*[@data-testid='date-start-input']");
    private final By timeFields = By.xpath("//input[@data-testid='time-select-input']");
    private final By searchButton =
            By.xpath("//div[contains(@class,'TransfersSearchForm_submitButton')]//button");

    public String getFromValue() {
        return waitVisible(fromField).getText().trim();
    }

    public String getToValue() {
        return waitVisible(toField).getText().trim();
    }



    public TransfersPage(WebDriver driver) {
        super(driver);
    }

    public TransfersPage waitForLoad() {
        waitVisible(fromField);
        dismissAdOverlays();
        return this;
    }

    public TransferResultsPage waitForResults() {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(ExpectedConditions.urlContains("/transfers/"));
        return new TransferResultsPage(driver);
    }

    public boolean isFormLoaded() {
        return !driver.findElements(fromField).isEmpty()
                && !driver.findElements(toField).isEmpty();
    }

    public TransfersPage selectRoundTrip() {
        dismissAdOverlays();
        clickWhenClickable(roundtripSwitch);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.findElements(dateFields).size() >= 2);
        return this;
    }

    public boolean isRoundTripSelected() {
        return driver.findElements(dateFields).size() >= 2
                && driver.findElements(timeFields).size() >= 2;
    }

    public TransfersPage setFrom(String query, String suggestionText) {
        fillDestination(fromField, query, suggestionText);
        return this;
    }


    public TransfersPage setTo(String query, String suggestionText) {
        fillDestination(toField, query, suggestionText);
        return this;
    }

    public TransfersPage clickSearch() {
        dismissAdOverlays();
        String startUrl = driver.getCurrentUrl();

        for (int attempt = 1; attempt <= 3; attempt++) {
            ((JavascriptExecutor) driver).executeScript("document.activeElement.blur();");

            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.elementToBeClickable(searchButton));
            scrollTo(btn);

            try {
                btn.click();
            } catch (TimeoutException e) {
                System.out.println("Переход запущен, страница грузится дольше лимита");
                return this;
            } catch (ElementClickInterceptedException e) {
                dismissAdOverlays();
                continue;
            }

            try {
                new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(d -> !d.getCurrentUrl().equals(startUrl));
                return this;
            } catch (TimeoutException e) {
                System.out.println("Клик №" + attempt + " не запустил поиск, повторяю");
            }
        }

        throw new IllegalStateException(
                "Кнопка «Найти» не запустила поиск за 3 попытки. Откуда: [" + getFromValue() +
                        "], Куда: [" + getToValue() + "]");
    }

    public boolean isSearchStarted() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.urlContains("/transfers/"));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void fillDestination(By field, String query, String suggestionKey) {
        By allSuggestions = By.xpath("//div[contains(@class,'SuggestOption_item')]");
        By target = By.xpath("//div[contains(@class,'SuggestOption_item')]" +
                "[contains(., '" + suggestionKey + "')]");

        for (int attempt = 1; attempt <= 3; attempt++) {
            dismissAdOverlays();
            WebElement box = waitVisible(field);
            scrollTo(box);
            box.click();

            WebElement input = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> {
                        WebElement active = d.switchTo().activeElement();
                        return "input".equalsIgnoreCase(active.getTagName()) ? active : null;
                    });
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
            input.sendKeys(query);

            try {
                new WebDriverWait(driver, Duration.ofSeconds(15))
                        .until(ExpectedConditions.elementToBeClickable(target))
                        .click();
            } catch (TimeoutException e) {
                String available = driver.findElements(allSuggestions).stream()
                        .map(el -> el.getText().replace('\n', ' '))
                        .collect(Collectors.joining(" | "));
                throw new IllegalStateException(
                        "Подсказка с «" + suggestionKey + "» не найдена. Сайт предложил: " + available);
            }

            String value = waitVisible(field).getText().trim();
            if (value.toLowerCase().contains(suggestionKey.toLowerCase())) {
                ((JavascriptExecutor) driver).executeScript("document.activeElement.blur();");
                return;
            }
            System.out.println("Поле не заполнилось с попытки " + attempt + ", в нём: [" + value + "]");
        }

        throw new IllegalStateException(
                "Не удалось заполнить поле значением «" + suggestionKey + "» за 3 попытки");
    }
}