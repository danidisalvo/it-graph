package com.probendi.itgraph;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Exposes the RESTful endpoints to upload and download a graph.
 *
 * @author Daniele Di Salvo
 * @since 1.0.0
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/graph")
public class GraphResource {

    @Inject
    GraphService service;

    /**
     * Explicit empty constructor.
     */
    public GraphResource() {
    }

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

    /**
     * Returns a simplified string representation of this graph starting from the given root node.
     *
     * @param root the root node
     * @return a simplified string representation of this graph starting from the given root node
     */
    @GET
    @Path("/printout/{root}")
    @Produces(MediaType.TEXT_PLAIN)
    public String stringifyGraph(@PathParam("root") String root) {
        return service.stringifyGraph(root).orElseThrow(NotFoundException::new);
    }
}
