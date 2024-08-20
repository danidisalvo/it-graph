package com.probendi.itgraph;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Exposes the RESTful endpoints to add, delete, and update edges.
 */
@Path("/edges/{source}/{target}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EdgeResource {

    @Inject
    GraphService graphService;

    @Inject
    NodeService nodeService;

    /**
     * Creates a new edge from source to target.
     *
     * @param source the source
     * @param target the target
     */
    @POST
    public Graph createEdge(@PathParam("source") String source, @PathParam("target") String target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("Source and target must be different");
        }
        nodeService.createEdge(source, target);
        return graphService.getGraph();
    }

    /**
     * Deletes the edge from source to target.
     *
     * @param source the source
     * @param target the target
     * @return the number of deleted edges
     */
    @DELETE
    public Graph deleteEdge(@PathParam("source") String source, @PathParam("target") String target) {
        if (nodeService.deleteEdge(source, target) == 0) {
            throw new NotFoundException();
        }
        return graphService.getGraph();
    }
}
