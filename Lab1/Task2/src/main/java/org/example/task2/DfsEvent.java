package org.example.task2;

import java.util.Objects;

public class DfsEvent {

    private final DfsPoint point;
    private final String vertex;
    private final String neighbor;

    public DfsEvent(DfsPoint point, String vertex, String neighbor) {
        this.point = point;
        this.vertex = vertex;
        this.neighbor = neighbor;
    }

    public DfsPoint getPoint() {
        return point;
    }

    public String getVertex() {
        return vertex;
    }

    public String getNeighbor() {
        return neighbor;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof DfsEvent)) {
            return false;
        }

        DfsEvent that = (DfsEvent) other;

        return point == that.point
                && Objects.equals(vertex, that.vertex)
                && Objects.equals(neighbor, that.neighbor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, vertex, neighbor);
    }

    @Override
    public String toString() {
        return "DfsEvent{" +
                "point=" + point +
                ", vertex='" + vertex + '\'' +
                ", neighbor='" + neighbor + '\'' +
                '}';
    }
}