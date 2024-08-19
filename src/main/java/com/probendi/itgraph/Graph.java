package com.probendi.itgraph;

import com.probendi.itgraph.edge.Edge;
import com.probendi.itgraph.edge.EdgeDTO;
import com.probendi.itgraph.edge.EdgeService;
import com.probendi.itgraph.node.Node;
import com.probendi.itgraph.node.NodeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A graph contains nodes and edges.
 */
@ApplicationScoped
public class Graph {

    @Inject
    EdgeService edgeService;
    @Inject
    NodeService nodeService;

    private List<Node> nodes = new LinkedList<>();
    private List<EdgeDTO> edges = new LinkedList<>();

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        if (nodes == null) {
            nodes = new LinkedList<>();
        }
        this.nodes = nodes;
    }

    public List<EdgeDTO> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeDTO> edges) {
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

    public Graph generateGraph() {
        Graph graph = new Graph();
        graph.setNodes(nodeService.findAllNodes());
        graph.setEdges(edgeService.findAllEdges());
        return graph;
    }
}
