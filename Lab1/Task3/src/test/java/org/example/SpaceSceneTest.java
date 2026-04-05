package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpaceSceneTest {

    @Test
    @DisplayName("Variant 330906 scene must match the narrative")
    void shouldCreateSceneFromVariantNarrative() {
        SpaceScene scene = SpaceScene.variant330906();

        assertEquals(SpaceScene.ScreenScale.HUGE, scene.screen().scale());
        assertEquals(2, scene.events().size());

        SpaceScene.AppearanceEvent firstEvent = scene.events().get(0);
        assertEquals(SpaceScene.ScreenRegion.EDGE, firstEvent.region());
        SpaceScene.BinaryStarSystem binaryStarSystem =
                assertInstanceOf(SpaceScene.BinaryStarSystem.class, firstEvent.object());
        assertEquals(2, binaryStarSystem.stars().size());
        assertTrue(binaryStarSystem.stars().stream()
                .allMatch(star -> star.color() == SpaceScene.ColorTone.RED
                        && star.apparentSize() == SpaceScene.RelativeSize.PLATE_SIZED));

        SpaceScene.AppearanceEvent secondEvent = scene.events().get(1);
        assertEquals(SpaceScene.ScreenRegion.CORNER, secondEvent.region());
        SpaceScene.PlanetView planetView =
                assertInstanceOf(SpaceScene.PlanetView.class, secondEvent.object());
        assertEquals(SpaceScene.RelativeSize.LARGE, planetView.apparentSize());
        assertEquals(SpaceScene.PlanetPhase.CRESCENT, planetView.phase());
        assertEquals(SpaceScene.ColorTone.RED, planetView.illumination().from());
        assertEquals(SpaceScene.ColorTone.BLACK, planetView.illumination().to());
        assertTrue(planetView.nightSideVisible());
    }

    @Test
    @DisplayName("Planet with visible night side must fade into black")
    void shouldRejectPlanetWithoutBlackNightSide() {
        SpaceScene.ColorGradient invalidGradient =
                new SpaceScene.ColorGradient(SpaceScene.ColorTone.BLACK, SpaceScene.ColorTone.RED);

        assertThrows(IllegalArgumentException.class, () ->
                new SpaceScene.PlanetView(
                        SpaceScene.RelativeSize.LARGE,
                        SpaceScene.PlanetPhase.CRESCENT,
                        invalidGradient,
                        true
                ));
    }

    @Test
    @DisplayName("Scene events must be strictly ordered")
    void shouldRejectSceneWithUnorderedEvents() {
        SpaceScene.Screen screen = new SpaceScene.Screen(SpaceScene.ScreenScale.HUGE);
        SpaceScene.Star star = new SpaceScene.Star(
                SpaceScene.ColorTone.RED,
                SpaceScene.RelativeSize.PLATE_SIZED
        );

        List<SpaceScene.AppearanceEvent> unorderedEvents = List.of(
                new SpaceScene.AppearanceEvent(SpaceScene.ScreenRegion.CORNER, 2, star),
                new SpaceScene.AppearanceEvent(SpaceScene.ScreenRegion.EDGE, 2, star)
        );

        assertThrows(IllegalArgumentException.class, () -> new SpaceScene(screen, unorderedEvents));
    }

    @Test
    @DisplayName("Scene events collection must be immutable")
    void shouldExposeImmutableEvents() {
        SpaceScene scene = SpaceScene.variant330906();

        assertThrows(UnsupportedOperationException.class, () ->
                scene.events().add(new SpaceScene.AppearanceEvent(
                        SpaceScene.ScreenRegion.EDGE,
                        3,
                        new SpaceScene.Star(SpaceScene.ColorTone.RED, SpaceScene.RelativeSize.PLATE_SIZED)
                )));
    }

    @Test
    @DisplayName("Scene accessors must return the main celestial objects")
    void shouldExposeBinaryStarSystemAndPlanetView() {
        SpaceScene scene = SpaceScene.variant330906();

        assertTrue(scene.binaryStarSystem().isPresent());
        assertTrue(scene.planetView().isPresent());
    }
}
