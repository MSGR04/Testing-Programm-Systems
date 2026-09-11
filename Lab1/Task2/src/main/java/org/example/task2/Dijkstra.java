package org.example.task2;

import java.util.*;

public class Dijkstra {

    /*
     * Характерные точки алгоритма.
     */
    public enum Point {
        START,
        TAKE_VERTEX,
        VISIT_VERTEX,
        CHECK_NEIGHBOR,
        UPDATE_DISTANCE,
        NO_UPDATE,
        FINISH_FOUND,
        NO_PATH,
        BUILD_PATH
    }

    public static class Result {
        public final int distance;
        public final List<String> path;
        public final List<Point> trace;
        public final List<String> tree;

        public Result(int distance, List<String> path, List<Point> trace, List<String> tree) {
            this.distance = distance;
            this.path = path;
            this.trace = trace;
            this.tree = tree;
        }
    }

    private final Map<String, Map<String, Integer>> graph = new HashMap<>();

    public void addVertex(String name) {
        graph.putIfAbsent(name, new HashMap<>());
    }
    public void addEdge(String from, String to, int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Вес ребра должен быть положительным");
        }

        addVertex(from);
        addVertex(to);

        graph.get(from).put(to, weight);
        graph.get(to).put(from, weight);
    }

    /*
     * Алгоритм Дейкстры.
     */
    public Result findShortestPath(String start, String finish) {
        if (!graph.containsKey(start)) {
            throw new IllegalArgumentException("Нет начальной вершины");
        }

        if (!graph.containsKey(finish)) {
            throw new IllegalArgumentException("Нет конечной вершины");
        }

        List<Point> trace = new ArrayList<>();
        List<String> tree = new ArrayList<>();

        trace.add(Point.START);

        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String vertex : graph.keySet()) {
            distance.put(vertex, Integer.MAX_VALUE);
        }

        distance.put(start, 0);

        PriorityQueue<String> queue = new PriorityQueue<>(
                Comparator.comparingInt(distance::get)
        );

        queue.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            trace.add(Point.TAKE_VERTEX);

            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);
            tree.add(current);
            trace.add(Point.VISIT_VERTEX);

            if (current.equals(finish)) {
                trace.add(Point.FINISH_FOUND);
                trace.add(Point.BUILD_PATH);

                return new Result(
                        distance.get(finish),
                        buildPath(previous, start, finish),
                        trace,
                        tree
                );
            }

            for (String neighbor : graph.get(current).keySet()) {
                trace.add(Point.CHECK_NEIGHBOR);

                if (visited.contains(neighbor)) {
                    trace.add(Point.NO_UPDATE);
                    continue;
                }

                int newDistance = distance.get(current) + graph.get(current).get(neighbor);

                if (newDistance < distance.get(neighbor)) {
                    distance.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                    trace.add(Point.UPDATE_DISTANCE);
                } else {
                    trace.add(Point.NO_UPDATE);
                }
            }
        }

        trace.add(Point.NO_PATH);

        return new Result(
                Integer.MAX_VALUE,
                List.of(),
                trace,
                tree
        );
    }

    /*
     * Восстановление пути.
     */
    private List<String> buildPath(Map<String, String> previous, String start, String finish) {
        LinkedList<String> path = new LinkedList<>();

        String current = finish;

        while (current != null) {
            path.addFirst(current);

            if (current.equals(start)) {
                break;
            }

            current = previous.get(current);
        }

        return path;
    }
}