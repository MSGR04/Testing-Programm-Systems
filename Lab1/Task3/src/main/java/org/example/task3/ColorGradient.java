package org.example.task3;

import java.util.Objects;

public final class ColorGradient {
    private final ColorTone from;
    private final ColorTone to;

    public ColorGradient(ColorTone from, ColorTone to) {
        this.from = Objects.requireNonNull(from, "from");
        this.to = Objects.requireNonNull(to, "to");
        if (from == to) {
            throw new IllegalArgumentException("gradient must connect different colors");
        }
    }

    public ColorTone from() {
        return from;
    }

    public ColorTone to() {
        return to;
    }
}

