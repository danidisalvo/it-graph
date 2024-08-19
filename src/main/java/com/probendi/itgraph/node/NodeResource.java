package com.probendi.itgraph.node;

import com.probendi.itgraph.Graph;
import com.probendi.itgraph.edge.EdgeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
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
 */
@Path("/node")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NodeResource {

    @Inject
    Graph graph;
    @Inject
    NodeService nodeService;

    @POST
    public Graph create(Node node) {
        if (nodeService.findNode(node.getId()).isPresent()) {
            throw new BadRequestException("Duplicated node");
        }
        nodeService.createNode(node);
        return graph.generateGraph();
    }

    @Path("/{id}")
    @DELETE
    public Graph delete(@PathParam("id") String id) {
        if (nodeService.deleteNode(id) == 0) {
            throw new NotFoundException();
        }
        return graph.generateGraph();
    }

    @Path("/{id}")
    @PUT
    public Graph update(@PathParam("id") String id, Node node) {
        if (!id.equals(node.getId())) {
            throw new BadRequestException();
        }
        if (nodeService.updateNode(node) == 0) {
            throw new NotFoundException();
        }
        return graph.generateGraph();
    }
}
