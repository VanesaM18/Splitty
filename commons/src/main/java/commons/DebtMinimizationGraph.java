package commons;

import java.util.*;

public class DebtMinimizationGraph {
    public static class Edge {
        private int to, flow, capacity, reverseIndex;

        Edge(int to, int flow, int capacity, int reverseIndex) {
            this.to = to;
            this.flow = flow;
            this.capacity = capacity;
            this.reverseIndex = reverseIndex;
        }

        /**
         * Gets the to
         * @return the to
         */
        public int getTo() {
            return to;
        }

        /**
         * Gets the flow
         * @return the flow
         */
        public int getFlow() {
            return flow;
        }

        /**
         * Gets the capacity
         * @return the capacity
         */
        public int getCapacity() {
            return capacity;
        }

        /**
         * Sets a new value for the flow
         * @param flow the flow value
         */
        public void setFlow(int flow) {
            this.flow = flow;
        }
    }

    private int maxChainValue;
    private int firstValueFinal;
    private List<List<Edge>> graph;
    private int[] level;
    private int[] start;

    /**
     * Creates an instance of DebtMinimizationGraph
     * @param vertices the number of vertices for the graph
     */
    public DebtMinimizationGraph(int vertices) {
        graph = new ArrayList<>(vertices);
        level = new int[vertices];
        start = new int[vertices];
        for (int i = 0; i < vertices; i++) {
            graph.add(new ArrayList<>());
        }
    }

    /**
     * Adds an edge
     * @param from the node to start the edge
     * @param to the node to finish the edge
     * @param capacity the capacity of the edge
     */
    public void addEdge(int from, int to, int capacity) {
        graph.get(from).add(new Edge(to, 0, capacity, graph.get(to).size()));
        graph.get(to).add(
            new Edge(from, 0, 0, graph.get(from).size() - 1));
    }

    /**
     * Adds an edge without the reverse one
     * @param from the node to start the edge
     * @param to the node to finish the edge
     * @param capacity the capacity of the edge
     */
    public void addEdgeWithoutReverse(int from, int to, int capacity) {
        graph.get(from).add(new Edge(to, 0, capacity, graph.get(to).size()));
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

    /**
     * Calculates the maxFlow
     * @param source the source
     * @param sink the sink
     * @return the max flow
     */
    public int maxFlow(int source, int sink) {
        if (source == sink) {
            return -1;
        }
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

    /**
     * Gets the edges list for a vertex
     * @param vertex the vertex
     * @return the edges list
     */
    public List<Edge> getEdgesForVertex(int vertex) {
        return graph.get(vertex);
    }

    /**
     * Gets all nodes in the same connected component
     * @param from the node to start from
     * @return the list of nodes
     */
    public List<Integer> getConnectedNodes(int from) {
        List<Integer> reachableNodes = new ArrayList<>();
        boolean[] visited = new boolean[graph.size()];
        Arrays.fill(visited, false);
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(from);
        visited[from] = true;
        while (!queue.isEmpty()) {
            int currentNode = queue.poll();
            reachableNodes.add(currentNode);
            for (Edge edge : graph.get(currentNode)) {
                if (!visited[edge.to] && edge.capacity != 0) {
                    visited[edge.to] = true;
                    queue.offer(edge.to);
                }
            }
        }
        return reachableNodes;
    }

    /**
     * Tries to minimize the debts by removing chains in which the payment can be done faster
     * @param participantCount the number of participants (or nodes)
     */
    public void minimizeDebtChains(int participantCount) {
        HashSet<Integer> visited = new HashSet<>();
        while (true) {
            int which = getWhich(participantCount, visited);
            if (which == -1) {
                break;
            }
            visited.add(which);
            while (true) {
                List<Integer> maxChain = new ArrayList<>();
                maxChainValue = Integer.MIN_VALUE;
                firstValueFinal = -1;
                HashSet<Integer> visitedChain = new HashSet<>();
                findMaxChain(which, new ArrayList<>(), 0, maxChain, visitedChain, -1);
                if (maxChain.size() > 2) {
                    for (int i = 0; i < maxChain.size() - 1; i++) {
                        removeEdgeOrUpdate(maxChain.get(i), maxChain.get(i + 1));
                    }
                    int from = maxChain.get(0);
                    int to = maxChain.get(maxChain.size() - 1);
                    addEdge(from, to, firstValueFinal);
                } else {
                    break;
                }
            }
        }
    }

    private int getWhich(int participantCount, HashSet<Integer> visited) {
        Map<Integer, Integer> inDegrees = new HashMap<>();
        initialize(participantCount, inDegrees);
        for (List<Edge> edges : getGraph()) {
            for (Edge edge : edges) {
                if (edge.capacity != 0) {
                    inDegrees.put(edge.to, inDegrees.getOrDefault(edge.to, 0) + 1);
                }
            }
        }
        int minn = Integer.MAX_VALUE;
        int which = -1;
        for (int i = 0; i < participantCount; i++) {
            if (inDegrees.get(i) < minn && !visited.contains(i)) {
                minn = inDegrees.get(i);
                which = i;
            }
        }
        return which;
    }

    private void initialize(int participantCount, Map<Integer, Integer> inDegrees) {
        for (int i = 0; i < participantCount; i++) {
            inDegrees.put(i, 0);
        }
    }

    private void findMaxChain(int currentNode, List<Integer> currentChain, int currentSum,
                              List<Integer> maxChain,
                              HashSet<Integer> visitedChain, int firstValue) {
        currentChain.add(currentNode);
        visitedChain.add(currentNode);
        boolean isEndNode = true;
        for (DebtMinimizationGraph.Edge edge : getGraph().get(currentNode)) {
            if (edge.capacity > 0 && !visitedChain.contains(edge.to)
                && (edge.capacity <= firstValue || firstValue == -1)) {
                isEndNode = false;
                if (firstValue == -1) {
                    firstValue = edge.capacity;
                }
                findMaxChain(edge.to, new ArrayList<>(currentChain),
                    currentSum + edge.capacity, maxChain, visitedChain, firstValue);
            }
        }
        if (isEndNode && currentChain.size() > maxChainValue && firstValue != -1) {
            maxChainValue = currentChain.size();
            firstValueFinal = firstValue;
            maxChain.clear();
            maxChain.addAll(currentChain);
        }
    }

    private void removeEdgeOrUpdate(int from, int to) {
        for (Edge e: getGraph().get(from)) {
            if (e.to == to) {
                e.capacity -= firstValueFinal;
                break;
            }
        }
        getGraph().get(from).removeIf(edge -> edge.to == to && edge.capacity == 0);
    }

    /**
     * Gets the graph
     * @return the graph
     */
    public List<List<Edge>> getGraph() {
        return graph;
    }
}
