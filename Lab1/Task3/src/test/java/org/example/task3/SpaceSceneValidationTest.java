package org.example.task3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpaceSceneValidationTest {

    @Test
    @DisplayName("Edge light must change its state when it appears")
    void shouldUpdateEdgeLightState() {
        EdgeLight edgeLight = new EdgeLight();

        edgeLight.appearOnEdge();

        assertEquals(LightState.VISIBLE_ON_EDGE, edgeLight.state());
        assertTrue(edgeLight.isVisible());
        assertEquals(ScreenRegion.EDGE, edgeLight.region());
    }

    @Test
    @DisplayName("Visible object names must match the domain model")
    void shouldExposeDomainObjectNames() {
        EdgeLight edgeLight = new EdgeLight();
        Star star = new Star(ColorTone.RED, RelativeSize.PLATE_SIZED);
        PlanetView planet = new PlanetView(
                RelativeSize.LARGE,
                PlanetPhase.CRESCENT,
                new ColorGradient(ColorTone.RED, ColorTone.BLACK),
                true
        );

        assertEquals("edgeLight", edgeLight.name());
        assertEquals("star", star.name());
        assertEquals("planet", planet.name());
    }

    @Test
    @DisplayName("Hidden objects must not be visible before appearance")
    void shouldKeepObjectsHiddenBeforeAppearance() {
        EdgeLight edgeLight = new EdgeLight();
        Star star = new Star(ColorTone.RED, RelativeSize.PLATE_SIZED);
        PlanetView planet = new PlanetView(
                RelativeSize.LARGE,
                PlanetPhase.CRESCENT,
                new ColorGradient(ColorTone.RED, ColorTone.BLACK),
                true
        );

        assertFalse(edgeLight.isVisible());
        assertFalse(star.isVisible());
        assertFalse(planet.isVisible());
    }

    @Test
    @DisplayName("Star must become visible when it crawls on the edge")
    void shouldUpdateStarStateWhenCrawling() {
        Star star = new Star(ColorTone.RED, RelativeSize.PLATE_SIZED);

        star.crawlAlongEdge();

        assertEquals(StarState.CRAWLING_ON_EDGE, star.state());
        assertTrue(star.isVisible());
        assertEquals(ScreenRegion.EDGE, star.region());
    }

    @Test
    @DisplayName("Binary system appearance must reveal both stars")
    void shouldRevealBothStarsInBinarySystem() {
        BinaryStarSystem system = new BinaryStarSystem(
                new Star(ColorTone.RED, RelativeSize.PLATE_SIZED),
                new Star(ColorTone.RED, RelativeSize.PLATE_SIZED)
        );

        system.appearOnEdge();

        assertEquals(BinarySystemState.VISIBLE, system.state());
        assertTrue(system.stars().stream().allMatch(Star::isVisible));
        assertTrue(system.isVisible());
    }

    @Test
    @DisplayName("Planet appearance must update its state and region")
    void shouldUpdatePlanetStateWhenAppearing() {
        PlanetView planet = new PlanetView(
                RelativeSize.LARGE,
                PlanetPhase.CRESCENT,
                new ColorGradient(ColorTone.RED, ColorTone.BLACK),
                true
        );

        planet.appearInCorner();

        assertEquals(PlanetState.VISIBLE_IN_CORNER, planet.state());
        assertTrue(planet.isVisible());
        assertEquals(ScreenRegion.CORNER, planet.region());
    }

    @Test
    @DisplayName("Screen must report contained visible object")
    void shouldReportContainedObject() {
        Screen screen = new Screen(ScreenSize.HUGE);
        EdgeLight edgeLight = new EdgeLight();
        edgeLight.appearOnEdge();

        screen.show(ScreenRegion.EDGE, edgeLight);

        assertTrue(screen.contains(edgeLight));
        assertFalse(screen.contains(new EdgeLight()));
    }

    @Test
    @DisplayName("Screen must not duplicate the same object in a region")
    void shouldAvoidDuplicatingSameObjectOnScreen() {
        Screen screen = new Screen(ScreenSize.HUGE);
        EdgeLight edgeLight = new EdgeLight();
        edgeLight.appearOnEdge();

        screen.show(ScreenRegion.EDGE, edgeLight);
        screen.show(ScreenRegion.EDGE, edgeLight);

        assertEquals(1, screen.getObjectsInRegion(ScreenRegion.EDGE).size());
        assertEquals(1, screen.totalVisibleObjects());
    }

    @Test
    @DisplayName("Scene must reject repeated edge light reveal")
    void shouldRejectRepeatedEdgeLightReveal() {
        SpaceScene scene = SpaceScene.variant330906();
        scene.revealEdgeLight();

        assertThrows(IllegalStateException.class, scene::revealEdgeLight);
    }
}

