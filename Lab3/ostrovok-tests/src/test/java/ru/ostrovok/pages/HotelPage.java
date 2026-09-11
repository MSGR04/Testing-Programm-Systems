package ru.ostrovok.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HotelPage extends BasePage {
    private String parentHandle;
    private final By hotelTitle =
            By.xpath("//h1[contains(@class,'DesktopHeader_name')]");

    private final By bookButton =
            By.xpath("//a[contains(@class,'Booking_button')]");
    private final By reviewsButton =
            By.xpath("//button[@data-testid='rating-anchor-to-reviews']");

    public HotelPage(WebDriver driver) {
        super(driver);
    }

    public HotelPage switchToNewTab(String originalHandle) {
        wait.until(d -> d.getWindowHandles().size() > 1);
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
        return this;
    }

    public HotelPage waitForLoad() {
        waitVisible(hotelTitle);
        return this;
    }

    public boolean isLoaded() {
        try {
            return waitVisible(hotelTitle).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getHotelName() {
        return waitVisible(hotelTitle).getText();
    }

    public boolean isBookButtonVisible() {
        try {
            waitVisible(bookButton);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public HotelPage clickBook() {
        clickWhenClickable(bookButton);
        return this;
    }

    // UC-07
    public boolean isReviewsButtonVisible() {
        try {
            waitVisible(reviewsButton);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public HotelPage clickReviews() {
        clickWhenClickable(reviewsButton);
        return this;
    }

    public HotelPage setParentHandle(String handle) {
        this.parentHandle = handle;
        return this;
    }

}