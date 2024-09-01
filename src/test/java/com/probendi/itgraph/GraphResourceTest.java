package com.probendi.itgraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
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
                    new Node("bravo", 10, 10, NodeType.LEXEME),
                    new Node("charlie", 20, 20, NodeType.LEXEME),
                    new Node("delta", 30, 30, NodeType.DIVISION),
                    new Node("echo", 40, 40, NodeType.LEXEME),
                    new Node("fox trot", 50, 50, NodeType.LEXEME),
                    new Node("golf", 60, 60, NodeType.DIVISION),
                    new Node("hotel", 70, 70, NodeType.LEXEME),
                    new Node("india", 80, 80, NodeType.LEXEME)))
            .setEdges(Set.of(new Edge("ens", "bravo"),
                    new Edge("ens", "charlie"),
                    new Edge("ens", "delta"),
                    new Edge("ens", "echo"),
                    new Edge("delta", "fox trot"),
                    new Edge("delta", "golf"),
                    new Edge("golf", "hotel"),
                    new Edge("golf", "india"))
            );

    @PersistenceContext
    private EntityManager em;

    @Inject
    GraphService graphService;

    @BeforeEach
    @Transactional
    public void setup(TestInfo testInfo) throws IOException {
        em.createNativeQuery("delete from edges").executeUpdate();
        em.createNativeQuery("delete from nodes").executeUpdate();

        if (testInfo.getDisplayName().contains("stringifyGraph_ActusPotentia")) {
            try (var is = getClass().getClassLoader().getResourceAsStream("actus-potentia.json")) {
                var graph = new ObjectMapper().readValue(is, Graph.class);
                graphService.uploadGraph(graph);
            }
            return;
        }

        graph.getNodes().forEach(n -> {
            var query = String.format(INSERT_NODE, n.getId(), n.getX(), n.getY(), n.getType());
            em.createNativeQuery(query).executeUpdate();
        });
        graph.getEdges().forEach(e ->
                em.createNativeQuery(String.format(INSERT_EDGE, e.source(), e.target())).executeUpdate());

        if (testInfo.getDisplayName().startsWith("stringifyGraph")) {
            em.createNativeQuery(String.format(INSERT_EDGE, "charlie", "echo")).executeUpdate();
            em.createNativeQuery(String.format(INSERT_EDGE, "charlie", "hotel")).executeUpdate();
            if (testInfo.getDisplayName().contains("Hotel")) {
                em.createNativeQuery("delete from edges where target='fox trot'").executeUpdate();
                em.createNativeQuery("delete from nodes where id='fox trot'").executeUpdate();
                var query = String.format(INSERT_NODE, "fox", 50, 50, NodeType.LEXEME);
                em.createNativeQuery(query).executeUpdate();
                em.createNativeQuery(String.format(INSERT_EDGE, "delta", "fox")).executeUpdate();
            }
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
    public void stringifyGraph_FoxTrot() {
        var expected = """
                1 ens
                1.1 bravo
                1.2 charlie ...... echo
                            ...... hotel
                1.3.0 *
                1.3.1 fox trot
                1.3.2.0 *
                1.3.2.1 hotel .... charlie
                1.3.2.2 india
                1.4 echo ......... charlie
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
    public void stringifyGraph_Hotel() {
        var expected = """
                1 ens
                1.1 bravo
                1.2 charlie ..... echo
                            ..... hotel
                1.3.0 *
                1.3.1 fox
                1.3.2.0 *
                1.3.2.1 hotel ... charlie
                1.3.2.2 india
                1.4 echo ........ charlie
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
    public void stringifyGraph_ActusPotentia() {
        var expected = """
                1 ens
                1.1.0 *
                1.1.1 ens actu ............................ ens actu hoc
                1.1.1.1.0 *
                1.1.1.1.1 ens actu in alio
                1.1.1.1.2 ens actu in se
                1.1.1.2.0 *
                1.1.1.2.1 ens actu secundum quid
                1.1.1.2.2 ens actu simpliciter
                1.1.2 ens in potentia ..................... ens diminutum
                1.1.2.1.0 *
                1.1.2.1.1 ens in potentia secundum quid
                1.1.2.1.2 ens in potentia simpliciter
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
                1.1 bravo
                1.2 charlie
                1.3.0 *
                1.3.1 fox trot
                1.3.2.0 *
                1.3.2.1 hotel
                1.3.2.2 india
                1.4 echo
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
