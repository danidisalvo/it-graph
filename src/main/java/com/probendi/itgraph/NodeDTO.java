package com.probendi.itgraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The DTO of a {@link Node}.
 *
 * @param id    the id
 * @param x     the X coordinate
 * @param y     the Y coordinate
 * @param type  the node's type
 * @param edges the edges
 */
public record NodeDTO(String id, int x, int y, NodeType type, List<Edge> edges) {

    public NodeDTO(String id, int x, int y, NodeType type) {
        this(id, x, y, type, new ArrayList<>());
    }
}
