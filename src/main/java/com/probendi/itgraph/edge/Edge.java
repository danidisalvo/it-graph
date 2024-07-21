package com.probendi.itgraph.edge;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * A graph's edge.
 */
@JsonPropertyOrder({"source", "target"})
public class Edge {

    private String source;
    private String target;

    public Edge() {}

    public Edge(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public Edge setSource(String source) {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("source cannot be null or blank");
        }
        this.source = source;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public Edge setTarget(String target) {
        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException("target cannot be null or blank");
        }
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
