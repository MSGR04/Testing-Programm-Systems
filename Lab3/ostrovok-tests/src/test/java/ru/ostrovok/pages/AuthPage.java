package ru.ostrovok.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object для страницы авторизации / регистрации ostrovok.ru.
 * <p>
 * UC-12: Регистрация пользователя (основной и альтернативные потоки)
 */
public class AuthPage extends BasePage {

    // ── Форма входа/регистрации ───────────────────────────────────────────────
    private final By authForm =
            By.xpath("//form[contains(@class,'auth') or contains(@data-qa,'auth-form') or " +
                    "contains(@class,'login') or contains(@class,'Login')]");

    // ── Кнопка/ссылка «Зарегистрироваться» ───────────────────────────────────
    private final By registerLink =
            By.xpath("//a[contains(text(),'Зарегистрироваться') or " +
                    "contains(@data-qa,'register-link')] | " +
                    "//button[contains(text(),'Зарегистрироваться')][1]");

    // ── Поле Email ────────────────────────────────────────────────────────────
    private final By emailInput =
            By.xpath("//input[@type='email' or @name='email' or " +
                    "contains(@data-qa,'email') or " +
                    "contains(@placeholder,'email') or " +
                    "contains(@placeholder,'Email')][1]");

    // ── Поле Пароль ───────────────────────────────────────────────────────────
    private final By passwordInput =
            By.xpath("//input[@type='password' or @name='password' or " +
                    "contains(@data-qa,'password')][1]");

    // ── Чекбокс согласия с обработкой персональных данных ────────────────────
    private final By consentCheckbox =
            By.xpath("//input[@type='checkbox' and " +
                    "(contains(@name,'consent') or contains(@data-qa,'consent') or " +
                    "contains(@name,'agree') or contains(@id,'agree'))][1]");

    // ── Кнопка «Зарегистрироваться» (submit) ─────────────────────────────────
    private final By submitButton =
            By.xpath("//button[@type='submit' or " +
                    "contains(@data-qa,'register-submit') or " +
                    "contains(text(),'Зарегистрироваться')][1]");

    // ── Сообщение об ошибке валидации email ──────────────────────────────────
    private final By emailValidationError =
            By.xpath("//*[contains(@class,'error') or contains(@data-qa,'error') or " +
                    "contains(@class,'invalid')][contains(text(),'email') or " +
                    "contains(text(),'Email') or " +
                    "contains(text(),'некорректн') or contains(text(),'неверн')][1]");

    // ── Кнопка «Войти через OK» ───────────────────────────────────────────────
    private final By loginViaOkButton =
            By.xpath("//button[contains(text(),'OK') or contains(@data-qa,'ok-login') or " +
                    "contains(@aria-label,'Одноклассники')] | " +
                    "//a[contains(@href,'ok.ru') and " +
                    "(contains(text(),'OK') or contains(@aria-label,'OK'))][1]");

    public AuthPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Ждёт появления формы авторизации.
     */
    public AuthPage waitForLoad() {
        waitVisible(authForm);
        return this;
    }

    /**
     * UC-12 шаг 2: кликает «Зарегистрироваться».
     */
    public AuthPage clickRegister() {
        clickWhenClickable(registerLink);
        return this;
    }

    /**
     * UC-12 шаг 3: вводит email.
     */
    public AuthPage enterEmail(String email) {
        typeText(emailInput, email);
        return this;
    }

    /**
     * UC-12 шаг 3: вводит пароль.
     */
    public AuthPage enterPassword(String password) {
        typeText(passwordInput, password);
        return this;
    }

    /**
     * UC-12 шаг 4: ставит галочку согласия.
     */
    public AuthPage acceptConsent() {
        clickWhenClickable(consentCheckbox);
        return this;
    }

    /**
     * UC-12 шаг 5: нажимает кнопку «Зарегистрироваться».
     */
    public AuthPage clickSubmit() {
        clickWhenClickable(submitButton);
        return this;
    }

    /**
     * UC-12 альт. поток 1: проверяет наличие ошибки валидации email.
     */
    public boolean isEmailValidationErrorVisible() {
        try {
            return waitVisible(emailValidationError).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * UC-12 альт. поток 2: нажимает «Войти через OK».
     */
    public AuthPage clickLoginViaOk() {
        clickWhenClickable(loginViaOkButton);
        return this;
    }

    /**
     * Проверяет, что новое окно появилось (например, OAuth).
     */
    public boolean isNewWindowOpened(int previousWindowCount) {
        return driver.getWindowHandles().size() > previousWindowCount;
    }

    /**
     * Форма авторизации видна на странице.
     */
    public boolean isLoaded() {
        try {
            return waitVisible(authForm).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
