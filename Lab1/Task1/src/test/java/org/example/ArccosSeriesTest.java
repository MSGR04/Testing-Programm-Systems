package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

public class ArccosSeriesTest {

    private static final double EPS = 1e-12;
    private static final int MAX_TERMS = 200000;

    @ParameterizedTest(name = "arccos({0}) valid")
    @DisplayName("Check corner valid values in [-1; 1]")
    @ValueSource(doubles = {
            -1.0,
            -0.999999,
            -0.99,
            -0.5,
            -0.000001,
            -0.0,
            0.0,
            0.000001,
            0.5,
            0.99,
            0.999999,
            1.0
    })
    void checkCornerValidDots(double x) {
        double expected = Math.acos(x);
        double actual = ArccosSeries.arccos(x, EPS, MAX_TERMS);

        assertAll(
                () -> assertEquals(expected, actual, 1e-8)
        );
    }

    @ParameterizedTest(name = "arccos({0}) invalid")
    @DisplayName("Check invalid values (must throw)")
    @ValueSource(doubles = {
            -999.9,
            -1.0000001,
            1.0000001,
            999.9,
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY
    })
    void checkInvalidDots(double x) {
        assertThrows(IllegalArgumentException.class,
                () -> ArccosSeries.arccos(x, EPS, MAX_TERMS));
    }

    @ParameterizedTest(name = "arccos({0}) = {1}")
    @DisplayName("Check between dots from CSV [-1; +1]")
    @CsvFileSource(resources = "/table_values_arccos.csv", numLinesToSkip = 1, delimiter = ';')
    void checkBetweenDotsMinus1And1(double x, double y) {
        double actual = ArccosSeries.arccos(x, EPS, MAX_TERMS);
        assertEquals(y, actual, 1e-8);
    }

    @Test
    @DisplayName("Random testing (random x in (-1; 1))")
    void checkRandomDots() {
        for (int i = 0; i < 200_000; i++) {
            double x = ThreadLocalRandom.current().nextDouble(-0.9999, 0.9999);
            double expected = Math.acos(x);
            double actual = ArccosSeries.arccos(x, EPS, MAX_TERMS);
            assertEquals(expected, actual, 1e-8);
        }
    }

}