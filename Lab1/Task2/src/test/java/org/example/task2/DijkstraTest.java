package org.example.task2;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DijkstraTest {

    static Stream<Arguments> graphProvider() {
        return Stream.of(
                Arguments.of(
                        "linear chain",
                        weightedGraph(new int[][]{{1, 2, 4}, {2, 3, 3}}),
                        1,
                        Map.of(1, 0, 2, 4, 3, 7),
                        linearTrace()
                ),
                Arguments.of(
                        "better alternative path",
                        weightedGraph(new int[][]{{1, 2, 10}, {1, 3, 2}, {3, 2, 3}, {2, 4, 1}, {3, 4, 9}}),
                        1,
                        Map.of(1, 0, 2, 5, 3, 2, 4, 6),
                        betterPathTrace()
                ),
                Arguments.of(
                        "non-improving relaxation",
                        weightedGraph(new int[][]{{1, 2, 1}, {1, 3, 4}, {2, 3, 5}}),
                        1,
                        Map.of(1, 0, 2, 1, 3, 4),
                        nonImprovingTrace()
                ),
                Arguments.of(
                        "unreachable vertex",
                        graphWithIsolatedVertex(3).addEdge(1, 2, 5),
                        1,
                        distances(1, 0, 2, 5, 3, DijkstraWithMarkers.INFINITY),
                        unreachableTrace()
                ),
                Arguments.of(
                        "isolated start vertex",
                        graphWithIsolatedVertex(7),
                        7,
                        Map.of(7, 0),
                        isolatedTrace(7)
                ),
                Arguments.of(
                        "missing start vertex",
                        weightedGraph(new int[][]{{1, 2, 5}}),
                        99,
                        Map.of(1, DijkstraWithMarkers.INFINITY, 2, DijkstraWithMarkers.INFINITY, 99, 0),
                        isolatedTrace(99)
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("graphProvider")
    @DisplayName("Dijkstra must hit the characteristic points in the expected order")
    void shouldMatchExpectedTrace(String graphName,
                                  WeightedGraph graph,
                                  int startVertex,
                                  Map<Integer, Integer> expectedDistances,
                                  List<DijkstraWithMarkers.TraceEvent> expectedTrace) {
        DijkstraWithMarkers dijkstra = new DijkstraWithMarkers(graph);
        DijkstraWithMarkers.Result result = dijkstra.shortestPaths(startVertex);

        assertEquals(
                expectedTrace,
                result.trace(),
                () -> graphName + System.lineSeparator()
                        + "expected: " + DijkstraWithMarkers.formatTrace(expectedTrace) + System.lineSeparator()
                        + "actual:   " + DijkstraWithMarkers.formatTrace(result.trace())
        );
        assertEquals(expectedDistances, result.distances(), graphName);
    }

    @Test
    @DisplayName("Graph must expose directed weighted adjacency")
    void shouldStoreDirectedEdges() {
        WeightedGraph graph = new WeightedGraph().addEdge(1, 2, 7).addEdge(2, 3, 4);

        assertEquals(List.of(new WeightedGraph.Edge(2, 7)), graph.getEdges(1));
        assertEquals(List.of(new WeightedGraph.Edge(3, 4)), graph.getEdges(2));
        assertEquals(List.of(), graph.getEdges(3));
    }

    @Test
    @DisplayName("Graph must expose undirected weighted adjacency when requested")
    void shouldStoreUndirectedEdges() {
        WeightedGraph graph = new WeightedGraph().addUndirectedEdge(1, 2, 6);

        assertEquals(List.of(new WeightedGraph.Edge(2, 6)), graph.getEdges(1));
        assertEquals(List.of(new WeightedGraph.Edge(1, 6)), graph.getEdges(2));
    }

    @Test
    @DisplayName("Graph must return empty edge list for missing vertex")
    void shouldReturnEmptyEdgesForMissingVertex() {
        WeightedGraph graph = new WeightedGraph().addEdge(1, 2, 7);

        assertEquals(List.of(), graph.getEdges(99));
        assertFalse(graph.containsVertex(99));
    }

    @Test
    @DisplayName("Graph copy must be independent from source graph")
    void shouldCopyGraphIndependently() {
        WeightedGraph source = new WeightedGraph().addEdge(1, 2, 5);
        WeightedGraph copy = source.copy();

        source.addEdge(1, 3, 1);

        assertEquals(List.of(new WeightedGraph.Edge(2, 5)), copy.getEdges(1));
        assertEquals(List.of(new WeightedGraph.Edge(2, 5), new WeightedGraph.Edge(3, 1)), source.getEdges(1));
    }

    @Test
    @DisplayName("Graph must expose known vertices")
    void shouldExposeVertices() {
        WeightedGraph graph = new WeightedGraph().addEdge(1, 2, 5).addVertex(7);

        assertTrue(graph.vertices().containsAll(List.of(1, 2, 7)));
    }

    @Test
    @DisplayName("Null adjacency map must be rejected")
    void shouldRejectNullAdjacencyMap() {
        assertThrows(NullPointerException.class, () -> WeightedGraph.fromAdjacency(null));
    }

    @Test
    @DisplayName("Null vertex key in adjacency map must be rejected")
    void shouldRejectGraphWithNullVertexKey() {
        Map<Integer, List<WeightedGraph.Edge>> invalidGraph = new LinkedHashMap<>();
        invalidGraph.put(null, List.of(new WeightedGraph.Edge(1, 1)));

        assertThrows(NullPointerException.class, () -> WeightedGraph.fromAdjacency(invalidGraph));
    }

    @Test
    @DisplayName("Null edge in adjacency map must be rejected")
    void shouldRejectGraphWithNullEdge() {
        Map<Integer, List<WeightedGraph.Edge>> invalidGraph = new LinkedHashMap<>();
        invalidGraph.put(1, new ArrayList<>(List.of(new WeightedGraph.Edge(2, 3))));
        invalidGraph.get(1).add(null);

        assertThrows(NullPointerException.class, () -> WeightedGraph.fromAdjacency(invalidGraph));
    }

    @Test
    @DisplayName("Adjacency map may contain null edge list for isolated vertex")
    void shouldAcceptNullEdgeListInAdjacencyMap() {
        Map<Integer, List<WeightedGraph.Edge>> adjacency = new LinkedHashMap<>();
        adjacency.put(1, null);
        adjacency.put(2, List.of(new WeightedGraph.Edge(3, 4)));

        WeightedGraph graph = WeightedGraph.fromAdjacency(adjacency);

        assertEquals(List.of(), graph.getEdges(1));
        assertEquals(List.of(new WeightedGraph.Edge(3, 4)), graph.getEdges(2));
        assertTrue(graph.vertices().containsAll(List.of(1, 2, 3)));
    }

    @Test
    @DisplayName("Negative edge weight must be rejected")
    void shouldRejectNegativeWeight() {
        assertThrows(IllegalArgumentException.class, () -> new WeightedGraph.Edge(2, -1));
        assertThrows(IllegalArgumentException.class, () -> new WeightedGraph().addEdge(1, 2, -1));
    }

    @Test
    @DisplayName("Dijkstra module must make defensive copy of source graph")
    void shouldMakeDefensiveCopyOfSourceGraph() {
        WeightedGraph source = weightedGraph(new int[][]{{1, 2, 5}});
        DijkstraWithMarkers dijkstra = new DijkstraWithMarkers(source);

        source.addEdge(1, 3, 1);

        assertEquals(Map.of(1, 0, 2, 5), dijkstra.shortestPaths(1).distances());
    }

    @Test
    @DisplayName("Repeated runs must produce fresh immutable results")
    void shouldReturnFreshTraceForEachInvocation() {
        DijkstraWithMarkers dijkstra = new DijkstraWithMarkers(weightedGraph(new int[][]{{1, 2, 4}}));
        DijkstraWithMarkers.Result firstResult = dijkstra.shortestPaths(1);
        DijkstraWithMarkers.Result secondResult = dijkstra.shortestPaths(1);

        assertEquals(linearSingleEdgeTrace(), firstResult.trace());
        assertEquals(linearSingleEdgeTrace(), secondResult.trace());
        assertThrows(UnsupportedOperationException.class, () -> firstResult.trace().add(finish()));
        assertThrows(UnsupportedOperationException.class, () -> firstResult.distances().put(99, 0));
    }

    @Test
    @DisplayName("Null graph must be rejected")
    void shouldRejectNullGraph() {
        assertThrows(NullPointerException.class, () -> new DijkstraWithMarkers(null));
    }

    @Test
    @DisplayName("Dijkstra must skip edge to already settled vertex")
    void shouldSkipEdgeToSettledVertex() {
        WeightedGraph graph = new WeightedGraph().addUndirectedEdge(1, 2, 5);
        DijkstraWithMarkers.Result result = new DijkstraWithMarkers(graph).shortestPaths(1);

        assertEquals(
                List.of(
                        init(1),
                        select(1, 0), settle(1, 0), scan(1), check(1, 2, 5), relax(1, 2, 5),
                        select(2, 5), settle(2, 5), scan(2), check(2, 1, 10), skip(2, 1, 10),
                        stop(), finish()
                ),
                result.trace()
        );
        assertEquals(Map.of(1, 0, 2, 5), result.distances());
    }

    @Test
    @DisplayName("Trace formatting must join all events in order")
    void shouldFormatTrace() {
        List<DijkstraWithMarkers.TraceEvent> trace = List.of(
                init(1), select(1, 0), settle(1, 0), scan(1), check(1, 2, 4), relax(1, 2, 4), stop(), finish()
        );

        assertEquals(
                "INIT(1=0) -> SELECT(1=0) -> SETTLE(1=0) -> SCAN(1) -> CHECK(1->2=4) -> RELAX(1->2=4) -> STOP -> FINISH",
                DijkstraWithMarkers.formatTrace(trace)
        );
    }

    @Test
    @DisplayName("Empty trace formatting must return empty string")
    void shouldFormatEmptyTrace() {
        assertEquals("", DijkstraWithMarkers.formatTrace(List.of()));
    }

    @Test
    @DisplayName("Trace events must expose readable string forms")
    void shouldRenderTraceEventsAsReadableStrings() {
        assertEquals("INIT(1=0)", init(1).toString());
        assertEquals("SELECT(2=5)", select(2, 5).toString());
        assertEquals("SETTLE(2=5)", settle(2, 5).toString());
        assertEquals("SCAN(3)", scan(3).toString());
        assertEquals("CHECK(3->4=7)", check(3, 4, 7).toString());
        assertEquals("RELAX(3->4=7)", relax(3, 4, 7).toString());
        assertEquals("SKIP(4->1=9)", skip(4, 1, 9).toString());
        assertEquals("STOP", stop().toString());
        assertEquals("FINISH", finish().toString());
    }

    @Test
    @DisplayName("Trace event must reject null point")
    void shouldRejectTraceEventWithNullPoint() {
        assertThrows(NullPointerException.class, () -> new DijkstraWithMarkers.TraceEvent(null, 1, 2, 3));
    }

    private static WeightedGraph weightedGraph(int[][] edges) {
        WeightedGraph graph = new WeightedGraph();
        for (int[] edge : edges) {
            graph.addEdge(edge[0], edge[1], edge[2]);
        }
        return graph;
    }

    private static WeightedGraph graphWithIsolatedVertex(int vertex) {
        return new WeightedGraph().addVertex(vertex);
    }

    private static Map<Integer, Integer> distances(int... values) {
        Map<Integer, Integer> result = new LinkedHashMap<>();
        for (int index = 0; index < values.length; index += 2) {
            result.put(values[index], values[index + 1]);
        }
        return result;
    }

    private static List<DijkstraWithMarkers.TraceEvent> linearTrace() {
        return List.of(
                init(1),
                select(1, 0), settle(1, 0), scan(1), check(1, 2, 4), relax(1, 2, 4),
                select(2, 4), settle(2, 4), scan(2), check(2, 3, 7), relax(2, 3, 7),
                select(3, 7), settle(3, 7), scan(3),
                stop(), finish()
        );
    }

    private static List<DijkstraWithMarkers.TraceEvent> linearSingleEdgeTrace() {
        return List.of(
                init(1),
                select(1, 0), settle(1, 0), scan(1), check(1, 2, 4), relax(1, 2, 4),
                select(2, 4), settle(2, 4), scan(2),
                stop(), finish()
        );
    }

    private static List<DijkstraWithMarkers.TraceEvent> betterPathTrace() {
        return List.of(
                init(1),
                select(1, 0), settle(1, 0), scan(1), check(1, 2, 10), relax(1, 2, 10), check(1, 3, 2), relax(1, 3, 2),
                select(3, 2), settle(3, 2), scan(3), check(3, 2, 5), relax(3, 2, 5), check(3, 4, 11), relax(3, 4, 11),
                select(2, 5), settle(2, 5), scan(2), check(2, 4, 6), relax(2, 4, 6),
                select(4, 6), settle(4, 6), scan(4),
                stop(), finish()
        );
    }

    private static List<DijkstraWithMarkers.TraceEvent> nonImprovingTrace() {
        return List.of(
                init(1),
                select(1, 0), settle(1, 0), scan(1), check(1, 2, 1), relax(1, 2, 1), check(1, 3, 4), relax(1, 3, 4),
                select(2, 1), settle(2, 1), scan(2), check(2, 3, 6), skip(2, 3, 6),
                select(3, 4), settle(3, 4), scan(3),
                stop(), finish()
        );
    }

    private static List<DijkstraWithMarkers.TraceEvent> unreachableTrace() {
        return List.of(
                init(1),
                select(1, 0), settle(1, 0), scan(1), check(1, 2, 5), relax(1, 2, 5),
                select(2, 5), settle(2, 5), scan(2),
                stop(), finish()
        );
    }

    private static List<DijkstraWithMarkers.TraceEvent> isolatedTrace(int vertex) {
        return List.of(init(vertex), select(vertex, 0), settle(vertex, 0), scan(vertex), stop(), finish());
    }

    private static DijkstraWithMarkers.TraceEvent init(int vertex) {
        return DijkstraWithMarkers.TraceEvent.initializeSource(vertex);
    }

    private static DijkstraWithMarkers.TraceEvent select(int vertex, int distance) {
        return DijkstraWithMarkers.TraceEvent.selectVertex(vertex, distance);
    }

    private static DijkstraWithMarkers.TraceEvent settle(int vertex, int distance) {
        return DijkstraWithMarkers.TraceEvent.markSettled(vertex, distance);
    }

    private static DijkstraWithMarkers.TraceEvent scan(int vertex) {
        return DijkstraWithMarkers.TraceEvent.startEdgeScan(vertex);
    }

    private static DijkstraWithMarkers.TraceEvent check(int vertex, int neighbor, int candidateDistance) {
        return DijkstraWithMarkers.TraceEvent.checkEdge(vertex, neighbor, candidateDistance);
    }

    private static DijkstraWithMarkers.TraceEvent relax(int vertex, int neighbor, int newDistance) {
        return DijkstraWithMarkers.TraceEvent.relaxEdge(vertex, neighbor, newDistance);
    }

    private static DijkstraWithMarkers.TraceEvent skip(int vertex, int neighbor, int candidateDistance) {
        return DijkstraWithMarkers.TraceEvent.skipEdge(vertex, neighbor, candidateDistance);
    }

    private static DijkstraWithMarkers.TraceEvent stop() {
        return DijkstraWithMarkers.TraceEvent.noReachableVertex();
    }

    private static DijkstraWithMarkers.TraceEvent finish() {
        return DijkstraWithMarkers.TraceEvent.finish();
    }
}
