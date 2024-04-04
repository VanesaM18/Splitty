package commons;

import java.util.*;

public class DebtMinimizationGraph {
    public static class Edge {
        public int to, flow, capacity, reverseIndex;

        Edge(int to, int flow, int capacity, int reverseIndex) {
            this.to = to;
            this.flow = flow;
            this.capacity = capacity;
            this.reverseIndex = reverseIndex;
        }
    }

    private List<List<Edge>> graph;
    private int[] level;
    private int[] start;

    public DebtMinimizationGraph(int vertices) {
        graph = new ArrayList<>(vertices);
        level = new int[vertices];
        start = new int[vertices];
        for (int i = 0; i < vertices; i++) {
            graph.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int capacity) {
        graph.get(from).add(new Edge(to, 0, capacity, graph.get(to).size()));
        graph.get(to).add(
            new Edge(from, 0, 0, graph.get(from).size() - 1));
    }

    private boolean bfs(int source, int sink) {
        Arrays.fill(level, -1);
        level[source] = 0;
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(source);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (Edge edge : graph.get(node)) {
                if (level[edge.to] == -1 && edge.flow < edge.capacity) {
                    level[edge.to] = level[node] + 1;
                    queue.offer(edge.to);
                }
            }
        }
        return level[sink] != -1;
    }

    private int sendFlow(int node, int flow, int sink) {
        if (node == sink) return flow;

        for (; start[node] < graph.get(node).size(); start[node]++) {
            Edge edge = graph.get(node).get(start[node]);
            if (level[edge.to] == level[node] + 1 && edge.flow < edge.capacity) {
                int currentFlow = Math.min(flow, edge.capacity - edge.flow);
                int tempFlow = sendFlow(edge.to, currentFlow, sink);

                if (tempFlow > 0) {
                    edge.flow += tempFlow;
                    graph.get(edge.to).get(edge.reverseIndex).flow -= tempFlow;
                    return tempFlow;
                }
            }
        }
        return 0;
    }

    public int maxFlow(int source, int sink) {
        int totalFlow = 0;
        while (bfs(source, sink)) {
            Arrays.fill(start, 0);
            int flow;
            while ((flow = sendFlow(source, Integer.MAX_VALUE, sink)) != 0) {
                totalFlow += flow;
            }
        }
        return totalFlow;
    }
    public List<Edge> getEdgesForVertex(int vertex) {
        return graph.get(vertex);
    }
}
