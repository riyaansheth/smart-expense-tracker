package com.expensetracker.dao;

import com.expensetracker.db.DBConnection;
import com.expensetracker.model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public void addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (type, amount, category, date, user_id) VALUES (?, ?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, transaction.getType());
            ps.setDouble(2, transaction.getAmount());
            ps.setString(3, transaction.getCategory());
            ps.setDate(4, Date.valueOf(transaction.getDate()));
            ps.setInt(5, transaction.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding transaction: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public List<Transaction> getAllTransactions() {
        String sql = "SELECT * FROM transactions ORDER BY date DESC";
        List<Transaction> list = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transactions: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
        }
        return list;
    }

    @Override
    public Transaction getTransactionById(int id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transaction by id: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
        }
        return null;
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        String sql = "UPDATE transactions SET type = ?, amount = ?, category = ?, date = ? WHERE id = ?";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, transaction.getType());
            ps.setDouble(2, transaction.getAmount());
            ps.setString(3, transaction.getCategory());
            ps.setDate(4, Date.valueOf(transaction.getDate()));
            ps.setInt(5, transaction.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating transaction: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public void deleteTransaction(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting transaction: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public List<Transaction> searchTransactions(String type, String category, String date) {
        List<Transaction> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM transactions WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (type != null && !type.isEmpty() && !type.equals("ALL")) {
            sql.append(" AND type = ?");
            params.add(type);
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND category LIKE ?");
            params.add("%" + category + "%");
        }
        if (date != null && !date.isEmpty()) {
            sql.append(" AND date = ?");
            params.add(date);
        }
        sql.append(" ORDER BY date DESC");

        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching transactions: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
        }
        return list;
    }

    @Override
    public Map<String, Double> getCategoryWiseSummary() {
        String sql = "SELECT category, SUM(amount) as total FROM transactions WHERE type = 'EXPENSE' GROUP BY category ORDER BY total DESC";
        Map<String, Double> summary = new HashMap<>();
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                summary.put(rs.getString("category"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching category summary: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
        }
        return summary;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String type = rs.getString("type");
        double amount = rs.getDouble("amount");
        String category = rs.getString("category");
        LocalDate date = rs.getDate("date").toLocalDate();
        int userId = rs.getInt("user_id");
        return new Transaction(id, type, amount, category, date, userId);
    }
}
