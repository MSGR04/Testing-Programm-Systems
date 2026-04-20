package org.example.task3;

import java.util.Objects;

public final class SpaceScene {
    private final Screen screen;
    private final EdgeLight edgeLight;
    private final BinaryStarSystem binaryStarSystem;
    private final PlanetView planetView;
    private SceneState state = SceneState.INITIAL_STILLNESS;
    private boolean played;

    public SpaceScene(Screen screen,
                      EdgeLight edgeLight,
                      BinaryStarSystem binaryStarSystem,
                      PlanetView planetView) {
        this.screen = Objects.requireNonNull(screen, "screen");
        this.edgeLight = Objects.requireNonNull(edgeLight, "edgeLight");
        this.binaryStarSystem = Objects.requireNonNull(binaryStarSystem, "binaryStarSystem");
        this.planetView = Objects.requireNonNull(planetView, "planetView");
    }

    public static SpaceScene variant330906() {
        return new SpaceScene(
                new Screen(ScreenSize.HUGE),
                new EdgeLight(),
                new BinaryStarSystem(
                        new Star(ColorTone.RED, RelativeSize.PLATE_SIZED),
                        new Star(ColorTone.RED, RelativeSize.PLATE_SIZED)
                ),
                new PlanetView(
                        RelativeSize.LARGE,
                        PlanetPhase.CRESCENT,
                        new ColorGradient(ColorTone.RED, ColorTone.BLACK),
                        true
                )
        );
    }

    public void revealEdgeLight() {
        ensureState(SceneState.INITIAL_STILLNESS, "edge light can appear only at the beginning");
        edgeLight.appearOnEdge();
        screen.show(ScreenRegion.EDGE, edgeLight);
        state = SceneState.EDGE_LIGHT_VISIBLE;
    }

    public void revealBinaryStarSystem() {
        if (state == SceneState.INITIAL_STILLNESS) {
            revealEdgeLight();
        }
        ensureState(SceneState.EDGE_LIGHT_VISIBLE, "binary system must appear after edge light");

        binaryStarSystem.appearOnEdge();
        screen.show(ScreenRegion.EDGE, binaryStarSystem.firstStar());
        screen.show(ScreenRegion.EDGE, binaryStarSystem.secondStar());
        state = SceneState.BINARY_SYSTEM_VISIBLE;
    }

    public void revealPlanetNightSide() {
        ensureState(SceneState.BINARY_SYSTEM_VISIBLE, "planet may appear only after binary system");
        planetView.appearInCorner();
        screen.show(ScreenRegion.CORNER, planetView);
        state = SceneState.PLANET_NIGHT_SIDE_VISIBLE;
    }

    public void play() {
        if (played) {
            throw new IllegalStateException("scene has already been played");
        }
        ensureState(SceneState.INITIAL_STILLNESS, "scene must start from the initial stillness");

        revealEdgeLight();
        revealBinaryStarSystem();
        revealPlanetNightSide();
        state = SceneState.COMPLETED;
        played = true;
    }

    public Screen screen() {
        return screen;
    }

    public EdgeLight edgeLight() {
        return edgeLight;
    }

    public BinaryStarSystem binaryStarSystem() {
        return binaryStarSystem;
    }

    public PlanetView planetView() {
        return planetView;
    }

    public SceneState state() {
        return state;
    }

    public boolean played() {
        return played;
    }

    private void ensureState(SceneState expected, String message) {
        if (state != expected) {
            throw new IllegalStateException(message + ": " + state);
        }
    }
}

