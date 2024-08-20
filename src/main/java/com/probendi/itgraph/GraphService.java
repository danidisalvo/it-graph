package com.probendi.itgraph;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

/**
 * Handles the business logic of a {@link Graph}.
 *
 * @author Daniele Di Salvo
 * @since 1.0.0
 */
@ApplicationScoped
public class GraphService {

    @Inject
    NodeService nodeService;

    /**
     * Clears the graph.
     *
     * @return the graph
     */
    @Transactional
    public Graph clearGraph() {
        nodeService.deleteAllNodes();
        return new Graph();
    }

    /**
     * Returns the graph.
     *
     * @return the graph
     */
    @Transactional
    public Graph getGraph() {
        return nodeService.fetchGraph();
    }

    /**
     * Uploads a graph.
     *
     * @param graph the graph to be uploaded
     * @return the graph
     */
    @Transactional
    public Graph uploadGraph(@NotNull Graph graph) {
        clearGraph();
        graph.getNodes().forEach(node -> nodeService.createNode(node));
        graph.getEdges().forEach(edge -> nodeService.findNode(edge.source()).orElseThrow()
                .addEdge(nodeService.findNode(edge.target()).orElseThrow()));
        return getGraph();
    }
}
