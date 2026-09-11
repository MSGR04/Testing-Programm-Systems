package org.example.task2;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void addVertexShouldAddSingleVertex() {
        Graph graph = new Graph();

        graph.addVertex("A");

        assertTrue(graph.containsVertex("A"));
        assertEquals(List.of(), graph.getNeighbors("A"));
    }

    @Test
    void addEdgeShouldCreateUndirectedConnection() {
        Graph graph = new Graph();

        graph.addEdge("A", "B");

        assertTrue(graph.containsVertex("A"));
        assertTrue(graph.containsVertex("B"));

        assertEquals(List.of("B"), graph.getNeighbors("A"));
        assertEquals(List.of("A"), graph.getNeighbors("B"));
    }

    @Test
    void getNeighborsForUnknownVertexShouldReturnEmptyList() {
        Graph graph = new Graph();

        assertEquals(List.of(), graph.getNeighbors("UNKNOWN"));
    }

    @Test
    void addDuplicateEdgeShouldNotDuplicateNeighbors() {
        Graph graph = new Graph();

        graph.addEdge("A", "B");
        graph.addEdge("A", "B");
        graph.addEdge("A", "B");

        assertEquals(List.of("B"), graph.getNeighbors("A"));
        assertEquals(List.of("A"), graph.getNeighbors("B"));
    }
}