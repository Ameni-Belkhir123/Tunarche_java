package com.example.oeuvre.Entities;

import java.time.LocalDateTime;

public class AssignationRequest {
    private int id;
    private int oeuvreId;
    private int galerieId;
    private int userId;
    private LocalDateTime requestDate;
    private String status; // PENDING, APPROVED, REJECTED
    private LocalDateTime adminActionDate;
    private Integer adminId;

    public AssignationRequest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOeuvreId() {
        return oeuvreId;
    }

    public void setOeuvreId(int oeuvreId) {
        this.oeuvreId = oeuvreId;
    }

    public int getGalerieId() {
        return galerieId;
    }

    public void setGalerieId(int galerieId) {
        this.galerieId = galerieId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAdminActionDate() {
        return adminActionDate;
    }

    public void setAdminActionDate(LocalDateTime adminActionDate) {
        this.adminActionDate = adminActionDate;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }



}