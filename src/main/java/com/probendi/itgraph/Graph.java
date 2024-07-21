package com.probendi.itgraph;

import com.probendi.itgraph.edge.Edge;
import com.probendi.itgraph.node.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A graph contains nodes and edges.
 */
public class Graph {

    private List<Node> nodes = new LinkedList<>();
    private List<Edge> edges = new LinkedList<>();

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        if (nodes == null) {
            nodes = new LinkedList<>();
        }
        this.nodes = nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        if (edges == null) {
            edges = new LinkedList<>();
        }
        this.edges = edges;
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
}
