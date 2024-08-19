package com.probendi.itgraph.edge;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.probendi.itgraph.node.Node;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * A graph's edge.
 */
@Entity
@Table(name = "edges")
@IdClass(Edge.class)
@JsonPropertyOrder({"source", "target"})
public class Edge {

    @Id
    @ManyToOne
    @JoinColumn(name = "source", nullable = false)
    private Node source;

    @Id
    @ManyToOne
    @JoinColumn(name = "target", nullable = false)
    private Node target;

    public Edge() {}

    public Edge(String source, String target) {
        this.source = new Node().setId(source);
        this.target = new Node().setId(target);
    }

    public Node getSource() {
        return source;
    }

    public Edge setSource(Node source) {
        this.source = source;
        return this;
    }

    public Node getTarget() {
        return target;
    }

    public Edge setTarget(Node target) {
        this.target = target;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge edge)) return false;
        return Objects.equals(source, edge.source) && Objects.equals(target, edge.target) ||
                Objects.equals(source, edge.target) && Objects.equals(target, edge.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}
