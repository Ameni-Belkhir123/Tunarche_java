package com.example.oeuvre.DAOs;

import com.example.oeuvre.Entities.Oeuvre;
import com.example.oeuvre.Tools.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OeuvreDAO {

    private final Connection connection = DBConnection.getConnection();

    public void createOeuvre(Oeuvre oeuvre) {
        String sql = "INSERT INTO oeuvres (title, description, approved, image_path, creator, gallery_name, creator_id, proposed_galerie_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, oeuvre.getTitle());
            pstmt.setString(2, oeuvre.getDescription());
            pstmt.setBoolean(3, oeuvre.isApproved());
            pstmt.setString(4, oeuvre.getImagePath());
            pstmt.setString(5, oeuvre.getCreator());
            pstmt.setString(6, oeuvre.getGalleryName());
            pstmt.setInt(7, oeuvre.getCreatorId());
            pstmt.setObject(8, oeuvre.getProposedGalerieId()); // Allows null
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Oeuvre getOeuvreById(int id) {
        String sql = "SELECT * FROM oeuvres WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToOeuvre(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Oeuvre> getAllOeuvres() throws SQLException {
        List<Oeuvre> oeuvres = new ArrayList<>();
        String query = "SELECT * FROM oeuvres"; // Modify as per your table structure

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                oeuvres.add(mapResultSetToOeuvre(rs));
            }
        }
        return oeuvres;
    }

    public void updateOeuvre(Oeuvre oeuvre) {
        String sql = "UPDATE oeuvres SET title = ?, description = ?, approved = ?, image_path = ?, creator = ?, gallery_name = ?, galerie_id = ?, proposed_galerie_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, oeuvre.getTitle());
            pstmt.setString(2, oeuvre.getDescription());
            pstmt.setBoolean(3, oeuvre.isApproved());
            pstmt.setString(4, oeuvre.getImagePath());
            pstmt.setString(5, oeuvre.getCreator());
            pstmt.setString(6, oeuvre.getGalleryName());
            pstmt.setObject(7, oeuvre.getGalerieId()); // Allows null
            pstmt.setObject(8, oeuvre.getProposedGalerieId()); // Allows null
            pstmt.setInt(9, oeuvre.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOeuvre(int id) {
        String sql = "DELETE FROM oeuvres WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Oeuvre> getOeuvresByGalerie(int galerieId) {
        List<Oeuvre> oeuvres = new ArrayList<>();
        String sql = "SELECT * FROM oeuvres WHERE galerie_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, galerieId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                oeuvres.add(mapResultSetToOeuvre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oeuvres;
    }

    public List<Oeuvre> getAllOeuvresByUserId(int userId) {
        List<Oeuvre> oeuvres = new ArrayList<>();
        String sql = "SELECT * FROM oeuvres WHERE creator_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                oeuvres.add(mapResultSetToOeuvre(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oeuvres;
    }

    private Oeuvre mapResultSetToOeuvre(ResultSet rs) throws SQLException {
        Oeuvre oeuvre = new Oeuvre();
        oeuvre.setId(rs.getInt("id"));
        oeuvre.setTitle(rs.getString("title"));
        oeuvre.setDescription(rs.getString("description"));
        oeuvre.setApproved(rs.getBoolean("approved"));
        oeuvre.setImagePath(rs.getString("image_path"));
        oeuvre.setCreator(rs.getString("creator"));
        oeuvre.setGalleryName(rs.getString("gallery_name"));
        oeuvre.setGalerieId(rs.getObject("galerie_id", Integer.class));
        oeuvre.setCreatorId(rs.getInt("creator_id"));
        oeuvre.setProposedGalerieId(rs.getObject("proposed_galerie_id", Integer.class));
        return oeuvre;
    }
}
