# Функциональное тестирование ostrovok.ru

## Selenium WebDriver + Java + JUnit 5 + Maven

### Структура проекта

```
ostrovok-tests/
├── pom.xml
├── browsers.properties          ← выбор браузера (chrome / firefox / all)
└── src/test/java/ru/ostrovok/tests/
    ├── driver/
    │   └── DriverFactory.java   ← фабрика WebDriver
    ├── pages/                   ← PageObject-классы
    │   ├── BasePage.java
    │   ├── MainPage.java
    │   ├── SearchResultsPage.java
    │   ├── HotelPage.java
    │   ├── BookingPage.java
    │   ├── ReviewsPage.java
    │   ├── TransfersPage.java
    │   └── AuthPage.java
    └── tests/                   ← тестовые классы
        ├── BaseTest.java
        ├── UC01SearchTest.java
        ├── UC02_03_04_05FilterTest.java
        ├── UC06_07HotelTest.java
        ├── UC08_09_10TransfersTest.java
        └── UC11_12AuthTest.java
```

### Требования

- Java 11+
- Maven 3.6+
- Google Chrome и/или Firefox (актуальные версии)
- WebDriverManager скачает нужный драйвер автоматически

### Запуск тестов

**Все тесты в обоих браузерах (параллельно):**

```bash
mvn test -Dbrowser=all
```

**Только в Chrome:**

```bash
mvn test -Dbrowser=chrome
```

**Только в Firefox:**

```bash
mvn test -Dbrowser=firefox
```

**Через browsers.properties** (отредактируйте файл, затем):

```bash
mvn test
```

**Запуск одного тест-класса:**

```bash
mvn test -Dtest=UC01SearchTest -Dbrowser=chrome
```

### Покрываемые прецеденты (Use Cases)

| UC    | Описание                              | Класс теста             |
|-------|---------------------------------------|-------------------------|
| UC-01 | Поиск жилья и открытие карточки отеля | UC01SearchTest          |
| UC-02 | Просмотр результатов поиска           | UC02_03_04_05FilterTest |
| UC-03 | Фильтр «3 звезды» и «Бесконтактное»   | UC02_03_04_05FilterTest |
| UC-04 | Сортировка «Сначала дешёвые»          | UC02_03_04_05FilterTest |
| UC-05 | Добавление/удаление из избранного     | UC02_03_04_05FilterTest |
| UC-06 | Бронирование отеля                    | UC06_07HotelTest        |
| UC-07 | Просмотр отзывов                      | UC06_07HotelTest        |
| UC-08 | Поиск трансфера в одну сторону        | UC08_09_10TransfersTest |
| UC-09 | Обмен направлений                     | UC08_09_10TransfersTest |
| UC-10 | Поиск трансфера туда-обратно          | UC08_09_10TransfersTest |
| UC-11 | Переход на corp.ostrovok.ru           | UC11_12AuthTest         |
| UC-12 | Регистрация (осн. + альт. потоки)     | UC11_12AuthTest         |
