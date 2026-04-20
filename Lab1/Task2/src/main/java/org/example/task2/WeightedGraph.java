package org.example.task2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class WeightedGraph {
    public record Edge(int to, int weight) {
        public Edge {
            if (weight < 0) {
                throw new IllegalArgumentException("weight must be non-negative");
            }
        }
    }

    private final Map<Integer, List<Edge>> adjacencyList = new LinkedHashMap<>();

    public WeightedGraph addVertex(int vertex) {
        adjacencyList.computeIfAbsent(vertex, ignored -> new ArrayList<>());
        return this;
    }

    public WeightedGraph addEdge(int from, int to, int weight) {
        addVertex(from);
        addVertex(to);
        adjacencyList.get(from).add(new Edge(to, weight));
        return this;
    }

    public WeightedGraph addUndirectedEdge(int first, int second, int weight) {
        addEdge(first, second, weight);
        addEdge(second, first, weight);
        return this;
    }

    public List<Edge> getEdges(int vertex) {
        List<Edge> edges = adjacencyList.get(vertex);
        return edges == null ? List.of() : List.copyOf(edges);
    }

    public boolean containsVertex(int vertex) {
        return adjacencyList.containsKey(vertex);
    }

    public Set<Integer> vertices() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(adjacencyList.keySet()));
    }

    public WeightedGraph copy() {
        WeightedGraph copy = new WeightedGraph();
        for (Map.Entry<Integer, List<Edge>> entry : adjacencyList.entrySet()) {
            Integer vertex = Objects.requireNonNull(entry.getKey(), "vertex");
            List<Edge> edges = List.copyOf(entry.getValue());
            copy.adjacencyList.put(vertex, new ArrayList<>(edges));
            for (Edge edge : edges) {
                Objects.requireNonNull(edge, "edge");
                copy.adjacencyList.computeIfAbsent(edge.to(), ignored -> new ArrayList<>());
            }
        }
        return copy;
    }

    public static WeightedGraph fromAdjacency(Map<Integer, List<Edge>> adjacency) {
        Objects.requireNonNull(adjacency, "adjacency");
        WeightedGraph graph = new WeightedGraph();
        for (Map.Entry<Integer, List<Edge>> entry : adjacency.entrySet()) {
            Integer vertex = Objects.requireNonNull(entry.getKey(), "vertex");
            graph.addVertex(vertex);

            List<Edge> edges = entry.getValue();
            if (edges == null) {
                continue;
            }

            for (Edge edge : edges) {
                Objects.requireNonNull(edge, "edge");
                graph.adjacencyList.get(vertex).add(edge);
                graph.adjacencyList.computeIfAbsent(edge.to(), ignored -> new ArrayList<>());
            }
        }
        return graph;
    }
}
