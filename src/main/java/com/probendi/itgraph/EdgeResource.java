package com.probendi.itgraph;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
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
     * Explicit empty constructor.
     */
    public EdgeResource() {
    }

    /**
     * Creates a new edge from source to target.
     *
     * @param source the source
     * @param target the target
     * @return the graph
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
