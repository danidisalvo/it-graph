package com.probendi.itgraph;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.*;

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
            em.createNamedQuery("Edge.deleteMany").setParameter("id", found).executeUpdate();
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
    @SuppressWarnings("unchecked")
    public List<NodeDTO> findAllNodes() {
        List<Object[]> rows = em.createNativeQuery(
                        "SELECT n.id, n.x, n.y, n.type, e.target FROM nodes AS n " +
                                "LEFT JOIN edges AS e ON n.id = e.source")
                .getResultList();

        Map<String, NodeDTO> map = new HashMap<>();

        for (Object[] row : rows) {
            var id = (String) row[0];
            var x = (Integer) row[1];
            var y = (Integer) row[2];
            var type = NodeType.valueOf((String) row[3]);
            var target = row.length > 4 ? (String) row[4] : null;

            NodeDTO nodeDTO = map.get(id);
            if (nodeDTO == null) {
                nodeDTO = new NodeDTO(id, x, y, type);
                map.put(id, nodeDTO);
            }
            if (target != null) {
                nodeDTO.edges().add(new Edge(id, target));
            }
        }

        return new ArrayList<>(map.values());
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
