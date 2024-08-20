package com.probendi.itgraph;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class NodeResourceTest {

    @PersistenceContext
    private EntityManager em;

    private final Edge ab = new Edge("a", "b");
    private final Edge ac = new Edge("a", "c");

    private final Node b = new Node("b", 30, 40, NodeType.LEXEME);
    private final Node c = new Node("c", 50, 60, NodeType.LEXEME);
    private final Node a = new Node("a", 10, 20, NodeType.OPPOSITION).addEdge(b).addEdge(c);

    @BeforeEach
    @Transactional
    public void setup() {
        em.createQuery("delete from Node").executeUpdate();
        em.persist(a);
        em.persist(b);
        em.persist(c);
    }

    @Test
    public void create() {
        var a = new Node("new-node", 0, 0, NodeType.LEXEME);

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
        var a = new Node("a", 0, 0, NodeType.LEXEME);

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

        assertFalse(graph.getNodes().contains(a));
        assertTrue(graph.getNodes().contains(b));
        assertTrue(graph.getNodes().contains(c));
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

        assertTrue(graph.getNodes().contains(a));
        assertFalse(graph.getNodes().contains(b));
        assertTrue(graph.getNodes().contains(c));
        assertFalse(graph.getEdges().contains(ab));
        assertTrue(graph.getEdges().contains(ac));
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
        var updatedNode = new Node("a", 1, 1, NodeType.DIVISION);

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
        assertTrue(graph.getNodes().contains(b));
        assertTrue(graph.getNodes().contains(c));
        assertTrue(graph.getEdges().contains(ab));
        assertTrue(graph.getEdges().contains(ac));
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
