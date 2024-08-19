package com.probendi.itgraph.edge;

import com.probendi.itgraph.Graph;
import com.probendi.itgraph.node.NodeService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
    Graph graph;
    @Inject
    NodeService nodeService;

    @POST
    @Transactional
    public Graph create(EdgeDTO edge) {
        if (Objects.equals(edge.source(), edge.target())) {
            throw new BadRequestException("source and target must be different");
        }
        if (nodeService.findNode(edge.source()).isEmpty()) {
            throw new BadRequestException("Source node not found");
        }
        if (nodeService.findNode(edge.target()).isEmpty()) {
            throw new BadRequestException("Target node not found");
        }
        if (edgeService.findEdge(edge.source(), edge.target()).isPresent() ||
                edgeService.findEdge(edge.target(), edge.source()).isPresent()) {
            throw new BadRequestException("Duplicated edge");
        }
        edgeService.createEdge(edge);
        return graph.generateGraph();
    }

    @Path("/{source}/{target}")
    @DELETE
    @Transactional
    public Graph delete(@PathParam("source") String source, @PathParam("target") String target) {
        if (edgeService.findEdge(source, target).isPresent()) {
            edgeService.deleteEdge(source, target);
        } else if (edgeService.findEdge(target, source).isPresent()) {
            edgeService.deleteEdge(target, source);
        } else {
            throw new NotFoundException();
        }
        return graph.generateGraph();
    }
}
