package com.example.oeuvre.DAOs;

import com.example.oeuvre.Entities.AssignationRequest;
import com.example.oeuvre.Tools.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssignationRequestDAO {
    private static final Logger LOGGER = Logger.getLogger(AssignationRequestDAO.class.getName());
    private final Connection dbConnection = DBConnection.getConnection();

    public void createRequest(AssignationRequest request) {
        String sql = "INSERT INTO assignationrequest (oeuvre_id, galerie_id, user_id, request_date, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = this.dbConnection.prepareStatement(sql)) {
            pstmt.setInt(1, request.getOeuvreId());
            pstmt.setInt(2, request.getGalerieId());
            pstmt.setInt(3, request.getUserId());
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(5, "PENDING");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la demande", e);
        }
    }

    // Fix the getPendingRequests method SQL query
    public List<AssignationRequest> getPendingRequests() {
        List<AssignationRequest> requests = new ArrayList<>();
        // Correct the column name from 'userId' to 'user_id'
        String sql = "SELECT id, oeuvre_id, galerie_id, user_id, request_date, status, admin_action_date, admin_id FROM assignationrequest WHERE status = 'PENDING'";

        try (Statement stmt = this.dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des demandes", e);
        }

        return requests;
    }

    // 🔥 NEW METHOD: Get pending requests by selected galerie
    public List<AssignationRequest> getPendingRequestsByGalerie(int galerieId) {
        List<AssignationRequest> requests = new ArrayList<>();
        String sql = "SELECT id, oeuvre_id, galerie_id, user_id, request_date, status, admin_action_date, admin_id FROM assignationrequest WHERE status = 'PENDING' AND galerie_id = ?";

        try (PreparedStatement pstmt = this.dbConnection.prepareStatement(sql)) {
            pstmt.setInt(1, galerieId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToRequest(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des demandes par galerie", e);
        }

        return requests;
    }

    public void updateRequestStatus(int requestId, String status, Integer adminId) {
        String sql = "UPDATE assignationrequest SET status = ?, admin_action_date = ?, admin_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = this.dbConnection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            if (adminId != null) {
                pstmt.setInt(3, adminId);
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setInt(4, requestId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du statut de la demande", e);
        }
    }

    public AssignationRequest getRequestById(int requestId) {
        String sql = "SELECT id, oeuvre_id, galerie_id, user_id, request_date, status, admin_action_date, admin_id FROM assignationrequest WHERE id = ?";

        try (PreparedStatement pstmt = this.dbConnection.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRequest(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la demande par ID", e);
        }

        return null;
    }

    // ✨ NEW METHOD: Get all assignation requests
    public List<AssignationRequest> getAllRequests() {
        List<AssignationRequest> allRequests = new ArrayList<>();
        String sql = "SELECT id, oeuvre_id, galerie_id, user_id, request_date, status, admin_action_date, admin_id FROM assignationrequest";

        try (Statement stmt = this.dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                allRequests.add(mapResultSetToRequest(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de toutes les demandes", e);
        }

        return allRequests;
    }

    // 🔥 Helper method to avoid repeating ResultSet mapping
    private AssignationRequest mapResultSetToRequest(ResultSet rs) throws SQLException {
        AssignationRequest request = new AssignationRequest();
        request.setId(rs.getInt("id"));
        request.setOeuvreId(rs.getInt("oeuvre_id"));
        request.setGalerieId(rs.getInt("galerie_id"));
        request.setUserId(rs.getInt("user_id"));
        request.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());
        request.setStatus(rs.getString("status"));

        Timestamp adminActionTimestamp = rs.getTimestamp("admin_action_date");
        request.setAdminActionDate(adminActionTimestamp != null ? adminActionTimestamp.toLocalDateTime() : null);

        int adminId = rs.getInt("admin_id");
        request.setAdminId(rs.wasNull() ? null : adminId);

        return request;
    }
}