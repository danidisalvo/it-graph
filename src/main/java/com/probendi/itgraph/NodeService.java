package com.probendi.itgraph;

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
 *
 * @author Daniele Di Salvo
 * @since 1.0.0
 */
@ApplicationScoped
public class NodeService {

    @Inject
    NodeRepository repository;

    /**
     * Explicit empty constructor.
     */
    public NodeService() {
    }

    /**
     * Creates an edge from source to target.
     *
     * @param source the source
     * @param target the target
     */
    @Transactional
    public void createEdge(@NotBlank(message = "source must not be blank") String source,
                           @NotBlank(message = "target must not be blank") String target) {
        repository.createEdge(source, target);
    }

    /**
     * Creates the given node.
     *
     * @param node the node to be created
     */
    @Transactional
    public void createNode(@NotNull @Valid Node node) {
        if (repository.findNode(node.getId()) != null) {
            throw new IllegalArgumentException("Duplicated node");
        }
        repository.createNode(node);
    }

    /**
     * Delete all nodes.
     */
    @Transactional
    public void deleteAllNodes() {
        repository.deleteAllNodes();
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
        return repository.deleteEdge(source, target);
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
     * Returns the graph.
     *
     * @return the graph
     */
    @Transactional
    public Graph fetchGraph() {
        return repository.fetchGraph();
    }

    /**
     * Returns the IDs of all lexemes linked to the given node, root node excluded.
     *
     * @param node the node
     * @param root the root node
     * @return the IDs of all lexemes linked to the given node, root node excluded
     */
    public List<String> fetchLinkedLexemes(String node, String root) {
        return repository.fetchLinkedLexemes(node, root);
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
