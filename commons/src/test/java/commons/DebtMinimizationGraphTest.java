package commons;
import commons.DebtMinimizationGraph.Edge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class DebtMinimizationGraphTest {
    private DebtMinimizationGraph graph;
    @BeforeEach
    public void setUp() {
        graph = new DebtMinimizationGraph(5);
    }

    @Test
    public void testGraphInitialization() {
        assertEquals(5, graph.getGraph().size(), "Graph should be initialized with 5 vertices.");
    }

    @Test
    public void testAddEdgeAndVerify() {
        graph.addEdge(0, 1, 10);
        List<Edge> edgesFrom0 = graph.getEdgesForVertex(0);
        assertEquals(1, edgesFrom0.size(), "Node 0 should have 1 outgoing edge.");
        Edge edge = edgesFrom0.get(0);
        assertEquals(1, edge.getTo(), "The edge should go to node 1.");
        assertEquals(10, edge.getCapacity(), "The edge's capacity should be 10.");
    }

    @Test
    public void testMaxFlowSimpleScenario() {
        graph.addEdge(0, 1, 10);
        graph.addEdge(1, 2, 5);
        graph.addEdge(0, 2, 5);
        assertEquals(10, graph.maxFlow(0, 2), "Max flow from 0 to 2 should be 10.");
    }

    @Test
    public void testGetConnectedNodes() {
        graph.addEdge(0, 1, 10);
        graph.addEdge(1, 2, 5);
        graph.addEdge(0, 2, 5);
        List<Integer> connectedNodes = graph.getConnectedNodes(0);
        assertEquals(3, connectedNodes.size(), "There should be 3 nodes connected to node 0.");
        assertTrue(connectedNodes.containsAll(List.of(0, 1, 2)), "Connected nodes should include 0, 1, and 2.");
    }

    @Test
    public void testAddEdgeWithoutReverse() {
        graph.addEdgeWithoutReverse(0, 1, 15);
        List<Edge> edgesFrom0 = graph.getEdgesForVertex(0);
        assertEquals(1, edgesFrom0.size(), "Node 0 should have 1 outgoing edge.");
        Edge edge = edgesFrom0.get(0);
        assertEquals(1, edge.getTo(), "The edge should point to node 1.");
        assertEquals(15, edge.getCapacity(), "The edge's capacity should be 15.");
        List<Edge> edgesFrom1 = graph.getEdgesForVertex(1);
        assertTrue(edgesFrom1.isEmpty(), "Node 1 should not have outgoing edges.");
    }

    @Test
    public void testMaxFlowComplexScenario() {
        graph.addEdge(0, 1, 10);
        graph.addEdge(1, 2, 20);
        graph.addEdge(2, 3, 10);
        graph.addEdge(3, 4, 20);
        graph.addEdge(0, 2, 5);
        graph.addEdge(2, 4, 5);
        assertEquals(15, graph.maxFlow(0, 4), "Max flow from 0 to 4 should be 15.");
    }

    @Test
    public void testMinimizeDebtChains() {
        graph.addEdge(0, 1, 100);
        graph.addEdge(1, 2, 100);
        graph.addEdge(2, 3, 100);
        assertEquals(100, graph.maxFlow(0, 3), "Max flow from 0 to 3 should initially be 100.");
        graph.minimizeDebtChains(5);
        for (int i = 0; i < 5; ++i) {
            for (DebtMinimizationGraph.Edge edge: graph.getEdgesForVertex(i)) {
                edge.setFlow(0);
                System.out.println(i + "->" + edge.getTo() + " " + edge.getCapacity());
            }
        }
        assertEquals(100, graph.maxFlow(0, 3), "Max flow from 0 to 3 should still be 100 after minimization.");
        List<Edge> edgesFrom0 = graph.getEdgesForVertex(0);
        boolean directEdgeExists = edgesFrom0.stream().anyMatch(edge -> edge.getTo() == 3 && edge.getCapacity() == 100);
        assertTrue(directEdgeExists, "A direct edge from 0 to 3 with capacity 100 should exist after minimization.");
    }

    @Test
    public void testAddingEdgeToNonExistentVertex() {
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> graph.addEdge(0, 5, 10));
        assertTrue(exception.getMessage().contains("Index 5 out of bounds for length 5"));
    }
}