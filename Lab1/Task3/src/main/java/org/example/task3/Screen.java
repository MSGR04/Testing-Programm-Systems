package org.example.task3;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Screen {
    private final ScreenSize size;
    private final Map<ScreenRegion, List<VisibleObject>> objectsByRegion = new EnumMap<>(ScreenRegion.class);

    public Screen(ScreenSize size) {
        this.size = Objects.requireNonNull(size, "size");
    }

    public ScreenSize size() {
        return size;
    }

    public void show(ScreenRegion region, VisibleObject object) {
        Objects.requireNonNull(region, "region");
        Objects.requireNonNull(object, "object");
        if (!object.isVisible()) {
            throw new IllegalStateException("object must be visible before placing on screen");
        }
        if (object.region() != region) {
            throw new IllegalStateException("object region does not match target region");
        }

        List<VisibleObject> objects = objectsByRegion.computeIfAbsent(region, ignored -> new ArrayList<>());
        if (!objects.contains(object)) {
            objects.add(object);
        }
    }

    public List<VisibleObject> getObjectsInRegion(ScreenRegion region) {
        Objects.requireNonNull(region, "region");
        return List.copyOf(objectsByRegion.getOrDefault(region, List.of()));
    }

    public int totalVisibleObjects() {
        return objectsByRegion.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    public boolean contains(VisibleObject object) {
        Objects.requireNonNull(object, "object");
        return objectsByRegion.values().stream().anyMatch(list -> list.contains(object));
    }
}

