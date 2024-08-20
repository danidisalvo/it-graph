package com.probendi.itgraph;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class NodeResourceTest {

    @PersistenceContext
    private EntityManager em;

    private final List<NodeDTO> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();

    @BeforeEach
    @Transactional
    public void setup() {
        nodes.clear();
        edges.clear();

        em.createQuery("delete from Edge").executeUpdate();
        em.createQuery("delete from Node").executeUpdate();

        nodes.add(new NodeDTO("a", 10, 20, NodeType.OPPOSITION));
        nodes.add(new NodeDTO("b", 30, 40, NodeType.LEXEME));
        nodes.add(new NodeDTO("c", 50, 60, NodeType.LEXEME));
        edges.add(new Edge("a", "b"));
        edges.add(new Edge("a", "c"));

        Node a = new Node(nodes.get(0));
        Node b = new Node(nodes.get(1));
        Node c = new Node(nodes.get(2));

        a.addEdge(b).addEdge(c);

        em.persist(a);
        em.persist(b);
        em.persist(c);
    }

    @Test
    public void create() {
        var a = new NodeDTO("new-node", 0, 0, NodeType.LEXEME);

        var graph = given()
                .contentType(ContentType.JSON)
                .body(a)
                .when()
                .post("/nodes")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .extract()
                .as(Graph.class);

        assertTrue(graph.getNodes().contains(a));
    }

    @Test
    public void create_BAD_REQUEST() {
        var a = new NodeDTO("a", 0, 0, NodeType.LEXEME);

        given()
                .contentType(ContentType.JSON)
                .body(a)
                .when()
                .post("/nodes")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .body(is("{\"message\":\"BAD_REQUEST\",\"details\":\"Duplicated node\"}"));
    }

    @Test
    public void delete_a() {
        var graph = given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/nodes/a")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .extract()
                .as(Graph.class);

        assertFalse(graph.getNodes().contains(nodes.getFirst()));
        assertTrue(graph.getNodes().contains(nodes.get(1)));
        assertTrue(graph.getNodes().contains(nodes.get(2)));
        assertTrue(graph.getEdges().isEmpty());
    }

    @Test
    public void delete_b() {
        var graph = given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/nodes/b")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .extract()
                .as(Graph.class);

        assertTrue(graph.getNodes().contains(nodes.get(0)));
        assertFalse(graph.getNodes().contains(nodes.get(1)));
        assertTrue(graph.getNodes().contains(nodes.get(2)));
        assertFalse(graph.getEdges().contains(edges.getFirst()));
        assertTrue(graph.getEdges().contains(edges.get(1)));
    }

    @Test
    public void delete_NOT_FOUND() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/nodes/z")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .log()
                .body();
    }

    @Test
    public void update() {
        var updatedNode = new NodeDTO("a", 1, 1, NodeType.DIVISION);

        var graph = given()
                .contentType(ContentType.JSON)
                .body(updatedNode)
                .when()
                .put("/nodes/a")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .log()
                .body()
                .extract()
                .as(Graph.class);

        assertTrue(graph.getNodes().contains(updatedNode));
        assertTrue(graph.getNodes().contains(nodes.get(1)));
        assertTrue(graph.getNodes().contains(nodes.get(2)));
        assertTrue(graph.getEdges().contains(edges.get(0)));
        assertTrue(graph.getEdges().contains(edges.get(1)));
    }

    @Test
    public void update_BAD_REQUEST() {
        var a = new Node("a", 0, 0, NodeType.LEXEME);

        given()
                .contentType(ContentType.JSON)
                .body(a)
                .when()
                .put("/nodes/b")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .log()
                .body();
    }

    @Test
    public void update_NOT_FOUND() {
        var z = new Node("z", 0, 0, NodeType.LEXEME);

        given()
                .contentType(ContentType.JSON)
                .body(z)
                .when()
                .put("/nodes/z")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .log()
                .body();
    }
}
