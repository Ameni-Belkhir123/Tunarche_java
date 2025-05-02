package com.example.oeuvre.Interfaces;

import com.example.oeuvre.Entities.Oeuvre;
import java.util.List;

public interface IOeuvreService {
    void createOeuvre(Oeuvre oeuvre, int userId);
    void updateOeuvre(Oeuvre oeuvre);
    void deleteOeuvre(int oeuvreId);
    void approveOeuvre(int oeuvreId, int adminId);
    List<Oeuvre> getAllOeuvres();
    Oeuvre getOeuvreById(int oeuvreId);
    List<Oeuvre> getOeuvresByUser(int userId);
    void assignOeuvreToGalerie(int oeuvreId, int galerieId, int userId);
    List<Oeuvre> getOeuvresByGalerie(int galerieId);
    List<Oeuvre> getPendingApprovalOeuvres();
}