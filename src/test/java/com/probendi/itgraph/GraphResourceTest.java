package com.probendi.itgraph;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class GraphResourceTest {

    private static final String INSERT_EDGE = "insert into edges(source,target) values('%s','%s')";
    private static final String INSERT_NODE = "insert into nodes(id,x,y,type) values('%s',%d,%d,'%s')";

    private static final Graph graph = new Graph()
            .setNodes(Set.of(new Node("ens", 0, 0, NodeType.LEXEME),
                    new Node("B", 10, 10, NodeType.LEXEME),
                    new Node("C", 20, 20, NodeType.LEXEME),
                    new Node("D", 30, 30, NodeType.DIVISION),
                    new Node("E", 40, 40, NodeType.LEXEME),
                    new Node("F", 50, 50, NodeType.LEXEME),
                    new Node("G", 60, 60, NodeType.DIVISION),
                    new Node("H", 70, 70, NodeType.LEXEME),
                    new Node("I", 80, 80, NodeType.LEXEME)))
            .setEdges(Set.of(new Edge("ens", "B"),
                    new Edge("ens", "C"),
                    new Edge("ens", "D"),
                    new Edge("ens", "E"),
                    new Edge("D", "F"),
                    new Edge("D", "G"),
                    new Edge("G", "H"),
                    new Edge("G", "I"))
            );

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    @Transactional
    public void setup(TestInfo testInfo) {
        em.createNativeQuery("delete from edges").executeUpdate();
        em.createNativeQuery("delete from nodes").executeUpdate();
        graph.getNodes().forEach(n -> {
            var query = String.format(INSERT_NODE, n.getId(), n.getX(), n.getY(), n.getType());
            em.createNativeQuery(query).executeUpdate();
        });
        graph.getEdges().forEach(e ->
                em.createNativeQuery(String.format(INSERT_EDGE, e.source(), e.target())).executeUpdate());

        if (testInfo.getTags().contains("stringify-graph")) {
            em.createNativeQuery(String.format(INSERT_EDGE, "C", "H")).executeUpdate();
        }
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

    @Test
    @Tag("stringify-graph")
    public void stringifyGraph() {
        var expected = """
                1 ens
                1.1 B
                1.2 C
                1.3.1 F
                1.3.2.1 H
                1.3.2.2 I
                1.4 E
                """;

        var actual = given()
                .when()
                .get("/graph/printout/ens")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.TEXT)
                .log()
                .body()
                .extract()
                .asString();
        assertEquals(expected, actual);
    }

    @Test
    public void stringifyTree() {
        var expected = """
                1 ens
                1.1 B
                1.2 C
                1.3.1 F
                1.3.2.1 H
                1.3.2.2 I
                1.4 E
                """;

        var actual = given()
                .when()
                .get("/graph/printout/ens")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.TEXT)
                .log()
                .body()
                .extract()
                .asString();
        assertEquals(expected, actual);
    }

    @Test
    public void generatePrintoutNotFound() {
        given()
                .when()
                .get("/graph/printout/not-found")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }
}
