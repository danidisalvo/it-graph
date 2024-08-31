package com.probendi.itgraph;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        nodeService.deleteAllNodes();
        graph.getNodes().forEach(node -> nodeService.createNode(node));
        graph.getEdges().forEach(edge -> nodeService.findNode(edge.source()).orElseThrow()
                .addEdge(nodeService.findNode(edge.target()).orElseThrow()));
        return getGraph();
    }

    /**
     * Returns a simplified string representation of this graph starting from the given root node.
     *
     * @param root the root node
     * @return a simplified string representation of this graph starting from the given root node
     */
    @Transactional
    public String stringifyGraph(String root) {
        var node = nodeService.findNode(root).orElseThrow(IllegalArgumentException::new);

        StringBuilder sb = new StringBuilder();
        List<Integer> counters = new ArrayList<>();
        counters.add(1);
        stringify(node, new HashSet<>(), sb, counters);
        return sb.toString();
    }

    /**
     * Builds a simplified string representation of this node using the Depth-First Search algorithm.
     *
     * @param node the node
     * @param visitedNodes the nodes that have already been visited
     * @param sb the string builder used to build the simplified string representation of this graph
     * @param counters a list of integers that tracks the depth level, e.g. 1,3, and 2
     */
    // stringify recursively stringifies the graph using the Depth-First Search algorithm
    private void stringify(Node node, Set<String> visitedNodes, StringBuilder sb, List<Integer> counters) {
        if (visitedNodes.contains(node.getId())) {
            return;
        }
        visitedNodes.add(node.getId());
        if (node.getType() == NodeType.LEXEME) {
            sb.append(String.format("%s %s\n", formatCounters(counters), node.getId()));
        }

        // a node has always at least one edge
        for (var edge : node.getEdges()) {
            if (!visitedNodes.contains(edge.getId())) {
                counters.add(0);
                break;
            }
        }
        for (var edge : node.getEdges()) {
            if (!visitedNodes.contains(edge.getId())) {
                var n = counters.size() - 1;
                counters.set(n, counters.get(n) + 1);
                List<Integer> clonedList = new ArrayList<>(counters);
                stringify(edge, visitedNodes, sb, counters);
                counters = new ArrayList<>(clonedList);
            }
        }
    }

    /**
     * Formats the given counters. For example, if counters are 1, 3, and, this method returns {@code 1.3.2}
     *
     * @param counters the counters
     * @return the formatted counters
     */
    private String formatCounters(List<Integer> counters) {
        StringBuilder sb = new StringBuilder();
        counters.forEach(i -> sb.append('.').append(i));
        return sb.substring(1);
    }
}
