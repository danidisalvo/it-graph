package com.probendi.itgraph;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * An undirected graph.
 *
 * @author Daniele Di Salvo
 * @since 1.0.0
 */
@ApplicationScoped
public class Graph {

    private Set<Node> nodes = new TreeSet<>();
    private Set<Edge> edges = new TreeSet<>();

    public Set<Node> getNodes() {
        return nodes;
    }

    public Graph setNodes(@NotNull Set<Node> nodes) {
        this.nodes = new TreeSet<>();
        this.nodes.addAll(nodes);
        return this;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public Graph setEdges(@NotNull Set<Edge> edges) {
        this.edges = new TreeSet<>();
        this.edges.addAll(edges);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Graph graph)) return false;
        return nodes.equals(graph.nodes) && edges.equals(graph.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, edges);
    }

    @Override
    public String toString() {
        return "Graph{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                '}';
    }

    /**
     * Adds the given node to the nodes.
     *
     * @param node the node to be added
     */
    public void addNode(@NotNull Node node) {
        nodes.add(node);
    }

    /**
     * Adds the given edge to the edge.
     *
     * @param edge the edge to be added
     */
    public void addEdge(@NotNull Edge edge) {
        edges.add(edge);
    }
}
