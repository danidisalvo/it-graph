package com.probendi.itgraph;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.*;

/**
 * Provides methods for executing CRUD operations on a {@link Node}.
 *
 * @author Daniele Di Salvo
 * @since 1.0.0
 */
@ApplicationScoped
public class NodeRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * Creates an edge from source to target.
     *
     * @param source the source
     * @param target the target
     */
    public void createEdge(String source, String target) {
        var from = em.find(Node.class, source);
        if (from == null) {
            throw new IllegalArgumentException("Source not found");
        }

        var to = em.find(Node.class, target);
        if (to == null) {
            throw new IllegalArgumentException("Target not found");
        }

        if (from.getEdges().contains(to)) {
            throw new IllegalArgumentException("Edge already exists");
        }

        from.getEdges().add(to);
        em.merge(from);
    }

    /**
     * Creates the given node.
     *
     * @param node the node to be created
     */
    public void createNode(Node node) {
        em.persist(node);
    }

    /**
     * Delete all nodes.
     */
    public void deleteAllNodes() {
        em.createQuery("DELETE FROM Node").executeUpdate();
    }

    /**
     * Deletes the edge from source to target.
     *
     * @param source the source
     * @param target the target
     * @return the number of deleted edges
     */
    @Transactional
    public int deleteEdge(String source, String target) {
        var from = em.find(Node.class, source);
        if (from == null) {
            throw new IllegalArgumentException("Source not found");
        }

        var to = em.find(Node.class, target);
        if (to == null) {
            throw new IllegalArgumentException("Target not found");
        }

        if (from.getEdges().contains(to)) {
            from.removeEdge(to);
            em.merge(from);
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Deletes the node with the given id.
     *
     * @param id the id
     * @return the number of deleted nodes
     */
    public int deleteNode(String id) {
        var found = em.find(Node.class, id);
        if (found != null) {
            em.remove(found);
            return 1;
        }
        return 0;
    }

    /**
     * Returns the graph.
     *
     * @return the graph
     */
    @SuppressWarnings("unchecked")
    public Graph fetchGraph() {
        List<Object[]> rows = em.createNativeQuery(
                        "SELECT n.id, n.x, n.y, n.type, e.target FROM nodes AS n " +
                                "LEFT JOIN edges AS e ON n.id = e.source")
                .getResultList();

        Graph graph = new Graph();

        for (Object[] row : rows) {
            var id = (String) row[0];
            var x = (Integer) row[1];
            var y = (Integer) row[2];
            var type = NodeType.valueOf((String) row[3]);
            var target = (String) row[4];

            graph.addNode(new Node().setId(id).setX(x).setY(y).setType(type));
            if (target != null) {
                graph.addEdge(new Edge(id, target));
            }
        }

        return graph;
    }

    /**
     * Returns the node with the given id, or {@code null} if the node does not exist.
     *
     * @param id the id
     * @return the node with the given id, or {@code null} if the node does not exist
     */
    public Node findNode(String id) {
        return em.find(Node.class, id);
    }

    /**
     * Updates the given node.
     *
     * @param node the node to be updated
     * @return the number of updated nodes
     */
    public int updateNode(Node node) {
        var found = em.find(Node.class, node.getId());
        if (found != null) {
            found.setX(node.getX());
            found.setY(node.getY());
            found.setType(node.getType());
            em.merge(found);
            return 1;
        }
        return 0;
    }
}
