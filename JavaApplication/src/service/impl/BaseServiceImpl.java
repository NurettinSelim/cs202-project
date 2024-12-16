package service.impl;

import service.BaseService;
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseServiceImpl<T, ID> implements BaseService<T, ID> {
    protected abstract String getTableName();
    protected abstract String getIdColumnName();
    protected abstract T mapRow(ResultSet rs) throws SQLException;
    protected abstract void setCreateStatement(PreparedStatement stmt, T entity) throws SQLException;
    protected abstract void setUpdateStatement(PreparedStatement stmt, T entity) throws SQLException;

    @Override
    public T create(T entity) {
        String sql = String.format("INSERT INTO %s VALUES (?)", getTableName());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setCreateStatement(stmt, entity);
            stmt.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating entity", e);
        }
    }

    @Override
    public T findById(ID id) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", getTableName(), getIdColumnName());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding entity by ID", e);
        }
    }

    @Override
    public List<T> findAll() {
        String sql = String.format("SELECT * FROM %s", getTableName());
        List<T> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all entities", e);
        }
    }

    @Override
    public T update(T entity) {
        String sql = String.format("UPDATE %s SET ? WHERE %s = ?", getTableName(), getIdColumnName());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setUpdateStatement(stmt, entity);
            stmt.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating entity", e);
        }
    }

    @Override
    public void delete(ID id) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?", getTableName(), getIdColumnName());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting entity", e);
        }
    }

    @Override
    public boolean exists(ID id) {
        String sql = String.format("SELECT 1 FROM %s WHERE %s = ?", getTableName(), getIdColumnName());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking entity existence", e);
        }
    }

    protected void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 