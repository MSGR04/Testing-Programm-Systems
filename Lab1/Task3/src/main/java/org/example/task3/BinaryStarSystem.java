package org.example.task3;

import java.util.List;
import java.util.Objects;

public final class BinaryStarSystem {
    private final Star firstStar;
    private final Star secondStar;
    private BinarySystemState state = BinarySystemState.HIDDEN;

    public BinaryStarSystem(Star firstStar, Star secondStar) {
        this.firstStar = Objects.requireNonNull(firstStar, "firstStar");
        this.secondStar = Objects.requireNonNull(secondStar, "secondStar");
        if (firstStar == secondStar) {
            throw new IllegalArgumentException("binary system must contain two different stars");
        }
    }

    public void appearOnEdge() {
        firstStar.crawlAlongEdge();
        secondStar.crawlAlongEdge();
        state = BinarySystemState.VISIBLE;
    }

    public Star firstStar() {
        return firstStar;
    }

    public Star secondStar() {
        return secondStar;
    }

    public List<Star> stars() {
        return List.of(firstStar, secondStar);
    }

    public BinarySystemState state() {
        return state;
    }

    public boolean isVisible() {
        return state == BinarySystemState.VISIBLE;
    }
}

