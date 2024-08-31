package com.probendi.itgraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * A graph's node.
 *
 * @author Daniele Di Salvo
 * @since 1.0.0
 */
@Entity
@Table(name = "nodes")
@JsonPropertyOrder({"id", "x", "y", "type"})
@NamedNativeQuery(
        name = "Node.findIncomingLexemes",
        query = "SELECT e.source FROM edges e INNER JOIN nodes n ON e.source = n.id " +
                "WHERE e.target = :target AND n.type = 'LEXEME' AND e.source != :root ORDER BY e.source",
        resultClass = String.class
)
@SuppressWarnings("unused")
public class Node implements Comparable<Node> {

    @Id
    @NotBlank(message = "id must not be blank")
    private String id;
    private int x;
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
    @JsonIgnore
    private final Set<Node> edges = new HashSet<>();

    /**
     * Explicit empty constructor.
     */
    public Node() {
    }

    /**
     * Creates a node with the given values
     *
     * @param id   the id
     * @param x    the x coordinate
     * @param y    the y coordinate
     * @param type the type
     */
    public Node(String id, int x, int y, NodeType type) {
        setId(id);
        setX(x);
        setY(y);
        setType(type);
    }

    /**
     * Returns the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to be set
     * @return this node
     */
    public Node setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the x coordinate.
     *
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x coordinate.
     *
     * @param x the x coordinate to be set
     * @return this node
     */
    public Node setX(int x) {
        this.x = x;
        return this;
    }

    /**
     * Returns the y coordinate.
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y coordinate.
     *
     * @param y the y coordinate to be set
     * @return this node
     */
    public Node setY(int y) {
        this.y = y;
        return this;
    }

    /**
     * Returns the type.
     *
     * @return the type
     */
    public NodeType getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the type to be set
     * @return this node
     */
    public Node setType(NodeType type) {
        this.type = type;
        return this;
    }

    /**
     * Returns the edges.
     *
     * @return the edges
     */
    public Set<Node> getEdges() {
        return edges;
    }

    /**
     * Sets the edges. It calls {@link #addEdge(Node)} for each edge.
     *
     * @param edges the edges to set
     * @return this node
     */
    public Node setEdges(Set<Node> edges) {
        this.edges.clear();
        edges.forEach(this::addEdge);
        return this;
    }

    /**
     * Adds an edge to the given target node.
     *
     * @param node the target node
     * @return this node
     */
    public Node addEdge(Node node) {
        edges.add(node);
        node.getEdges().add(this);
        return this;
    }

    /**
     * Removes the edge to the given target node.
     *
     * @param node the target node
     */
    public void removeEdge(Node node) {
        edges.remove(node);
        node.getEdges().remove(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node node)) {
            return false;
        }
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

    @Override
    public int compareTo(@NotNull Node o) {
        return id.compareTo(o.id);
    }

    /**
     * Removes the edges of the node that is going to be removed.
     */
    @PreRemove
    private void removeEdges() {
        new TreeSet<>(edges).forEach(this::removeEdge);
    }
}


