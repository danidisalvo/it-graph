package com.probendi.itgraph;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.*;

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

    @ManyToMany
    @JoinTable(
            name = "edges",
            joinColumns = @JoinColumn(name = "source"),
            inverseJoinColumns = @JoinColumn(name = "target")
    )
    private Set<Node> edges = new HashSet<>();

    public Node() {
    }

    public Node(String id, int x, int y, NodeType type) {
        setId(id);
        setX(x);
        setY(y);
        setType(type);
    }

    public Node(NodeDTO node) {
        this(node.id(), node.x(), node.y(), node.type());
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

    public Set<Node> getEdges() {
        return edges;
    }

    public Node setEdges(Set<Node> nodes) {
        edges.clear();
        nodes.forEach(this::addEdge);
        return this;
    }

    public Node addEdge(Node node) {
        edges.add(node);
        return this;
    }

    public boolean removeEdge(Node node) {
        return edges.remove(node);
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


