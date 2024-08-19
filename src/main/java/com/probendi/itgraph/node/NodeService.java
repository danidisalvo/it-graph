package com.probendi.itgraph.node;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Handles the business logic of a {@link Node}.
 */
@ApplicationScoped
public class NodeService {

    @Inject
    NodeRepository repository;

    /**
     * Creates the given node.
     *
     * @param node the node to be created
     */
    @Transactional
    public void createNode(@NotNull @Valid Node node) {
        repository.createNode(node);
    }

    /**
     * Returns all nodes.
     *
     * @return all nodes
     */
    @Transactional
    public List<Node> findAllNodes() {
        return repository.findAllNodes();
    }

    /**
     * Returns the node with the given id.
     *
     * @param id the id
     * @return the node with the given id
     */
    @Transactional
    public Optional<Node> findNode(@NotBlank(message = "id must not be blank") String id) {
        return Optional.ofNullable(repository.findNode(id));
    }

    /**
     * Delete all nodes.
     */
    @Transactional
    public void deleteAllNodes() {
        repository.deleteAllNodes();
    }

    /**
     * Deletes the node with the given id.
     *
     * @param id the id
     * @return the number of deleted nodes
     */
    @Transactional
    public int deleteNode(@NotBlank(message = "id must not be blank") String id) {
        return repository.deleteNode(id);
    }

    /**
     * Updates the given node.
     *
     * @param node the node to be updated
     * @return the number of updated nodes
     */
    @Transactional
    public int updateNode(@NotNull @Valid Node node) {
        return repository.updateNode(node);
    }
}
