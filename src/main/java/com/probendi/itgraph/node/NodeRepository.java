package com.probendi.itgraph.node;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

/**
 * Provides methods for executing CRUD operations on a {@link Node}.
 *
 * @since 1.0.0
 */
@ApplicationScoped
public class NodeRepository {

    @PersistenceContext
    private EntityManager em;

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
     * Returns all nodes.
     *
     * @return all nodes
     */
    public List<Node> findAllNodes() {
        return em.createQuery("SELECT n FROM Node n", Node.class).getResultList();
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
        try {
            var found = em.find(Node.class, node.getId());
            found.setX(node.getX());
            found.setY(node.getY());
            found.setType(node.getType());
            em.merge(found);
            return 1;
        } catch (NoResultException e) {
            return 0;
        }
    }
}
