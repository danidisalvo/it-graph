package com.probendi.itgraph.node;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Objects;

/**
 * A graph's node.
 */
@Entity
@Table(name = "nodes")
@JsonPropertyOrder({"id", "x", "y", "type"})
public class Node {

    @Id
    @NotBlank(message = "id must not be blank")
    private String id;
    @PositiveOrZero
    private int x;
    @PositiveOrZero
    private int y;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "type must not be null")
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
        this.id = id;
        return this;
    }

    public int getX() {
        return x;
    }

    public Node setX(int x) {
        this.x = x;
        return this;
    }

    public int getY() {
        return y;
    }

    public Node setY(int y) {
        this.y = y;
        return this;
    }

    public NodeType getType() {
        return type;
    }

    public Node setType(NodeType type) {
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


