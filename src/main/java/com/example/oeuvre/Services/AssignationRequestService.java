package com.example.oeuvre.Services;

import com.example.oeuvre.DAOs.AssignationRequestDAO;
import com.example.oeuvre.Entities.AssignationRequest;
import com.example.oeuvre.DAOs.UserDAO;
import jakarta.mail.MessagingException;

import java.util.List;

public class AssignationRequestService {
    private final AssignationRequestDAO assignationRequestDAO = new AssignationRequestDAO();
    private final UserDAO userDAO = new UserDAO();
    private final EmailService emailService = new EmailService();

    public void createRequest(int oeuvreId, int galerieId, int userId) {
        AssignationRequest request = new AssignationRequest();
        request.setOeuvreId(oeuvreId);
        request.setGalerieId(galerieId);
        request.setUserId(userId);
        this.assignationRequestDAO.createRequest(request);
    }

    public List<AssignationRequest> getPendingRequests() {
        return this.assignationRequestDAO.getPendingRequests();
    }

    // 🔥 NEW METHOD: get pending requests filtered by galerie
    public List<AssignationRequest> getPendingRequestsByGalerie(int galerieId) {
        return this.assignationRequestDAO.getPendingRequestsByGalerie(galerieId);
    }

    public void approveRequest(int requestId, int adminId) {
        AssignationRequest request = this.assignationRequestDAO.getRequestById(requestId);
        this.assignationRequestDAO.updateRequestStatus(requestId, "APPROVED", adminId);

        // Get user email and send notification
        String userEmail = userDAO.getUserEmailById(request.getUserId());
        if (userEmail != null && !userEmail.isEmpty()) {
            try {
                emailService.sendApprovalEmail(userEmail);
            } catch (MessagingException e) {
                System.err.println("Failed to send approval email: " + e.getMessage());
            }
        }
    }

    public void rejectRequest(int requestId, int adminId) {
        this.assignationRequestDAO.updateRequestStatus(requestId, "REJECTED", adminId);
    }

    public AssignationRequest getRequestById(int requestId) {
        return this.assignationRequestDAO.getRequestById(requestId);
    }


}