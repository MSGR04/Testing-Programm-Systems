package org.example.task2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class Graph {

    /*
     * Map хранит список смежности.
     *
     * LinkedHashMap и LinkedHashSet нужны для того,
     * чтобы порядок обхода был предсказуемым.
     *
     * Например, если мы добавили ребро A-B, а потом A-C,
     * то соседи A будут идти именно так: B, C.
     */
    private final Map<String, LinkedHashSet<String>> adjacencyList = new LinkedHashMap<>();

    public void addVertex(String vertex) {
        adjacencyList.putIfAbsent(vertex, new LinkedHashSet<>());
    }

    public void addEdge(String firstVertex, String secondVertex) {
        addVertex(firstVertex);
        addVertex(secondVertex);

        adjacencyList.get(firstVertex).add(secondVertex);
        adjacencyList.get(secondVertex).add(firstVertex);
    }

    public boolean containsVertex(String vertex) {
        return adjacencyList.containsKey(vertex);
    }

    public List<String> getNeighbors(String vertex) {
        if (!containsVertex(vertex)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(adjacencyList.get(vertex));
    }
}