package org.example.task2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DfsTraversal {

    public List<DfsEvent> traverse(Graph graph, String startVertex) {
        if (!graph.containsVertex(startVertex)) {
            throw new IllegalArgumentException("Стартовой вершины нет в графе: " + startVertex);
        }

        List<DfsEvent> events = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        events.add(new DfsEvent(DfsPoint.START, startVertex, null));

        dfs(graph, startVertex, visited, events);

        events.add(new DfsEvent(DfsPoint.FINISH, startVertex, null));

        return events;
    }

    private void dfs(
            Graph graph,
            String currentVertex,
            Set<String> visited,
            List<DfsEvent> events
    ) {
        events.add(new DfsEvent(DfsPoint.ENTER_VERTEX, currentVertex, null));

        visited.add(currentVertex);
        events.add(new DfsEvent(DfsPoint.MARK_AS_VISITED, currentVertex, null));

        for (String neighbor : graph.getNeighbors(currentVertex)) {
            events.add(new DfsEvent(DfsPoint.CHECK_NEIGHBOR, currentVertex, neighbor));

            if (!visited.contains(neighbor)) {
                events.add(new DfsEvent(DfsPoint.GO_TO_UNVISITED_NEIGHBOR, currentVertex, neighbor));
                dfs(graph, neighbor, visited, events);
            } else {
                events.add(new DfsEvent(DfsPoint.SKIP_ALREADY_VISITED_NEIGHBOR, currentVertex, neighbor));
            }
        }

        events.add(new DfsEvent(DfsPoint.EXIT_VERTEX, currentVertex, null));
    }
}