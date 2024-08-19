package com.probendi.itgraph.edge;

import com.probendi.itgraph.Graph;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class EdgeResourceTest {

    @Inject
    EdgeService edgeService;
    @Inject
    NodeService nodeService;

    @BeforeEach
    @Transactional
    public void setup() throws SQLException {
        nodeService.deleteAllNodes();
    }

    @Test
    public void tesCreate_OK() throws SQLException {
        Node source = new Node("source", 10, 10, NodeType.LEXEME);
        Node target = new Node("target", 20, 20, NodeType.LEXEME);
        addNodesAndEdges(List.of(source, target), List.of());

        Edge edge = new Edge("source", "target");

        Graph actualGraph = given()
                .contentType(ContentType.JSON)
                .body(edge)
                .when().post("/edge")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(Graph.class);

        assertTrue(actualGraph.getEdges().contains(edge));
    }

    @Test
    public void tesCreate_BAD_REQUEST_SameSourceAndTarget() {
        Edge edge = new Edge("source", "source");

        given()
                .contentType(ContentType.JSON)
                .body(edge)
                .when().post("/edge")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .body(is("source and target must be different"));
    }

    @Test
    public void tesCreate_BAD_REQUEST_SourceNotFound() {
        Edge edge = new Edge("source", "target");

        given()
                .contentType(ContentType.JSON)
                .body(edge)
                .when().post("/edge")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .body(is("Source node not found"));
    }

    @Test
    public void tesCreate_BAD_REQUEST_TargetNotFound() throws SQLException {
        Node source = new Node("source", 10, 10, NodeType.LEXEME);
        addNodesAndEdges(List.of(source), List.of());

        Edge edge = new Edge("source", "target");

        given()
                .contentType(ContentType.JSON)
                .body(edge)
                .when().post("/edge")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .body(is("Target node not found"));
    }

    @Test
    public void tesCreate_BAD_REQUEST_Duplicated() throws SQLException {
        Node source = new Node("source", 10, 10, NodeType.LEXEME);
        Node target = new Node("target", 20, 20, NodeType.LEXEME);
        Edge edge = new Edge("source", "target");
        addNodesAndEdges(List.of(source, target), List.of(edge));

        given()
                .contentType(ContentType.JSON)
                .body(edge)
                .when().post("/edge")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .body(is("Duplicated edge"));
    }

    @Test
    public void tesDelete_OK() throws SQLException {
        Node source = new Node("source", 10, 10, NodeType.LEXEME);
        Node target = new Node("target", 20, 20, NodeType.LEXEME);
        Edge edge = new Edge("source", "target");
        addNodesAndEdges(List.of(source, target), List.of(edge));

        Graph actualGraph = given()
                .contentType(ContentType.JSON)
                .when().delete("/edge/source/target")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(Graph.class);

        assertFalse(actualGraph.getEdges().contains(edge));
    }

    @Test
    public void tesDelete_NOT_FOUND() {
        given()
                .contentType(ContentType.JSON)
                .when().delete("/edge/{source}/{target}", 0, 1)
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
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
