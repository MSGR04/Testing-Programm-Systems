package org.example.task1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    @DisplayName("Main class constructor must be instantiable")
    void shouldInstantiateMain() {
        assertNotNull(new Main());
    }

    @Test
    @DisplayName("Main without arguments must print demo table")
    void shouldPrintDemoWhenArgsAreMissing() {
        String text = normalize(runMain());

        assertTrue(text.contains("x=-0.90"));
        assertTrue(text.contains("x=-0.50"));
        assertTrue(text.contains("x= 0.00"));
        assertTrue(text.contains("x= 0.50"));
        assertTrue(text.contains("x= 0.90"));
    }

    @Test
    @DisplayName("Main with explicit arguments must print comparison block")
    void shouldPrintDetailedOutputForExplicitArguments() {
        String text = normalize(runMain("0.5", "1e-12", "100000"));

        assertTrue(text.contains("x = 0.5"));
        assertTrue(text.contains("Series arccos(x) ="));
        assertTrue(text.contains("Math.acos(x)     ="));
        assertTrue(text.contains("|diff|           ="));
    }

    @Test
    @DisplayName("Main with only x must use default eps and maxTerms")
    void shouldUseDefaultValuesWhenOnlyXIsProvided() {
        String text = normalize(runMain("0.5"));

        assertTrue(text.contains("Series arccos(x) ="));
        assertTrue(text.contains("Math.acos(x)     ="));
    }

    @Test
    @DisplayName("Main with x and eps must use default maxTerms")
    void shouldUseDefaultMaxTermsWhenOnlyXAndEpsAreProvided() {
        String text = normalize(runMain("0.5", "1e-12"));

        assertTrue(text.contains("Series arccos(x) ="));
        assertTrue(text.contains("Math.acos(x)     ="));
    }

    private static String runMain(String... args) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Main.main(args);
        } finally {
            System.setOut(originalOut);
        }

        return output.toString(StandardCharsets.UTF_8);
    }

    private static String normalize(String text) {
        return text.replace(',', '.');
    }
}
