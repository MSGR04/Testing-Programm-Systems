package org.example.task3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    @DisplayName("Main must print the final state of variant 330906")
    void shouldPrintSceneSummary() {
        assertDoesNotThrow(Main::new);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            Main.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }

        List<String> lines = output.toString(StandardCharsets.UTF_8).lines().toList();

        assertEquals(
                List.of(
                        "Scene state: COMPLETED",
                        "Screen size: HUGE",
                        "Visible objects: 4",
                        "Edge objects: 3",
                        "Corner objects: 1"
                ),
                lines
        );
    }
}
