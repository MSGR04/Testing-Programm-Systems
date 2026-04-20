package org.example.task3;

import java.util.Objects;

public final class Star implements VisibleObject {
    private final ColorTone color;
    private final RelativeSize apparentSize;
    private StarState state = StarState.HIDDEN;
    private ScreenRegion region;

    public Star(ColorTone color, RelativeSize apparentSize) {
        this.color = Objects.requireNonNull(color, "color");
        this.apparentSize = Objects.requireNonNull(apparentSize, "apparentSize");
    }

    public void crawlAlongEdge() {
        state = StarState.CRAWLING_ON_EDGE;
        region = ScreenRegion.EDGE;
    }

    public ColorTone color() {
        return color;
    }

    public RelativeSize apparentSize() {
        return apparentSize;
    }

    public StarState state() {
        return state;
    }

    @Override
    public String name() {
        return "star";
    }

    @Override
    public boolean isVisible() {
        return state == StarState.CRAWLING_ON_EDGE;
    }

    @Override
    public ScreenRegion region() {
        return region;
    }
}

