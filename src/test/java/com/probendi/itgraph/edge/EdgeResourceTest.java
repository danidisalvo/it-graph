package com.probendi.itgraph.edge;

import com.probendi.itgraph.*;
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
class EdgeResourceTest {

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
        var edge = new Edge("b", "c");

        var graph = given()
                .contentType(ContentType.JSON)
                .body(edge)
                .when()
                .post("/edges/b/c")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .extract()
                .as(Graph.class);

        assertTrue(graph.getEdges().contains(edge));
    }

    @Test
    public void create_BAD_REQUEST_SameSourceAndTarget() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/edges/source/source")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .body(is("{\"message\":\"BAD_REQUEST\",\"details\":\"Source and target must be different\"}"));
    }

    @Test
    public void create_BAD_REQUEST_SourceNotFound() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/edges/not-found/a")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .body(is("{\"message\":\"BAD_REQUEST\",\"details\":\"Source not found\"}"));
    }

    @Test
    public void create_BAD_REQUEST_TargetNotFound() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/edges/a/not-found")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .body(is("{\"message\":\"BAD_REQUEST\",\"details\":\"Target not found\"}"));
    }

    @Test
    public void create_BAD_REQUEST_Duplicated() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/edges/a/b")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .body(is("{\"message\":\"BAD_REQUEST\",\"details\":\"Duplicated edge\"}"));
    }

    @Test
    public void delete() {
        var graph = given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/edges/a/b")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .extract()
                .as(Graph.class);

        assertFalse(graph.getEdges().contains(edges.getFirst()));
    }

    @Test
    public void delete_NOT_FOUND() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/edges/b/c")
                .then()
                .log()
                .body()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }
}
