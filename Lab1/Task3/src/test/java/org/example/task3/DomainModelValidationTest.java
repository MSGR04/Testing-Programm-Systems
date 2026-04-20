package org.example.task3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainModelValidationTest {

    @Test
    @DisplayName("Screen must reject null size")
    void shouldRejectNullScreenSize() {
        assertThrows(NullPointerException.class, () -> new Screen(null));
    }

    @Test
    @DisplayName("Screen must reject invisible object placement")
    void shouldRejectInvisibleObjectPlacement() {
        Screen screen = new Screen(ScreenSize.HUGE);
        EdgeLight hiddenLight = new EdgeLight();

        assertThrows(IllegalStateException.class, () -> screen.show(ScreenRegion.EDGE, hiddenLight));
    }

    @Test
    @DisplayName("Screen must reject region mismatch")
    void shouldRejectRegionMismatch() {
        Screen screen = new Screen(ScreenSize.HUGE);
        EdgeLight edgeLight = new EdgeLight();
        edgeLight.appearOnEdge();

        assertThrows(IllegalStateException.class, () -> screen.show(ScreenRegion.CORNER, edgeLight));
    }

    @Test
    @DisplayName("Objects on screen must be exposed as immutable lists")
    void shouldExposeImmutableRegionLists() {
        Screen screen = new Screen(ScreenSize.HUGE);
        EdgeLight edgeLight = new EdgeLight();
        edgeLight.appearOnEdge();
        screen.show(ScreenRegion.EDGE, edgeLight);

        assertThrows(UnsupportedOperationException.class, () -> screen.getObjectsInRegion(ScreenRegion.EDGE).add(edgeLight));
    }

    @Test
    @DisplayName("Star must reject null attributes")
    void shouldRejectNullStarAttributes() {
        assertThrows(NullPointerException.class, () -> new Star(null, RelativeSize.PLATE_SIZED));
        assertThrows(NullPointerException.class, () -> new Star(ColorTone.RED, null));
    }

    @Test
    @DisplayName("Binary system must reject invalid stars")
    void shouldRejectInvalidBinarySystem() {
        Star star = new Star(ColorTone.RED, RelativeSize.PLATE_SIZED);

        assertThrows(NullPointerException.class, () -> new BinaryStarSystem(null, star));
        assertThrows(NullPointerException.class, () -> new BinaryStarSystem(star, null));
        assertThrows(IllegalArgumentException.class, () -> new BinaryStarSystem(star, star));
    }

    @Test
    @DisplayName("Gradient must connect different colors")
    void shouldRejectFlatGradient() {
        assertThrows(IllegalArgumentException.class, () -> new ColorGradient(ColorTone.RED, ColorTone.RED));
    }

    @Test
    @DisplayName("Planet view must reject invalid night side gradient")
    void shouldRejectInvalidNightSideGradient() {
        assertThrows(IllegalArgumentException.class, () ->
                new PlanetView(
                        RelativeSize.LARGE,
                        PlanetPhase.CRESCENT,
                        new ColorGradient(ColorTone.BLACK, ColorTone.RED),
                        true
                ));
    }

    @Test
    @DisplayName("Planet view must reject null attributes")
    void shouldRejectNullPlanetAttributes() {
        ColorGradient gradient = new ColorGradient(ColorTone.RED, ColorTone.BLACK);

        assertThrows(NullPointerException.class, () -> new PlanetView(null, PlanetPhase.CRESCENT, gradient, true));
        assertThrows(NullPointerException.class, () -> new PlanetView(RelativeSize.LARGE, null, gradient, true));
        assertThrows(NullPointerException.class, () -> new PlanetView(RelativeSize.LARGE, PlanetPhase.CRESCENT, null, true));
    }

    @Test
    @DisplayName("Planet without night side visibility may still exist")
    void shouldAllowPlanetWithoutNightSideVisibility() {
        PlanetView planet = new PlanetView(
                RelativeSize.LARGE,
                PlanetPhase.FULL_DISK,
                new ColorGradient(ColorTone.BLACK, ColorTone.RED),
                false
        );

        planet.appearInCorner();

        assertTrue(planet.isVisible());
        assertEquals(ScreenRegion.CORNER, planet.region());
    }

    @Test
    @DisplayName("Scene must reject null components")
    void shouldRejectNullSceneComponents() {
        Screen screen = new Screen(ScreenSize.HUGE);
        EdgeLight edgeLight = new EdgeLight();
        BinaryStarSystem binaryStarSystem = new BinaryStarSystem(
                new Star(ColorTone.RED, RelativeSize.PLATE_SIZED),
                new Star(ColorTone.RED, RelativeSize.PLATE_SIZED)
        );
        PlanetView planetView = new PlanetView(
                RelativeSize.LARGE,
                PlanetPhase.CRESCENT,
                new ColorGradient(ColorTone.RED, ColorTone.BLACK),
                true
        );

        assertThrows(NullPointerException.class, () -> new SpaceScene(null, edgeLight, binaryStarSystem, planetView));
        assertThrows(NullPointerException.class, () -> new SpaceScene(screen, null, binaryStarSystem, planetView));
        assertThrows(NullPointerException.class, () -> new SpaceScene(screen, edgeLight, null, planetView));
        assertThrows(NullPointerException.class, () -> new SpaceScene(screen, edgeLight, binaryStarSystem, null));
    }
}

