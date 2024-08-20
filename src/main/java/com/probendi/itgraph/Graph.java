package com.probendi.itgraph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A graph contains nodes and edges.
 */
@ApplicationScoped
@JsonIgnoreProperties(value = { "empty" })
public class Graph {

    private List<NodeDTO> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    public List<NodeDTO> getNodes() {
        return nodes;
    }

    public Graph setNodes(@NotNull List<NodeDTO> nodes) {
        this.nodes = new ArrayList<>();
        this.nodes.addAll(nodes);
        return this;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Graph setEdges(@NotNull List<Edge> edges) {
        this.edges = new ArrayList<>();
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
     * Returns {@code true} if this graph is empty.
     *
     * @return {@code true} if this graph is empty
     */
    public boolean isEmpty() {
        return nodes.isEmpty() && edges.isEmpty();
    }
}
