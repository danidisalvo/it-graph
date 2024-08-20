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
class EdgeResourceTest {

    @PersistenceContext
    private EntityManager em;

    private final Edge ab = new Edge("a", "b");

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
        var graph = given()
                .contentType(ContentType.JSON)
                .when()
                .post("/edges/b/c")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .log()
                .body()
                .extract()
                .as(Graph.class);

        assertTrue(graph.getEdges().contains(new Edge("b", "c")));
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
    public void create_SourceNotFound() {
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
                .body(is("{\"message\":\"BAD_REQUEST\",\"details\":\"Edge already exists\"}"));
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

        assertFalse(graph.getEdges().contains(ab));
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
