package com.probendi.itgraph.edge;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

/**
 * Provides methods for executing CRUD operations on an {@link Edge}.
 */
public class EdgeRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * Creates the given edge.
     *
     * @param edge to be created
     */
    public void createEdge(EdgeDTO edge) {
        em.persist(new Edge(edge.source(), edge.target()));
    }

    /**
     * Deletes the edge with the given source and target.
     *
     * @param source the source
     * @param target the target
     * @return the number of deleted edges
     */
    public int deleteEdge(String source, String target) {
        var found = em.find(Edge.class, new Edge(source, target));
        if (found!= null) {
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
    public List<EdgeDTO> findAllEdges() {
        return em.createQuery("SELECT e FROM Edge e", EdgeDTO.class).getResultList();
    }

    /**
     * Returns the edge with the given source and target, or {@code null} if the edge does not exist.
     *
     * @param source the source
     * @param target the target
     * @return the edge with the given source and target, or {@code null} if the edge does not exist
     */
    public EdgeDTO findEdge(String source, String target) {
        return em.find(EdgeDTO.class, new Edge(source, target));
    }
}
