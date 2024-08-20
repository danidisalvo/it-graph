package com.probendi.itgraph;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;
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
     * Returns all nodes.
     *
     * @return all nodes
     */
    @Transactional
    public List<NodeDTO> findAllNodes() {
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
     * Updates the given node.
     *
     * @param node the node to be updated
     * @return the number of updated nodes
     */
    @Transactional
    public int updateNode(@NotNull @Valid Node node) {
        return repository.updateNode(node);
    }

    /**
     * Deletes the edge from source to target.
     *
     * @param source the source
     * @param target the target
     * @return the number of deleted edges
     */
//    @Transactional
//    public int deleteEdge(String source, String target) {
//        var edge = validate(source, target);
//        edge.getSource().getEdges().remove(edge.getTarget());
//
//        var node = edge.getSource().removeEdge(edge.getTarget());
//        repository.updateNode(node);
//        return 1;
//    }

//    public Edge pippo(String source, String target) {
//        if (Objects.equals(source, target)) {
//            throw new IllegalArgumentException("Source and target must be different");
//        }
//        var src = repository.findNode(source);
//        if (src == null) {
//            throw new IllegalArgumentException("Source not found");
//        }
//        var tgt = repository.findNode(target);
//        if (tgt == null) {
//            throw new IllegalArgumentException("Target not found");
//        }
//        return new Edge(src, tgt);
//    }
}
