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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class GraphResourceTest {

    @PersistenceContext
    private EntityManager em;

    private final List<NodeDTO> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();

    @BeforeEach
    @Transactional
    public void setup() {
        nodes.clear();
        edges.clear();

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

        assertTrue(graph.isEmpty());
    }

    @Test
    public void getGraph() {
        var graph = new Graph().setNodes(nodes).setEdges(edges);

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
        var a = new NodeDTO("a", 100, 200, NodeType.OPPOSITION);
        var b = new NodeDTO("b", 300, 400, NodeType.LEXEME);
        var c = new NodeDTO("c", 500, 600, NodeType.LEXEME);
        var ab = new Edge("a", "b");
        var ac = new Edge("a", "c");

        var graph = new Graph();
        graph.setNodes(List.of(a, b, c));
        graph.setEdges(List.of(ab, ac));

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
