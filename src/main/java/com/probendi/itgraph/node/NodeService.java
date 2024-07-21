package com.probendi.itgraph.node;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Exposes the persistence services to add, delete, and update edges.
 */
@ApplicationScoped
public class NodeService {

    private static final String DELETE = "DELETE FROM node WHERE id = ?";
    private static final String DELETE_ALL = "DELETE FROM node";
    private static final String FIND = "SELECT id, x, y, type FROM node WHERE id = ?";
    private static final String FIND_ALL = "SELECT id, x, y, type FROM node";
    private static final String INSERT = "INSERT INTO node(id, x, y, type) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE node SET x = ?, y = ?, type = ? WHERE id = ?";

    @Inject
    DataSource dataSource;

    @Transactional
    public List<Node> findAll() throws SQLException {
        List<Node> nodes = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                nodes.add(new Node()
                        .setId(rs.getString(1))
                        .setX(rs.getInt(2))
                        .setY(rs.getInt(3))
                        .setType(NodeType.valueOf(rs.getString(4))));
            }
        }
        return nodes;
    }

    @Transactional
    public Node findNode(String id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Node()
                            .setId(rs.getString(1))
                            .setX(rs.getInt(2))
                            .setY(rs.getInt(3))
                            .setType(NodeType.valueOf(rs.getString(4)));
                }
            }
        }
        return null;
    }

    @Transactional
    public void createNode(Node node) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(INSERT)) {

            stmt.setString(1, node.getId());
            stmt.setInt(2, node.getX());
            stmt.setInt(3, node.getY());
            stmt.setString(4, node.getType().name());
            stmt.executeUpdate();
        }
    }

    @Transactional
    public void deleteAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE_ALL)) {

            stmt.executeUpdate();
        }
    }

    @Transactional
    public void deleteNode(String id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    @Transactional
    public void updateNode(Node node) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(UPDATE)) {

            stmt.setInt(1, node.getX());
            stmt.setInt(2, node.getY());
            stmt.setString(3, node.getType().name());
            stmt.setString(4, node.getId());
            stmt.executeUpdate();
        }
    }
}
