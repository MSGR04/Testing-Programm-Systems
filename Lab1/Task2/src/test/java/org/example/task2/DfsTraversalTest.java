package org.example.task2;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DfsTraversalTest {

    private static DfsEvent event(DfsPoint point, String vertex, String neighbor) {
        return new DfsEvent(point, vertex, neighbor);
    }

    @Test
    void dfsShouldVisitGraphAndMatchExpectedCharacteristicPoints() {
        Graph graph = new Graph();

        /*
         * Граф:
         *
         *     A
         *    / \
         *   B   C
         *    \ /
         *     D
         *
         * Рёбра добавлены в таком порядке:
         * A-B
         * A-C
         * B-D
         * C-D
         *
         * Поэтому соседи A: B, C.
         * DFS из A сначала пойдёт в B.
         */
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");

        DfsTraversal traversal = new DfsTraversal();

        List<DfsEvent> actualEvents = traversal.traverse(graph, "A");

        List<DfsEvent> expectedEvents = List.of(
                event(DfsPoint.START, "A", null),

                event(DfsPoint.ENTER_VERTEX, "A", null),
                event(DfsPoint.MARK_AS_VISITED, "A", null),

                event(DfsPoint.CHECK_NEIGHBOR, "A", "B"),
                event(DfsPoint.GO_TO_UNVISITED_NEIGHBOR, "A", "B"),

                event(DfsPoint.ENTER_VERTEX, "B", null),
                event(DfsPoint.MARK_AS_VISITED, "B", null),

                event(DfsPoint.CHECK_NEIGHBOR, "B", "A"),
                event(DfsPoint.SKIP_ALREADY_VISITED_NEIGHBOR, "B", "A"),

                event(DfsPoint.CHECK_NEIGHBOR, "B", "D"),
                event(DfsPoint.GO_TO_UNVISITED_NEIGHBOR, "B", "D"),

                event(DfsPoint.ENTER_VERTEX, "D", null),
                event(DfsPoint.MARK_AS_VISITED, "D", null),

                event(DfsPoint.CHECK_NEIGHBOR, "D", "B"),
                event(DfsPoint.SKIP_ALREADY_VISITED_NEIGHBOR, "D", "B"),

                event(DfsPoint.CHECK_NEIGHBOR, "D", "C"),
                event(DfsPoint.GO_TO_UNVISITED_NEIGHBOR, "D", "C"),

                event(DfsPoint.ENTER_VERTEX, "C", null),
                event(DfsPoint.MARK_AS_VISITED, "C", null),

                event(DfsPoint.CHECK_NEIGHBOR, "C", "A"),
                event(DfsPoint.SKIP_ALREADY_VISITED_NEIGHBOR, "C", "A"),

                event(DfsPoint.CHECK_NEIGHBOR, "C", "D"),
                event(DfsPoint.SKIP_ALREADY_VISITED_NEIGHBOR, "C", "D"),

                event(DfsPoint.EXIT_VERTEX, "C", null),
                event(DfsPoint.EXIT_VERTEX, "D", null),
                event(DfsPoint.EXIT_VERTEX, "B", null),

                event(DfsPoint.CHECK_NEIGHBOR, "A", "C"),
                event(DfsPoint.SKIP_ALREADY_VISITED_NEIGHBOR, "A", "C"),

                event(DfsPoint.EXIT_VERTEX, "A", null),

                event(DfsPoint.FINISH, "A", null)
        );

        assertEquals(expectedEvents, actualEvents);
    }

    @Test
    void dfsShouldWorkForSingleIsolatedVertex() {
        Graph graph = new Graph();
        graph.addVertex("A");

        DfsTraversal traversal = new DfsTraversal();

        List<DfsEvent> actualEvents = traversal.traverse(graph, "A");

        List<DfsEvent> expectedEvents = List.of(
                event(DfsPoint.START, "A", null),
                event(DfsPoint.ENTER_VERTEX, "A", null),
                event(DfsPoint.MARK_AS_VISITED, "A", null),
                event(DfsPoint.EXIT_VERTEX, "A", null),
                event(DfsPoint.FINISH, "A", null)
        );

        assertEquals(expectedEvents, actualEvents);
    }

    @Test
    void dfsShouldThrowExceptionWhenStartVertexDoesNotExist() {
        Graph graph = new Graph();
        graph.addVertex("A");

        DfsTraversal traversal = new DfsTraversal();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> traversal.traverse(graph, "X")
        );

        assertEquals("Стартовой вершины нет в графе: X", exception.getMessage());
    }

//    @Test
//    void dfsEventEqualsShouldReturnTrueForSameObject() {
//        DfsEvent event = new DfsEvent(DfsPoint.START, "A", null);
//
//        assertEquals(event, event);
//    }
//
//    @Test
//    void dfsEventEqualsShouldReturnFalseForDifferentClass() {
//        DfsEvent event = new DfsEvent(DfsPoint.START, "A", null);
//
//        assertNotEquals(event, "not event");
//    }

    @Test
    void dfsEventGettersHashCodeAndToStringShouldWork() {
        DfsEvent event = new DfsEvent(DfsPoint.CHECK_NEIGHBOR, "A", "B");
        DfsEvent sameEvent = new DfsEvent(DfsPoint.CHECK_NEIGHBOR, "A", "B");

        assertEquals(DfsPoint.CHECK_NEIGHBOR, event.getPoint());
        assertEquals("A", event.getVertex());
        assertEquals("B", event.getNeighbor());

        assertEquals(event.hashCode(), sameEvent.hashCode());
        assertTrue(event.toString().contains("CHECK_NEIGHBOR"));
        assertTrue(event.toString().contains("A"));
        assertTrue(event.toString().contains("B"));
    }



}