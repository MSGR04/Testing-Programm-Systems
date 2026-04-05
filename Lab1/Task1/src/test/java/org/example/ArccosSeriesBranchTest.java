package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArccosSeriesBranchTest {

    @Test
    @DisplayName("Branch x >= 0 is covered (x = 0.5)")
    void coversNonNegativeBranch() {
        double x = 0.5;
        double actual = ArccosSeries.arccos(x, 1e-12, 100000);
        assertEquals(Math.acos(x), actual, 1e-10);
    }
    @Test
    @DisplayName("Covers loop termination by maxTerms (no break)")
    void coversLoopMaxTermsExit() {
        double x = 0.5;

        double eps = 1e-30;

        int maxTerms = 1;

        double result = ArccosSeries.arccos(x, eps, maxTerms);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Branch x < 0 is covered (x = -0.5)")
    void coversNegativeBranch() {
        double x = -0.5;
        double actual = ArccosSeries.arccos(x, 1e-12, 100000);
        assertEquals(Math.acos(x), actual, 1e-10);
    }

    @Test
    @DisplayName("Corner cases x = 1 and x = -1")
    void coversExactEdges() {
        assertAll(
                () -> assertEquals(0.0, ArccosSeries.arccos(1.0, 1e-12, 200000), 1e-10),
                () -> assertEquals(Math.PI, ArccosSeries.arccos(-1.0, 1e-12, 200000), 1e-10)
        );
    }

    @Test
    @DisplayName("Validate throws for invalid x")
    void coversInvalidX() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> ArccosSeries.arccos(1.00001, 1e-12, 10)),
                () -> assertThrows(IllegalArgumentException.class, () -> ArccosSeries.arccos(-1.00001, 1e-12, 10)),
                () -> assertThrows(IllegalArgumentException.class, () -> ArccosSeries.arccos(Double.NaN, 1e-12, 10)),
                () -> assertThrows(IllegalArgumentException.class, () -> ArccosSeries.arccos(Double.POSITIVE_INFINITY, 1e-12, 10)),
                () -> assertThrows(IllegalArgumentException.class, () -> ArccosSeries.arccos(Double.NEGATIVE_INFINITY, 1e-12, 10))
        );
    }

    @Test
    @DisplayName("Validate throws for invalid eps and maxTerms")
    void coversInvalidEpsAndMaxTerms() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> ArccosSeries.arccos(0.0, 0.0, 10)),
                () -> assertThrows(IllegalArgumentException.class, () -> ArccosSeries.arccos(0.0, -1e-3, 10)),
                () -> assertThrows(IllegalArgumentException.class, () -> ArccosSeries.arccos(0.0, Double.NaN, 10)),
                () -> assertThrows(IllegalArgumentException.class, () -> ArccosSeries.arccos(0.0, 1e-12, 0)),
                () -> assertThrows(IllegalArgumentException.class, () -> ArccosSeries.arccos(0.0, 1e-12, -5))
        );
    }

}
