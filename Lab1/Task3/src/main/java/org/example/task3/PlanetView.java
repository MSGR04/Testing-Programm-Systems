package org.example.task3;

import java.util.Objects;

public final class PlanetView implements VisibleObject {
    private final RelativeSize apparentSize;
    private final PlanetPhase phase;
    private final ColorGradient illumination;
    private final boolean nightSideVisible;
    private PlanetState state = PlanetState.HIDDEN;
    private ScreenRegion region;

    public PlanetView(RelativeSize apparentSize,
                      PlanetPhase phase,
                      ColorGradient illumination,
                      boolean nightSideVisible) {
        this.apparentSize = Objects.requireNonNull(apparentSize, "apparentSize");
        this.phase = Objects.requireNonNull(phase, "phase");
        this.illumination = Objects.requireNonNull(illumination, "illumination");
        this.nightSideVisible = nightSideVisible;

        if (nightSideVisible && illumination.to() != ColorTone.BLACK) {
            throw new IllegalArgumentException("night side must fade into black");
        }
    }

    public void appearInCorner() {
        state = PlanetState.VISIBLE_IN_CORNER;
        region = ScreenRegion.CORNER;
    }

    public RelativeSize apparentSize() {
        return apparentSize;
    }

    public PlanetPhase phase() {
        return phase;
    }

    public ColorGradient illumination() {
        return illumination;
    }

    public boolean nightSideVisible() {
        return nightSideVisible;
    }

    public PlanetState state() {
        return state;
    }

    @Override
    public String name() {
        return "planet";
    }

    @Override
    public boolean isVisible() {
        return state == PlanetState.VISIBLE_IN_CORNER;
    }

    @Override
    public ScreenRegion region() {
        return region;
    }
}

