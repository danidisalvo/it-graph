package com.probendi.itgraph;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class GraphResourceTest {

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
    public void clearGraph() {
        var graph = given()
                .when()
                .delete("/graph")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .extract().as(Graph.class);

        assertTrue(graph.getNodes().isEmpty() && graph.getEdges().isEmpty());
    }

    @Test
    public void getGraph() {
        var graph = new Graph().setNodes(Set.of(a, b, c)).setEdges(Set.of(ab, ac));

        var actualGraph = given()
                .when()
                .get("/graph")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .extract().as(Graph.class);

        assertEquals(graph, actualGraph);
    }

    @Test
    public void uploadGraph() {
        var a = new Node("a", 100, 200, NodeType.OPPOSITION);
        var b = new Node("b", 300, 400, NodeType.LEXEME);
        var c = new Node("c", 500, 600, NodeType.LEXEME);
        var ab = new Edge("a", "b");
        var ac = new Edge("a", "c");

        var graph = new Graph();
        graph.setNodes(Set.of(a, b, c));
        graph.getEdges().add(ab);
        graph.getEdges().add(ac);

        // we need to ensure that Edge#compareTo works as expected
        assertEquals(2, graph.getEdges().size());


        var actualGraph = given()
                .contentType(ContentType.JSON)
                .body(graph)
                .when()
                .put("/graph")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .extract()
                .as(Graph.class);

        assertEquals(graph, actualGraph);
    }
}
