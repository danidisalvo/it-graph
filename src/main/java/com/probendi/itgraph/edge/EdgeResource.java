package com.probendi.itgraph.edge;

import com.probendi.itgraph.Graph;
import com.probendi.itgraph.node.NodeService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Exposes the RESTful endpoints to add, delete, and update edges.
 */
@Path("/edge")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EdgeResource {

    @Inject
    EdgeService edgeService;
    @Inject
    NodeService nodeService;

    @POST
    @Transactional
    public Response create(Edge edge) throws SQLException {
        if (Objects.equals(edge.getSource(), edge.getTarget())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("source and target must be different").build();
        }
        if (nodeService.findNode(edge.getSource()) == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Source node not found").build();
        }
        if (nodeService.findNode(edge.getTarget()) == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Target node not found").build();
        }
        if (edgeService.findEdge(edge.getSource(), edge.getTarget()) != null ||
                edgeService.findEdge(edge.getTarget(), edge.getSource()) != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Duplicated edge").build();
        }
        edgeService.createEdge(edge);
        return Response.ok(generateGraph()).build();
    }

    @Path("/{source}/{target}")
    @DELETE
    @Transactional
    public Response delete(@PathParam("source") String source, @PathParam("target") String target) throws SQLException {
        if (edgeService.findEdge(source, target) != null) {
            edgeService.deleteEdge(source, target);
        } else if (edgeService.findEdge(target, source) != null) {
            edgeService.deleteEdge(target, source);
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(generateGraph()).build();
    }

    private Graph generateGraph() throws SQLException {
        Graph graph = new Graph();
        graph.setNodes(nodeService.findAll());
        graph.setEdges(edgeService.findAll());
        return graph;
    }
}
