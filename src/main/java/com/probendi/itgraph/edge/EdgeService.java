package com.probendi.itgraph.edge;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Handles the business logic of an {@link Edge}.
 */
@ApplicationScoped
public class EdgeService {

    @Inject
    EdgeRepository repository;

    /**
     * Creates the given edge.
     *
     * @param edge to be created
     */
    @Transactional
    public void createEdge(@NotNull @Valid EdgeDTO edge) {
        repository.createEdge(edge);
    }

    /**
     * Deletes the edge with the given source and target.
     *
     * @param source the source
     * @param target the target
     * @return the number of deleted edges
     */
    @Transactional
    public int deleteEdge(String source, String target) {
        return repository.deleteEdge(source, target);
    }

    /**
     * Returns all nodes.
     *
     * @return all nodes
     */
    @Transactional
    public List<EdgeDTO> findAllEdges() {
        return repository.findAllEdges();
    }

    /**
     * Returns the edge with the given source and target.
     *
     * @param source the source
     * @param target the target
     * @return the edge with the given source and target
     */
    @Transactional
    public Optional<EdgeDTO> findEdge(String source, String target) {
        return Optional.ofNullable(repository.findEdge(source, target));
    }
}
