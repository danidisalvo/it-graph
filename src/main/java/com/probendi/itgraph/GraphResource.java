package com.probendi.itgraph;

import com.probendi.itgraph.edge.Edge;
import com.probendi.itgraph.edge.EdgeService;
import com.probendi.itgraph.node.Node;
import com.probendi.itgraph.node.NodeService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;

/**
 * Exposes the RESTful endpoints to upload and download a graph.
 */
@Path("/graph")
public class GraphResource {

    public static final String STATUS_UP = "{\"status\": \"up\"}";

    @Inject
    EdgeService edgeService;

    @Inject
    NodeService nodeService;

    @DELETE
    @Transactional
    public Response clear() throws SQLException {
        nodeService.deleteAll();
        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Path("/")
    public Response get() throws SQLException {
        Graph graph = new Graph();
        graph.setNodes(nodeService.findAll());
        graph.setEdges(edgeService.findAll());
        return Response.ok(graph).build();
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response health() {
        return Response.ok(STATUS_UP).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Path("/")
    public Response put(Graph graph) throws SQLException {
        for (Node node : graph.getNodes()) {
            nodeService.createNode(node);
        }
        for (Edge edge : graph.getEdges()) {
            edgeService.createEdge(edge);
        }
        return Response.ok(graph).build();
    }
}
