package com.probendi.itgraph;

import com.probendi.itgraph.edge.Edge;
import com.probendi.itgraph.edge.EdgeService;
import com.probendi.itgraph.node.Node;
import com.probendi.itgraph.node.NodeService;
import com.probendi.itgraph.node.NodeType;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static com.probendi.itgraph.GraphResource.STATUS_UP;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class GraphResourceTest {

    @Inject
    EdgeService edgeService;
    @Inject
    NodeService nodeService;

    @BeforeEach
    @Transactional
    public void setup() {
        nodeService.deleteAllNodes();
    }

    @Test
    void testHealth() {
        given()
                .when().get("/graph/health")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(is(STATUS_UP));
    }

    @Test
    public void testGet() throws SQLException {
        Node a = new Node("a", 10, 20, NodeType.OPPOSITION);
        Node b = new Node("b", 30, 40, NodeType.LEXEME);
        Node c = new Node("c", 50, 60, NodeType.LEXEME);
        Edge ab = new Edge("a", "b");
        Edge ac = new Edge("a", "c");

        addNodesAndEdges(List.of(a, b, c), List.of(ab, ac));

        Graph graph = new Graph();
        graph.setNodes(List.of(a, b, c));
        graph.setEdges(List.of(ab, ac));

        Graph actualGraph = given()
                .when().get("/graph")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType("application/json")
                .extract().as(Graph.class);

        assertEquals(graph, actualGraph);
    }

    @Test
    public void testPut_OK() {

        Node a = new Node("a", 10, 20, NodeType.OPPOSITION);
        Node b = new Node("b", 30, 40, NodeType.LEXEME);
        Node c = new Node("c", 50, 60, NodeType.LEXEME);
        Edge ab = new Edge("a", "b");
        Edge ac = new Edge("a", "c");

        Graph graph = new Graph();
        graph.setNodes(List.of(a, b, c));
        graph.setEdges(List.of(ab, ac));

        Graph actualGraph = given()
                .contentType(ContentType.JSON)
                .body(graph)
                .when().put("/graph")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(Graph.class);

        assertEquals(graph, actualGraph);
    }

    @Transactional
    void addNodesAndEdges(List<Node> nodes, List<Edge> edges) throws SQLException {
        for (Node node : nodes) {
            nodeService.createNode(node);
        }
        for (Edge edge : edges) {
            edgeService.createEdge(edge);
        }
    }
}
