package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SpaceScene {

    public enum ScreenScale {
        LARGE,
        HUGE
    }

    public enum ScreenRegion {
        EDGE,
        CORNER
    }

    public enum ColorTone {
        RED,
        BLACK
    }

    public enum RelativeSize {
        PLATE_SIZED,
        LARGE
    }

    public enum PlanetPhase {
        CRESCENT,
        FULL_DISK
    }

    public interface VisibleObject {
        String kind();
    }

    public record Screen(ScreenScale scale) {
        public Screen {
            Objects.requireNonNull(scale, "scale");
        }
    }

    public record Star(ColorTone color, RelativeSize apparentSize) implements VisibleObject {
        public Star {
            Objects.requireNonNull(color, "color");
            Objects.requireNonNull(apparentSize, "apparentSize");
        }

        @Override
        public String kind() {
            return "star";
        }
    }

    public record BinaryStarSystem(Star firstStar, Star secondStar) implements VisibleObject {
        public BinaryStarSystem {
            Objects.requireNonNull(firstStar, "firstStar");
            Objects.requireNonNull(secondStar, "secondStar");
        }

        public List<Star> stars() {
            return List.of(firstStar, secondStar);
        }

        @Override
        public String kind() {
            return "binaryStarSystem";
        }
    }

    public record ColorGradient(ColorTone from, ColorTone to) {
        public ColorGradient {
            Objects.requireNonNull(from, "from");
            Objects.requireNonNull(to, "to");
            if (from == to) {
                throw new IllegalArgumentException("gradient must connect different colors");
            }
        }
    }

    public record PlanetView(RelativeSize apparentSize,
                             PlanetPhase phase,
                             ColorGradient illumination,
                             boolean nightSideVisible) implements VisibleObject {
        public PlanetView {
            Objects.requireNonNull(apparentSize, "apparentSize");
            Objects.requireNonNull(phase, "phase");
            Objects.requireNonNull(illumination, "illumination");
            if (nightSideVisible && illumination.to() != ColorTone.BLACK) {
                throw new IllegalArgumentException("night side must fade into black");
            }
        }

        @Override
        public String kind() {
            return "planet";
        }
    }

    public record AppearanceEvent(ScreenRegion region, int order, VisibleObject object) {
        public AppearanceEvent {
            Objects.requireNonNull(region, "region");
            Objects.requireNonNull(object, "object");
            if (order <= 0) {
                throw new IllegalArgumentException("order must be positive");
            }
        }
    }

    private final Screen screen;
    private final List<AppearanceEvent> events;

    public SpaceScene(Screen screen, List<AppearanceEvent> events) {
        this.screen = Objects.requireNonNull(screen, "screen");
        Objects.requireNonNull(events, "events");

        List<AppearanceEvent> copy = new ArrayList<>(events);
        copy.forEach(event -> Objects.requireNonNull(event, "event"));
        validateEventOrder(copy);

        this.events = List.copyOf(copy);
    }

    public Screen screen() {
        return screen;
    }

    public List<AppearanceEvent> events() {
        return events;
    }

    public Optional<BinaryStarSystem> binaryStarSystem() {
        return events.stream()
                .map(AppearanceEvent::object)
                .filter(BinaryStarSystem.class::isInstance)
                .map(BinaryStarSystem.class::cast)
                .findFirst();
    }

    public Optional<PlanetView> planetView() {
        return events.stream()
                .map(AppearanceEvent::object)
                .filter(PlanetView.class::isInstance)
                .map(PlanetView.class::cast)
                .findFirst();
    }

    public static SpaceScene variant330906() {
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

        return new SpaceScene(
                new Screen(ScreenScale.HUGE),
                List.of(
                        new AppearanceEvent(ScreenRegion.EDGE, 1, binaryStarSystem),
                        new AppearanceEvent(ScreenRegion.CORNER, 2, planetView)
                )
        );
    }

    private static void validateEventOrder(List<AppearanceEvent> events) {
        int lastOrder = 0;
        for (AppearanceEvent event : events) {
            if (event.order() <= lastOrder) {
                throw new IllegalArgumentException("events must be strictly ordered");
            }
            lastOrder = event.order();
        }
    }
}
