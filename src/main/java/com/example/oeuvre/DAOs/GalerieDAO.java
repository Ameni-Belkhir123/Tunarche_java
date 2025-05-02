package com.example.oeuvre.DAOs;

import com.example.oeuvre.Entities.Galerie;
import com.example.oeuvre.Tools.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public class GalerieDAO {
    private Connection connection = DBConnection.getConnection();

    public void save(Galerie galerie) {
        String query = "INSERT INTO galeries (name, description, address) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, galerie.getName());
            stmt.setString(2, galerie.getDescription());
            stmt.setString(3, galerie.getAddress());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving gallery", e);
        }
    }

    public void update(Galerie galerie) {
        String query = "UPDATE galeries SET name = ?, description = ?, address = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, galerie.getName());
            stmt.setString(2, galerie.getDescription());
            stmt.setString(3, galerie.getAddress());
            stmt.setInt(4, galerie.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating gallery", e);
        }
    }

    public void delete(int galerieId) {
        String query = "DELETE FROM galeries WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, galerieId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting gallery", e);
        }
    }

    public List<Galerie> findAll() {
        List<Galerie> galeries = new ArrayList<>();
        String query = "SELECT * FROM galeries";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Galerie galerie = new Galerie(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("address"),
                        null // imagePath is removed
                );
                galeries.add(galerie);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching galleries", e);
        }
        return galeries;
    }

    public Galerie findById(int galerieId) {
        String sql = "SELECT * FROM galeries WHERE id = ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, galerieId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Galerie galerie = new Galerie();
                galerie.setId(rs.getInt("id"));
                galerie.setName(rs.getString("name"));
                galerie.setDescription(rs.getString("description"));
                galerie.setAddress(rs.getString("address"));
                return galerie;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding gallerie", e);
        }
        return null;
    }
}
