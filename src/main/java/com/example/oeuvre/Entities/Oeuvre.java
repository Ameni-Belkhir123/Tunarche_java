package com.example.oeuvre.Entities;

import java.time.LocalDateTime;

public class Oeuvre {
    private int id;
    private String title;
    private String description;
    private boolean approved;
    private String imagePath;
    private String creator;
    private String galleryName;
    private int creatorId; // Correction: Renommer userId en creatorId pour la cohérence
    private Integer approvedByAdminId;
    private LocalDateTime approvalDate;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;
    private Integer galerieId;
    private Integer assignedByUserId;
    private Integer proposedGalerieId; // Ajout du champ proposedGalerieId

    public Oeuvre() {
    }

    public Oeuvre(int id, String title, String description, boolean approved, String imagePath, String creator, String galleryName, int creatorId, Integer approvedByAdminId, LocalDateTime approvalDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.approved = approved;
        this.imagePath = imagePath;
        this.creator = creator;
        this.galleryName = galleryName;
        this.creatorId = creatorId;
        this.approvedByAdminId = approvedByAdminId;
        this.approvalDate = approvalDate;
    }

    public Oeuvre(int id, String title, String description, boolean approved, String imagePath, String creator, String galleryName, int creatorId, Integer approvedByAdminId, LocalDateTime approvalDate, LocalDateTime creationDate, LocalDateTime lastModifiedDate, Integer galerieId, Integer assignedByUserId, Integer proposedGalerieId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.approved = approved;
        this.imagePath = imagePath;
        this.creator = creator;
        this.galleryName = galleryName;
        this.creatorId = creatorId;
        this.approvedByAdminId = approvedByAdminId;
        this.approvalDate = approvalDate;
        this.creationDate = creationDate;
        this.lastModifiedDate = lastModifiedDate;
        this.galerieId = galerieId;
        this.assignedByUserId = assignedByUserId;
        this.proposedGalerieId = proposedGalerieId;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getGalleryName() {
        return galleryName;
    }

    public void setGalleryName(String galleryName) {
        this.galleryName = galleryName;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getApprovedByAdminId() {
        return approvedByAdminId;
    }

    public void setApprovedByAdminId(Integer approvedByAdminId) {
        this.approvedByAdminId = approvedByAdminId;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Integer getGalerieId() {
        return galerieId;
    }

    public void setGalerieId(Integer galerieId) {
        this.galerieId = galerieId;
    }

    public Integer getAssignedByUserId() {
        return assignedByUserId;
    }

    public void setAssignedByUserId(Integer assignedByUserId) {
        this.assignedByUserId = assignedByUserId;
    }

    public Integer getProposedGalerieId() {
        return proposedGalerieId;
    }

    public void setProposedGalerieId(Integer proposedGalerieId) {
        this.proposedGalerieId = proposedGalerieId;
    }
}