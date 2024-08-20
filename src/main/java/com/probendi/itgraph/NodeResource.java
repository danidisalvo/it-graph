package com.probendi.itgraph;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Exposes the RESTful endpoints to add, delete, and update nodes.
 *
 * @author Daniele Di Salvo
 * @since 1.0.0
 */
@Path("/nodes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NodeResource {

    @Inject
    GraphService graphService;

    @Inject
    NodeService nodeService;

    @POST
    public Graph createNode(Node node) {
        nodeService.createNode(node);
        return graphService.getGraph();
    }

    @Path("/{id}")
    @DELETE
    public Graph deleteNode(@PathParam("id") String id) {
        if (nodeService.deleteNode(id) == 0) {
            throw new NotFoundException();
        }
        return graphService.getGraph();
    }

    @Path("/{id}")
    @PUT
    public Graph updateNode(@PathParam("id") String id, Node node) {
        if (!id.equals(node.getId())) {
            throw new IllegalArgumentException("id does not match node's id");
        }
        if (nodeService.updateNode(node) == 0) {
            throw new NotFoundException();
        }
        return graphService.getGraph();
    }
}
