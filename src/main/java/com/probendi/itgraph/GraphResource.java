package com.probendi.itgraph;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Exposes the RESTful endpoints to upload and download a graph.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/graph")
public class GraphResource {

    @Inject
    GraphService service;

    /**
     * Clears the graph.
     *
     * @return the graph
     */
    @DELETE
    public Graph clearGraph() {
        return service.clearGraph();
    }

    /**
     * Returns the graph.
     *
     * @return the graph
     */
    @GET
    public Graph getGraph() {
        return service.getGraph();
    }

    /**
     * Uploads a graph.
     *
     * @param graph the graph to be uploaded
     * @return the graph
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Graph uploadGraph(Graph graph) {
        return service.uploadGraph(graph);
    }
}
