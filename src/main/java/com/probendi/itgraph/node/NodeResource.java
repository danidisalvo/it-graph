package com.probendi.itgraph.node;

import com.probendi.itgraph.Graph;
import com.probendi.itgraph.edge.EdgeService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;

/**
 * Exposes the RESTful endpoints to add, delete, and update nodes.
 */
@Path("/node")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NodeResource {

    @Inject
    EdgeService edgeService;
    @Inject
    NodeService nodeService;

    @POST
    @Transactional
    public Response create(Node node) throws SQLException {
        if (nodeService.findNode(node.getId()) != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Duplicated node").build();
        }
        nodeService.createNode(node);
        return Response.ok(generateGraph()).build();
    }

    @Path("/{id}")
    @DELETE
    @Transactional
    public Response delete(@PathParam("id") String id) throws SQLException {
        if (nodeService.findNode(id) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        nodeService.deleteNode(id);
        return Response.ok(generateGraph()).build();
    }

    @Path("/{id}")
    @PUT
    @Transactional
    public Response update(@PathParam("id") String id, Node node) throws SQLException {
        if (!id.equals(node.getId())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (nodeService.findNode(id) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        nodeService.updateNode(node);
        return Response.ok(generateGraph()).build();
    }

    private Graph generateGraph() throws SQLException {
        Graph graph = new Graph();
        graph.setNodes(nodeService.findAll());
        graph.setEdges(edgeService.findAll());
        return graph;
    }
}
