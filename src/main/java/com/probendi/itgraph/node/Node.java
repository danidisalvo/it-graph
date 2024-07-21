package com.probendi.itgraph.node;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/**
 * A graph's node.
 */
@JsonPropertyOrder({"id", "x", "y", "type"})
public class Node {

    private String id;
    private int x;
    private int y;
    private NodeType type;

    public Node() {
    }

    public Node(String id, int x, int y, NodeType type) {
        setId(id);
        setX(x);
        setY(y);
        setType(type);
    }

    public String getId() {
        return id;
    }

    public Node setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id cannot be null or blank");
        }
        this.id = id;
        return this;
    }

    public int getX() {
        return x;
    }

    public Node setX(int x) {
        if (x < 0) {
            throw new IllegalArgumentException("x cannot be negative");
        }
        this.x = x;
        return this;
    }

    public int getY() {
        return y;
    }

    public Node setY(int y) {
        if (y < 0) {
            throw new IllegalArgumentException("y cannot be negative");
        }
        this.y = y;
        return this;
    }

    public NodeType getType() {
        return type;
    }

    public Node setType(NodeType type) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", type=" + type +
                '}';
    }
}


