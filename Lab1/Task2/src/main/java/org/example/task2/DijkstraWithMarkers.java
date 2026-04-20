package org.example.task2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class DijkstraWithMarkers {
    public static final int INFINITY = Integer.MAX_VALUE;

    public enum Point {
        INITIALIZE_SOURCE,
        SELECT_VERTEX,
        MARK_SETTLED,
        START_EDGE_SCAN,
        CHECK_EDGE,
        RELAX_EDGE,
        SKIP_EDGE,
        NO_REACHABLE_VERTEX,
        FINISH
    }

    public record TraceEvent(Point point, int vertex, Integer neighbor, Integer distance) {
        public TraceEvent {
            Objects.requireNonNull(point, "point");
        }

        public static TraceEvent initializeSource(int vertex) {
            return new TraceEvent(Point.INITIALIZE_SOURCE, vertex, null, 0);
        }

        public static TraceEvent selectVertex(int vertex, int distance) {
            return new TraceEvent(Point.SELECT_VERTEX, vertex, null, distance);
        }

        public static TraceEvent markSettled(int vertex, int distance) {
            return new TraceEvent(Point.MARK_SETTLED, vertex, null, distance);
        }

        public static TraceEvent startEdgeScan(int vertex) {
            return new TraceEvent(Point.START_EDGE_SCAN, vertex, null, null);
        }

        public static TraceEvent checkEdge(int vertex, int neighbor, int candidateDistance) {
            return new TraceEvent(Point.CHECK_EDGE, vertex, neighbor, candidateDistance);
        }

        public static TraceEvent relaxEdge(int vertex, int neighbor, int newDistance) {
            return new TraceEvent(Point.RELAX_EDGE, vertex, neighbor, newDistance);
        }

        public static TraceEvent skipEdge(int vertex, int neighbor, int candidateDistance) {
            return new TraceEvent(Point.SKIP_EDGE, vertex, neighbor, candidateDistance);
        }

        public static TraceEvent noReachableVertex() {
            return new TraceEvent(Point.NO_REACHABLE_VERTEX, -1, null, null);
        }

        public static TraceEvent finish() {
            return new TraceEvent(Point.FINISH, -1, null, null);
        }

        @Override
        public String toString() {
            return switch (point) {
                case INITIALIZE_SOURCE -> "INIT(" + vertex + "=0)";
                case SELECT_VERTEX -> "SELECT(" + vertex + "=" + distance + ")";
                case MARK_SETTLED -> "SETTLE(" + vertex + "=" + distance + ")";
                case START_EDGE_SCAN -> "SCAN(" + vertex + ")";
                case CHECK_EDGE -> "CHECK(" + vertex + "->" + neighbor + "=" + distance + ")";
                case RELAX_EDGE -> "RELAX(" + vertex + "->" + neighbor + "=" + distance + ")";
                case SKIP_EDGE -> "SKIP(" + vertex + "->" + neighbor + "=" + distance + ")";
                case NO_REACHABLE_VERTEX -> "STOP";
                case FINISH -> "FINISH";
            };
        }
    }

    public record Result(Map<Integer, Integer> distances, List<TraceEvent> trace) {
        public Result {
            distances = Collections.unmodifiableMap(new LinkedHashMap<>(distances));
            trace = Collections.unmodifiableList(new ArrayList<>(trace));
        }
    }

    private final WeightedGraph graph;

    public DijkstraWithMarkers(WeightedGraph graph) {
        this.graph = Objects.requireNonNull(graph, "graph").copy();
    }

    public Result shortestPaths(int startVertex) {
        WeightedGraph workingGraph = graph.copy();
        workingGraph.addVertex(startVertex);

        List<TraceEvent> trace = new ArrayList<>();
        Map<Integer, Integer> distances = new LinkedHashMap<>();
        Set<Integer> unsettled = new LinkedHashSet<>(workingGraph.vertices());
        Set<Integer> settled = new LinkedHashSet<>();

        for (Integer vertex : unsettled) {
            distances.put(vertex, INFINITY);
        }
        distances.put(startVertex, 0);
        trace.add(TraceEvent.initializeSource(startVertex));

        while (true) {
            Integer current = findClosestVertex(unsettled, distances);
            if (current == null || distances.get(current) == INFINITY) {
                trace.add(TraceEvent.noReachableVertex());
                trace.add(TraceEvent.finish());
                return new Result(distances, trace);
            }

            int currentDistance = distances.get(current);
            trace.add(TraceEvent.selectVertex(current, currentDistance));
            unsettled.remove(current);
            settled.add(current);
            trace.add(TraceEvent.markSettled(current, currentDistance));
            trace.add(TraceEvent.startEdgeScan(current));

            for (WeightedGraph.Edge edge : workingGraph.getEdges(current)) {
                int candidateDistance = currentDistance + edge.weight();
                trace.add(TraceEvent.checkEdge(current, edge.to(), candidateDistance));

                if (settled.contains(edge.to()) || candidateDistance >= distances.get(edge.to())) {
                    trace.add(TraceEvent.skipEdge(current, edge.to(), candidateDistance));
                    continue;
                }

                distances.put(edge.to(), candidateDistance);
                trace.add(TraceEvent.relaxEdge(current, edge.to(), candidateDistance));
            }
        }
    }

    public static String formatTrace(List<TraceEvent> trace) {
        return trace.stream()
                .map(TraceEvent::toString)
                .collect(Collectors.joining(" -> "));
    }

    private Integer findClosestVertex(Set<Integer> unsettled, Map<Integer, Integer> distances) {
        return unsettled.stream()
                .min(Comparator
                        .comparingInt((Integer vertex) -> distances.getOrDefault(vertex, INFINITY))
                        .thenComparingInt(Integer::intValue))
                .orElse(null);
    }
}
