package com.probendi.itgraph.node;

/**
 * The DTO of a {@link Node}.
 *
 * @param id     the id
 * @param x the X coordinate
 * @param y the Y coordinate
 * @param type the node's type
 */
public record NodeDTO(String id, int x, int y, NodeType type) {
}
