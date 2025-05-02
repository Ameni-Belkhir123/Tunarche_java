package com.example.oeuvre.DAOs;

import com.example.oeuvre.Entities.User;
import com.example.oeuvre.Tools.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;

public class UserDAO {

    private final Connection connection = DBConnection.getConnection();

    public User getUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    public void registerUser(User user) throws SQLException {
        String query = "INSERT INTO users (username, password, email, registration_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User newUser = new User();
        newUser.setId(rs.getInt("id"));
        newUser.setUsername(rs.getString("username"));
        newUser.setPassword(rs.getString("password"));
        newUser.setEmail(rs.getString("email"));
        newUser.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime());
        return newUser;
    }

    public String getUserEmailById(int userId) {
        String sql = "SELECT email FROM users WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}