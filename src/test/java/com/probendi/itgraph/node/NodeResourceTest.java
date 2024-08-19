package com.probendi.itgraph.node;

import com.probendi.itgraph.Graph;
import com.probendi.itgraph.edge.Edge;
import com.probendi.itgraph.edge.EdgeService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
class NodeResourceTest {

    @PersistenceContext
    private EntityManager em;

    @Inject
    EdgeService edgeService;
    @Inject
    NodeService nodeService;

    @BeforeEach
    @Transactional
    public void setup() throws SQLException {
        em.createQuery("delete from Node").executeUpdate();
    }

    @Test
    public void tesCreate_OK() {
        Node a = new Node("a", 0, 0, NodeType.LEXEME);

        Graph graph = given()
                .contentType(ContentType.JSON)
                .body(a)
                .when().post("/node")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(Graph.class);

        assertTrue(graph.getNodes().contains(a));
    }

    @Test
    public void tesCreate_BAD_REQUEST() throws SQLException {
        Node a = new Node("a", 0, 0, NodeType.LEXEME);
        addNodesAndEdges(List.of(a), List.of());

        given()
                .contentType(ContentType.JSON)
                .body(a)
                .when().post("/node")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .body(is("Duplicated node"));
    }

    @Test
    public void tesDelete_a_OK() throws SQLException {
        Node a = new Node("a", 0, 0, NodeType.LEXEME);
        Node b = new Node("b", 0, 0, NodeType.LEXEME);
        Node c = new Node("c", 0, 0, NodeType.LEXEME);
        Edge ab = new Edge("a", "b");
        Edge ac = new Edge("a", "c");
        addNodesAndEdges(List.of(a, b, c), List.of(ab, ac));

        Graph graph = given()
                .contentType(ContentType.JSON)
                .when().delete("/node/a")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(Graph.class);

        assertFalse(graph.getNodes().contains(a));
        assertTrue(graph.getNodes().contains(b));
        assertTrue(graph.getNodes().contains(c));
        assertFalse(graph.getEdges().contains(ab));
        assertFalse(graph.getEdges().contains(ac));
    }

    @Test
    public void tesDelete_b_OK() throws SQLException {
        Node a = new Node("a", 0, 0, NodeType.LEXEME);
        Node b = new Node("b", 0, 0, NodeType.LEXEME);
        Node c = new Node("c", 0, 0, NodeType.LEXEME);
        Edge ab = new Edge("a", "b");
        Edge ac = new Edge("a", "c");
        addNodesAndEdges(List.of(a, b, c), List.of(ab, ac));

        Graph graph = given()
                .contentType(ContentType.JSON)
                .when().delete("/node/b")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .extract().as(Graph.class);

        assertTrue(graph.getNodes().contains(a));
        assertFalse(graph.getNodes().contains(b));
        assertTrue(graph.getNodes().contains(c));
        assertFalse(graph.getEdges().contains(ab));
        assertTrue(graph.getEdges().contains(ac));
    }

    @Test
    public void tesDelete_NOT_FOUND() {
        given()
                .contentType(ContentType.JSON)
                .when().delete("/node/a")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void tesUpdate_OK() throws SQLException {
        Node a = new Node("a", 0, 0, NodeType.LEXEME);
        Node b = new Node("b", 0, 0, NodeType.LEXEME);
        Node c = new Node("c", 0, 0, NodeType.LEXEME);
        Edge ab = new Edge("a", "b");
        Edge ac = new Edge("a", "c");
        addNodesAndEdges(List.of(a, b, c), List.of(ab, ac));

        Node updatedNode = new Node(a.getId(), 1, 1, NodeType.DIVISION);

        Graph graph = given()
                .contentType(ContentType.JSON)
                .body(b)
                .when().put("/node/b")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().as(Graph.class);

        assertTrue(graph.getNodes().contains(updatedNode));
        assertTrue(graph.getNodes().contains(b));
        assertTrue(graph.getNodes().contains(c));
        assertTrue(graph.getEdges().contains(ab));
        assertTrue(graph.getEdges().contains(ac));
    }

    @Test
    public void tesUpdate_BAD_REQUEST() {
        Node a = new Node("a", 0, 0, NodeType.LEXEME);

        given()
                .contentType(ContentType.JSON)
                .body(a)
                .when().put("/node/b")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void tesUpdate_NOT_FOUND() {
        Node a = new Node("a", 0, 0, NodeType.LEXEME);

        given()
                .contentType(ContentType.JSON)
                .body(a)
                .when().put("/node/a")
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
