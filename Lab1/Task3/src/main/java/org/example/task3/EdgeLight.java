package org.example.task3;

public final class EdgeLight implements VisibleObject {
    private LightState state = LightState.HIDDEN;
    private ScreenRegion region;

    public void appearOnEdge() {
        state = LightState.VISIBLE_ON_EDGE;
        region = ScreenRegion.EDGE;
    }

    public LightState state() {
        return state;
    }

    @Override
    public String name() {
        return "edgeLight";
    }

    @Override
    public boolean isVisible() {
        return state == LightState.VISIBLE_ON_EDGE;
    }

    @Override
    public ScreenRegion region() {
        return region;
    }
}

