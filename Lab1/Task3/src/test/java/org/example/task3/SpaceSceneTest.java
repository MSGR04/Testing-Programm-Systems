package org.example.task3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpaceSceneTest {

    @Test
    @DisplayName("Variant 330906 must create initial hidden scene")
    void shouldCreateInitialSceneFromVariant() {
        SpaceScene scene = SpaceScene.variant330906();

        assertEquals(SceneState.INITIAL_STILLNESS, scene.state());
        assertEquals(ScreenSize.HUGE, scene.screen().size());
        assertFalse(scene.edgeLight().isVisible());
        assertFalse(scene.binaryStarSystem().isVisible());
        assertFalse(scene.planetView().isVisible());
        assertEquals(0, scene.screen().totalVisibleObjects());
    }

    @Test
    @DisplayName("Edge light must appear first on the edge of the screen")
    void shouldRevealEdgeLightFirst() {
        SpaceScene scene = SpaceScene.variant330906();

        scene.revealEdgeLight();

        assertEquals(SceneState.EDGE_LIGHT_VISIBLE, scene.state());
        assertTrue(scene.edgeLight().isVisible());
        assertEquals(ScreenRegion.EDGE, scene.edgeLight().region());
        assertEquals(List.of(scene.edgeLight()), scene.screen().getObjectsInRegion(ScreenRegion.EDGE));
    }

    @Test
    @DisplayName("Binary system must appear on the edge after the light")
    void shouldRevealBinarySystemOnEdge() {
        SpaceScene scene = SpaceScene.variant330906();
        scene.revealEdgeLight();

        scene.revealBinaryStarSystem();

        assertEquals(SceneState.BINARY_SYSTEM_VISIBLE, scene.state());
        assertTrue(scene.binaryStarSystem().isVisible());
        assertEquals(BinarySystemState.VISIBLE, scene.binaryStarSystem().state());
        assertTrue(scene.binaryStarSystem().stars().stream()
                .allMatch(star -> star.isVisible()
                        && star.region() == ScreenRegion.EDGE
                        && star.color() == ColorTone.RED
                        && star.apparentSize() == RelativeSize.PLATE_SIZED));
        assertEquals(3, scene.screen().getObjectsInRegion(ScreenRegion.EDGE).size());
    }

    @Test
    @DisplayName("Binary system reveal from initial state must auto-light the edge")
    void shouldAutoRevealEdgeLightBeforeBinarySystem() {
        SpaceScene scene = SpaceScene.variant330906();

        scene.revealBinaryStarSystem();

        assertTrue(scene.edgeLight().isVisible());
        assertTrue(scene.binaryStarSystem().isVisible());
        assertEquals(SceneState.BINARY_SYSTEM_VISIBLE, scene.state());
    }

    @Test
    @DisplayName("Planet night side must appear in the corner after binary system")
    void shouldRevealPlanetNightSideInCorner() {
        SpaceScene scene = SpaceScene.variant330906();
        scene.revealBinaryStarSystem();

        scene.revealPlanetNightSide();

        assertEquals(SceneState.PLANET_NIGHT_SIDE_VISIBLE, scene.state());
        assertTrue(scene.planetView().isVisible());
        assertEquals(ScreenRegion.CORNER, scene.planetView().region());
        assertEquals(RelativeSize.LARGE, scene.planetView().apparentSize());
        assertEquals(PlanetPhase.CRESCENT, scene.planetView().phase());
        assertEquals(ColorTone.RED, scene.planetView().illumination().from());
        assertEquals(ColorTone.BLACK, scene.planetView().illumination().to());
        assertTrue(scene.planetView().nightSideVisible());
        assertEquals(List.of(scene.planetView()), scene.screen().getObjectsInRegion(ScreenRegion.CORNER));
    }

    @Test
    @DisplayName("Scene play must reproduce the whole text narrative")
    void shouldPlayWholeScene() {
        SpaceScene scene = SpaceScene.variant330906();

        scene.play();

        assertEquals(SceneState.COMPLETED, scene.state());
        assertTrue(scene.played());
        assertEquals(4, scene.screen().totalVisibleObjects());
        assertEquals(3, scene.screen().getObjectsInRegion(ScreenRegion.EDGE).size());
        assertEquals(1, scene.screen().getObjectsInRegion(ScreenRegion.CORNER).size());
    }

    @Test
    @DisplayName("Scene must not allow replay")
    void shouldRejectReplay() {
        SpaceScene scene = SpaceScene.variant330906();
        scene.play();

        assertThrows(IllegalStateException.class, scene::play);
    }

    @Test
    @DisplayName("Planet must not appear before binary system")
    void shouldRejectPlanetBeforeBinarySystem() {
        SpaceScene scene = SpaceScene.variant330906();

        assertThrows(IllegalStateException.class, scene::revealPlanetNightSide);
    }
}

