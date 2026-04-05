package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class DFSWithMarkers {

    public enum Point {
        ENTER_VERTEX,
        START_NEIGHBOR_SCAN,
        CHECK_NEIGHBOR,
        RECURSIVE_DESCENT,
        EXIT_VERTEX
    }

    public record TraceEvent(Point point, int vertex, Integer neighbor) {
        public TraceEvent {
            Objects.requireNonNull(point, "point");
        }

        public static TraceEvent enter(int vertex) {
            return new TraceEvent(Point.ENTER_VERTEX, vertex, null);
        }

        public static TraceEvent startNeighborScan(int vertex) {
            return new TraceEvent(Point.START_NEIGHBOR_SCAN, vertex, null);
        }

        public static TraceEvent checkNeighbor(int vertex, int neighbor) {
            return new TraceEvent(Point.CHECK_NEIGHBOR, vertex, neighbor);
        }

        public static TraceEvent recursiveDescent(int vertex, int neighbor) {
            return new TraceEvent(Point.RECURSIVE_DESCENT, vertex, neighbor);
        }

        public static TraceEvent exit(int vertex) {
            return new TraceEvent(Point.EXIT_VERTEX, vertex, null);
        }

        @Override
        public String toString() {
            return switch (point) {
                case ENTER_VERTEX -> "ENTER(" + vertex + ")";
                case START_NEIGHBOR_SCAN -> "SCAN(" + vertex + ")";
                case CHECK_NEIGHBOR -> "CHECK(" + vertex + "->" + neighbor + ")";
                case RECURSIVE_DESCENT -> "DESCEND(" + vertex + "->" + neighbor + ")";
                case EXIT_VERTEX -> "EXIT(" + vertex + ")";
            };
        }
    }

    private final Map<Integer, List<Integer>> graph;

    public DFSWithMarkers(Map<Integer, List<Integer>> graph) {
        this.graph = copyGraph(graph);
    }

    public List<TraceEvent> dfs(int startVertex) {
        List<TraceEvent> trace = new ArrayList<>();
        Set<Integer> visited = new LinkedHashSet<>();
        dfsRecursive(startVertex, visited, trace);
        return trace;
    }

    public static String formatTrace(List<TraceEvent> trace) {
        return trace.stream()
                .map(TraceEvent::toString)
                .collect(Collectors.joining(" -> "));
    }

    private void dfsRecursive(int vertex, Set<Integer> visited, List<TraceEvent> trace) {
        trace.add(TraceEvent.enter(vertex));
        visited.add(vertex);

        trace.add(TraceEvent.startNeighborScan(vertex));
        for (int neighbor : graph.getOrDefault(vertex, List.of())) {
            trace.add(TraceEvent.checkNeighbor(vertex, neighbor));
            if (!visited.contains(neighbor)) {
                trace.add(TraceEvent.recursiveDescent(vertex, neighbor));
                dfsRecursive(neighbor, visited, trace);
            }
        }

        trace.add(TraceEvent.exit(vertex));
    }

    private static Map<Integer, List<Integer>> copyGraph(Map<Integer, List<Integer>> source) {
        Objects.requireNonNull(source, "graph");

        Map<Integer, List<Integer>> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : source.entrySet()) {
            Integer vertex = Objects.requireNonNull(entry.getKey(), "vertex");
            List<Integer> neighbors = entry.getValue() == null ? List.of() : List.copyOf(entry.getValue());

            result.put(vertex, neighbors);
            for (Integer neighbor : neighbors) {
                result.putIfAbsent(Objects.requireNonNull(neighbor, "neighbor"), List.of());
            }
        }

        return Collections.unmodifiableMap(result);
    }
}
