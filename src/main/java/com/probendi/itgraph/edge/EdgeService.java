package com.probendi.itgraph.edge;

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
public class EdgeService {

    private static final String DELETE = "DELETE FROM edge WHERE source = ? AND target= ?";
    private static final String FIND = "SELECT source, target FROM edge WHERE source = ? AND TARGET = ?";
    private static final String FIND_ALL = "SELECT source, target FROM edge";
    private static final String INSERT = "INSERT INTO edge(source, target) VALUES (?, ?)";

    @Inject
    DataSource dataSource;

    @Transactional
    public List<Edge> findAll() throws SQLException {
        List<Edge> edges = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                edges.add(new Edge().setSource(rs.getString(1)).setTarget(rs.getString(2)));
            }
        }
        return edges;
    }

    @Transactional
    public Edge findEdge(String source, String target) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND)) {

            stmt.setString(1, source);
            stmt.setString(2, target);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Edge().setSource(rs.getString(1)).setTarget(rs.getString(2));
                }
            }
        }
        return null;
    }

    @Transactional
    public void createEdge(Edge edge) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(INSERT)) {

            stmt.setString(1, edge.getSource());
            stmt.setString(2, edge.getTarget());
            stmt.executeUpdate();
        }
    }

    @Transactional
    public void deleteEdge(String source, String target) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE)) {

            stmt.setString(1, source);
            stmt.setString(2, target);
            stmt.executeUpdate();
        }
    }
}
