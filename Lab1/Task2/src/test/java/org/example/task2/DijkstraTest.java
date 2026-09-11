package org.example.task2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DijkstraTest {

    @Test
    void shouldFindShortestPath() {
        Dijkstra dijkstra = new Dijkstra();

        /*
         * Граф:
         *
         * A --4-- B --1-- C
         * |              |
         * 2              5
         * |              |
         * D -----1------ E
         *
         * Кратчайший путь из A в E:
         * A -> D -> E
         *
         * Длина пути:
         * 2 + 1 = 3
         */

        dijkstra.addEdge("A", "B", 4);
        dijkstra.addEdge("A", "D", 2);
        dijkstra.addEdge("B", "C", 1);
        dijkstra.addEdge("C", "E", 5);
        dijkstra.addEdge("D", "E", 1);

        Dijkstra.Result result = dijkstra.findShortestPath("A", "E");

        assertEquals(3, result.distance);
        assertEquals(List.of("A", "D", "E"), result.path);

        /*
         * Расширяющееся дерево:
         * алгоритм окончательно выбирает A, потом D, потом E.
         */
        assertEquals(List.of("A", "D", "E"), result.tree);

        assertTrue(result.trace.contains(Dijkstra.Point.START));
        assertTrue(result.trace.contains(Dijkstra.Point.TAKE_VERTEX));
        assertTrue(result.trace.contains(Dijkstra.Point.VISIT_VERTEX));
        assertTrue(result.trace.contains(Dijkstra.Point.CHECK_NEIGHBOR));
        assertTrue(result.trace.contains(Dijkstra.Point.UPDATE_DISTANCE));
        assertTrue(result.trace.contains(Dijkstra.Point.FINISH_FOUND));
        assertTrue(result.trace.contains(Dijkstra.Point.BUILD_PATH));
    }

    @Test
    void shouldReturnNoPathWhenFinishIsUnreachable() {
        Dijkstra dijkstra = new Dijkstra();

        /*
         * Две отдельные части графа:
         *
         * A --1-- B
         *
         * C --1-- D
         *
         * Пути из A в D нет.
         */

        dijkstra.addEdge("A", "B", 1);
        dijkstra.addEdge("C", "D", 1);

        Dijkstra.Result result = dijkstra.findShortestPath("A", "D");

        assertEquals(Integer.MAX_VALUE, result.distance);
        assertEquals(List.of(), result.path);

        assertEquals(List.of("A", "B"), result.tree);

        assertTrue(result.trace.contains(Dijkstra.Point.NO_PATH));
    }

    @Test
    void shouldHandleStartEqualsFinish() {
        Dijkstra dijkstra = new Dijkstra();

        dijkstra.addVertex("A");

        Dijkstra.Result result = dijkstra.findShortestPath("A", "A");

        assertEquals(0, result.distance);
        assertEquals(List.of("A"), result.path);
        assertEquals(List.of("A"), result.tree);

        assertEquals(
                List.of(
                        Dijkstra.Point.START,
                        Dijkstra.Point.TAKE_VERTEX,
                        Dijkstra.Point.VISIT_VERTEX,
                        Dijkstra.Point.FINISH_FOUND,
                        Dijkstra.Point.BUILD_PATH
                ),
                result.trace
        );
    }

    @Test
    void shouldThrowExceptionWhenWeightIsNegativeOrZero() {
        Dijkstra dijkstra = new Dijkstra();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dijkstra.addEdge("A", "B", 0)
        );

        assertEquals("Вес ребра должен быть положительным", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartVertexDoesNotExist() {
        Dijkstra dijkstra = new Dijkstra();

        dijkstra.addVertex("A");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dijkstra.findShortestPath("X", "A")
        );

        assertEquals("Нет начальной вершины", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFinishVertexDoesNotExist() {
        Dijkstra dijkstra = new Dijkstra();

        dijkstra.addVertex("A");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dijkstra.findShortestPath("A", "X")
        );

        assertEquals("Нет конечной вершины", exception.getMessage());
    }

    @Test
    void shouldSkipAlreadyVisitedVertexFromQueue() {
        Dijkstra dijkstra = new Dijkstra();

        /*
         * Граф:
         *
         * A --1-- B --1-- C
         * |              |
         * 10             1
         * |              |
         * C              D
         *
         * Сначала C попадёт в очередь с расстоянием 10.
         * Потом через B найдётся более короткое расстояние до C: 2.
         *
         * В очереди окажутся две записи для C.
         * Первая нормальная обработается.
         * Вторая позже попадёт в:
         *
         * if (visited.contains(current)) {
         *     continue;
         * }
         */

        dijkstra.addEdge("A", "B", 1);
        dijkstra.addEdge("A", "C", 10);
        dijkstra.addEdge("B", "C", 1);
        dijkstra.addEdge("C", "D", 1);

        /*
         * Добавляем отдельную недостижимую вершину E.
         * Благодаря этому алгоритм не остановится на D,
         * а продолжит разбирать очередь до конца.
         */
        dijkstra.addVertex("E");

        Dijkstra.Result result = dijkstra.findShortestPath("A", "E");

        assertEquals(Integer.MAX_VALUE, result.distance);
        assertEquals(List.of(), result.path);

        assertTrue(result.trace.contains(Dijkstra.Point.NO_PATH));
    }

    @Test
    void shouldNotUpdateDistanceWhenNewPathIsLonger() {
        Dijkstra dijkstra = new Dijkstra();

        /*
         * Граф:
         *
         * A --1-- B
         * |       |
         * 5       10
         * |       |
         * C --1-- D
         *
         * До C сначала найдётся путь A -> C = 5.
         * Потом алгоритм проверит путь A -> B -> C = 11.
         *
         * 11 хуже, чем 5, поэтому расстояние не обновится.
         * Это покроет ветку:
         *
         * else {
         *     trace.add(Point.NO_UPDATE);
         * }
         */

        dijkstra.addEdge("A", "B", 1);
        dijkstra.addEdge("A", "C", 5);
        dijkstra.addEdge("B", "C", 10);
        dijkstra.addEdge("C", "D", 1);

        Dijkstra.Result result = dijkstra.findShortestPath("A", "D");

        assertEquals(6, result.distance);
        assertEquals(List.of("A", "C", "D"), result.path);

        assertTrue(result.trace.contains(Dijkstra.Point.NO_UPDATE));
    }

    @Test
    void shouldFindShortestPathFromOneToSixForGraphFromPicture() {
        Dijkstra dijkstra = new Dijkstra();

        /*
         *
         *  * /--1----\
         *   / /   \   \
         *  | 2 ---- 3  |
         *  | | \  / |  |
         *  | |  \/  |  |
         *  | |  /\  |  |
         *  | | /  \ |  |
         *  | 5      4  |
         *  |        | /
         *  \--------6
         * Рёбра:
         * 1-2 = 1
         * 1-3 = 1
         * 1-6 = 2
         * 2-3 = 2
         * 2-5 = 1
         * 5-3 = 1
         * 2-4 = 2
         * 3-4 = 3
         * 4-6 = 1
         * 2-6 = 2
         */

        dijkstra.addEdge("1", "2", 1);
        dijkstra.addEdge("1", "3", 1);
        dijkstra.addEdge("1", "6", 2);

        dijkstra.addEdge("2", "3", 2);
        dijkstra.addEdge("2", "5", 1);
        dijkstra.addEdge("5", "3", 1);

        dijkstra.addEdge("2", "4", 2);
        dijkstra.addEdge("3", "4", 3);
        dijkstra.addEdge("4", "6", 1);
        dijkstra.addEdge("2", "6", 2);

        Dijkstra.Result result = dijkstra.findShortestPath("1", "6");

        assertEquals(2, result.distance);
        assertEquals(List.of("1", "6"), result.path);

        assertTrue(result.trace.contains(Dijkstra.Point.START));
        assertTrue(result.trace.contains(Dijkstra.Point.TAKE_VERTEX));
        assertTrue(result.trace.contains(Dijkstra.Point.VISIT_VERTEX));
        assertTrue(result.trace.contains(Dijkstra.Point.CHECK_NEIGHBOR));
        assertTrue(result.trace.contains(Dijkstra.Point.UPDATE_DISTANCE));
        assertTrue(result.trace.contains(Dijkstra.Point.FINISH_FOUND));
        assertTrue(result.trace.contains(Dijkstra.Point.BUILD_PATH));
    }
}