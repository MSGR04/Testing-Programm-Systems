package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DFSTest {

    static Stream<Arguments> graphProvider() {
        return Stream.of(
                Arguments.of(
                        "linear chain 1-2-3",
                        createUndirectedGraph(new int[][]{{1, 2}, {2, 3}}),
                        1,
                        linearTrace()
                ),
                Arguments.of(
                        "triangle cycle",
                        createUndirectedGraph(new int[][]{{1, 2}, {2, 3}, {3, 1}}),
                        1,
                        triangleTrace()
                ),
                Arguments.of(
                        "branching tree",
                        createUndirectedGraph(new int[][]{{1, 2}, {1, 3}, {2, 4}}),
                        1,
                        branchingTrace()
                ),
                Arguments.of(
                        "disconnected graph",
                        createUndirectedGraph(new int[][]{{1, 2}, {3, 4}}),
                        1,
                        disconnectedTrace()
                ),
                Arguments.of(
                        "self loop",
                        graphWithLoop(),
                        1,
                        selfLoopTrace()
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("graphProvider")
    @DisplayName("DFS must hit the characteristic points in the expected order")
    void shouldMatchExpectedTrace(String graphName,
                                  Map<Integer, List<Integer>> graph,
                                  int startVertex,
                                  List<DFSWithMarkers.TraceEvent> expectedTrace) {
        DFSWithMarkers dfs = new DFSWithMarkers(graph);

        List<DFSWithMarkers.TraceEvent> actualTrace = dfs.dfs(startVertex);

        assertEquals(
                expectedTrace,
                actualTrace,
                () -> graphName
                        + System.lineSeparator()
                        + "expected: " + DFSWithMarkers.formatTrace(expectedTrace)
                        + System.lineSeparator()
                        + "actual:   " + DFSWithMarkers.formatTrace(actualTrace)
        );
    }

    @Test
    @DisplayName("Missing start vertex is treated as an isolated vertex")
    void shouldTraverseMissingStartVertexAsIsolatedVertex() {
        DFSWithMarkers dfs = new DFSWithMarkers(createUndirectedGraph(new int[][]{{1, 2}, {2, 3}}));

        List<DFSWithMarkers.TraceEvent> actualTrace = dfs.dfs(99);

        assertEquals(List.of(
                enter(99),
                scan(99),
                exit(99)
        ), actualTrace);
    }

    @Test
    @DisplayName("Null graph must be rejected")
    void shouldRejectNullGraph() {
        assertThrows(NullPointerException.class, () -> new DFSWithMarkers(null));
    }

    private static Map<Integer, List<Integer>> createUndirectedGraph(int[][] edges) {
        Map<Integer, List<Integer>> graph = new LinkedHashMap<>();
        for (int[] edge : edges) {
            graph.computeIfAbsent(edge[0], ignored -> new ArrayList<>()).add(edge[1]);
            graph.computeIfAbsent(edge[1], ignored -> new ArrayList<>()).add(edge[0]);
        }
        return graph;
    }

    private static Map<Integer, List<Integer>> graphWithLoop() {
        Map<Integer, List<Integer>> graph = new LinkedHashMap<>();
        graph.put(1, List.of(1));
        return graph;
    }

    private static List<DFSWithMarkers.TraceEvent> linearTrace() {
        return List.of(
                enter(1),
                scan(1),
                check(1, 2),
                descend(1, 2),
                enter(2),
                scan(2),
                check(2, 1),
                check(2, 3),
                descend(2, 3),
                enter(3),
                scan(3),
                check(3, 2),
                exit(3),
                exit(2),
                exit(1)
        );
    }

    private static List<DFSWithMarkers.TraceEvent> triangleTrace() {
        return List.of(
                enter(1),
                scan(1),
                check(1, 2),
                descend(1, 2),
                enter(2),
                scan(2),
                check(2, 1),
                check(2, 3),
                descend(2, 3),
                enter(3),
                scan(3),
                check(3, 2),
                check(3, 1),
                exit(3),
                exit(2),
                check(1, 3),
                exit(1)
        );
    }

    private static List<DFSWithMarkers.TraceEvent> branchingTrace() {
        return List.of(
                enter(1),
                scan(1),
                check(1, 2),
                descend(1, 2),
                enter(2),
                scan(2),
                check(2, 1),
                check(2, 4),
                descend(2, 4),
                enter(4),
                scan(4),
                check(4, 2),
                exit(4),
                exit(2),
                check(1, 3),
                descend(1, 3),
                enter(3),
                scan(3),
                check(3, 1),
                exit(3),
                exit(1)
        );
    }

    private static List<DFSWithMarkers.TraceEvent> disconnectedTrace() {
        return List.of(
                enter(1),
                scan(1),
                check(1, 2),
                descend(1, 2),
                enter(2),
                scan(2),
                check(2, 1),
                exit(2),
                exit(1)
        );
    }

    private static List<DFSWithMarkers.TraceEvent> selfLoopTrace() {
        return List.of(
                enter(1),
                scan(1),
                check(1, 1),
                exit(1)
        );
    }

    private static DFSWithMarkers.TraceEvent enter(int vertex) {
        return DFSWithMarkers.TraceEvent.enter(vertex);
    }

    private static DFSWithMarkers.TraceEvent scan(int vertex) {
        return DFSWithMarkers.TraceEvent.startNeighborScan(vertex);
    }

    private static DFSWithMarkers.TraceEvent check(int vertex, int neighbor) {
        return DFSWithMarkers.TraceEvent.checkNeighbor(vertex, neighbor);
    }

    private static DFSWithMarkers.TraceEvent descend(int vertex, int neighbor) {
        return DFSWithMarkers.TraceEvent.recursiveDescent(vertex, neighbor);
    }

    private static DFSWithMarkers.TraceEvent exit(int vertex) {
        return DFSWithMarkers.TraceEvent.exit(vertex);
    }
}
