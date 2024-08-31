package com.probendi.itgraph;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.probendi.itgraph.NodeType.LEXEME;

/**
 * Handles the business logic of a {@link Graph}.
 *
 * @author Daniele Di Salvo
 * @since 1.0.0
 */
@ApplicationScoped
public class GraphService {

    private static final String PLACEHOLDER = "<REPLACE-ME>";

    @Inject
    NodeService nodeService;

    /**
     * Explicit empty constructor.
     */
    public GraphService() {
    }

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
    public String stringifyGraph(@NotBlank String root) {
        var node = nodeService.findNode(root).orElseThrow(IllegalArgumentException::new);

        var lines = new ArrayList<String>();
        var counters = new ArrayList<Integer>();
        counters.add(1);
        stringify(node, new HashSet<>(), lines, counters, root);

        int maxLength = 0;
        for (var line : lines) {
            var length = line.contains(PLACEHOLDER) ? line.indexOf(PLACEHOLDER) - 1 : line.length();
            if (length > maxLength) {
                maxLength = length;
            }
        }
        maxLength += 3;

        var sb = new StringBuilder();
        var spaces = "";
        for (var line : lines) {
            if (!line.isEmpty() && Character.isDigit(line.charAt(0))) {
                spaces = "";
            }
            if (line.contains(PLACEHOLDER)) {
                if (line.startsWith(PLACEHOLDER)) {
                    line = line.replace(PLACEHOLDER, spaces);
                } else {
                    var c = maxLength - line.indexOf(PLACEHOLDER);
                    var replacement = " " + String.valueOf('.').repeat(c > 0 ? c : 3) + " ";
                    line = line.replace(PLACEHOLDER, replacement);
                    spaces = String.valueOf(' ').repeat(line.indexOf(replacement)) + replacement;
                }
            }
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * Builds a simplified string representation of this node using the Depth-First Search algorithm.
     *
     * @param node         the node
     * @param visitedNodes the nodes that have already been visited
     * @param lines        the list used to build the simplified string representation of this graph
     * @param counters     keeps track of the depth level of this node. For example, if the depth is represented as
     *                     {@code }, the corresponding values in counters would be 1, 3, and 2
     * @param root         the root node
     */
    private void stringify(Node node,
                           Set<String> visitedNodes,
                           List<String> lines,
                           List<Integer> counters,
                           String root) {
        if (visitedNodes.contains(node.getId())) {
            return;
        }
        visitedNodes.add(node.getId());

        // handle lexemes
        if (node.getType() == LEXEME) {
            lines.add(formatCounters(counters) + " " + node.getId());

            // add a dotted line if this lexeme is not the root node and is linked to another lexeme
            if (!root.equals(node.getId())) {
                final var first = new AtomicBoolean(true);
                // handle the outgoing edges
                node.getEdges().stream().sorted().forEach(t -> {
                    if (t.getType() == LEXEME) {
                        if (first.get()) {
                            lines.set(lines.size() - 1, lines.getLast() + PLACEHOLDER + t.getId());
                            first.set(false);
                        } else {
                            lines.add(PLACEHOLDER + t.getId());
                        }
                    }
                });

                // handle the incoming edges
                nodeService.findIncomingLexemes(node.getId(), root).forEach(t -> {
                    if (first.get()) {
                        lines.set(lines.size() - 1, lines.getLast() + PLACEHOLDER + t);
                        first.set(false);
                    } else {
                        lines.add(PLACEHOLDER + t);
                    }
                });
            }
        }

        // a node always has at least one edge
        for (var edge : node.getEdges()) {
            if (!visitedNodes.contains(edge.getId())) {
                counters.add(0);
                break;
            }
        }

        // handle divisions and oppositions
        node.getEdges().stream().sorted().forEach(t -> {
            if ((root.equals(node.getId()) || node.getType() != LEXEME || t.getType() != LEXEME) &&
                    !visitedNodes.contains(t.getId())) {

                var n = counters.size() - 1;
                counters.set(n, counters.get(n) + 1);
                List<Integer> clonedList = new ArrayList<>(counters);
                stringify(t, visitedNodes, lines, counters, root);
                counters.clear();
                counters.addAll(clonedList);

            }
        });
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
