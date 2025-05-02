package com.example.oeuvre.Services;

import com.example.oeuvre.DAOs.OeuvreDAO;
import com.example.oeuvre.Entities.Oeuvre;
import com.example.oeuvre.Interfaces.IOeuvreService;

import java.sql.SQLException;
import java.util.List;

public class OeuvreService implements IOeuvreService {
    private final OeuvreDAO oeuvreDAO = new OeuvreDAO();

    @Override
    public void createOeuvre(Oeuvre oeuvre, int userId) {
        oeuvre.setCreatorId(userId);
        oeuvreDAO.createOeuvre(oeuvre); // Correction: Utiliser la méthode createOeuvre existante
    }

    @Override
    public void updateOeuvre(Oeuvre oeuvre) {
        oeuvreDAO.updateOeuvre(oeuvre); // Correction: Utiliser la méthode updateOeuvre existante
    }

    @Override
    public void deleteOeuvre(int oeuvreId) {
        oeuvreDAO.deleteOeuvre(oeuvreId); // Correction: Utiliser la méthode deleteOeuvre existante
    }

    @Override
    public void approveOeuvre(int oeuvreId, int adminId) {
        Oeuvre oeuvre = oeuvreDAO.getOeuvreById(oeuvreId); // Correction: Utiliser la méthode getOeuvreById existante
        if (oeuvre != null) {
            oeuvre.setApproved(true);
            oeuvre.setApprovedByAdminId(adminId);
            oeuvreDAO.updateOeuvre(oeuvre); // Correction: Utiliser la méthode updateOeuvre existante
        }
    }

    @Override
    public List<Oeuvre> getAllOeuvres() {
        try {
            return oeuvreDAO.getAllOeuvres(); // This may throw SQLException
        } catch (SQLException e) {
            e.printStackTrace();  // Log the exception for debugging
            // Optionally, you could handle the error more gracefully or return an empty list
            return null; // Or Collections.emptyList() for a more graceful fallback
        }
    }

    @Override
    public Oeuvre getOeuvreById(int oeuvreId) {
        return oeuvreDAO.getOeuvreById(oeuvreId); // Correction: Utiliser la méthode getOeuvreById existante
    }

    @Override
    public List<Oeuvre> getOeuvresByUser(int userId) {
        return oeuvreDAO.getAllOeuvresByUserId(userId); // Correction: Utiliser la méthode getAllOeuvresByUserId existante
    }

    @Override
    public void assignOeuvreToGalerie(int oeuvreId, int galerieId, int userId) {
        Oeuvre oeuvre = oeuvreDAO.getOeuvreById(oeuvreId); // Correction: Utiliser la méthode getOeuvreById existante
        if (oeuvre != null) {
            oeuvre.setGalerieId(galerieId);
            oeuvreDAO.updateOeuvre(oeuvre); // Correction: Utiliser la méthode updateOeuvre existante
            // Vous pourriez vouloir enregistrer l'utilisateur qui a effectué l'assignation
            // Si vous avez une colonne dédiée pour cela, vous devrez la mettre à jour ici.
        }
    }

    @Override
    public List<Oeuvre> getOeuvresByGalerie(int galerieId) {
        return oeuvreDAO.getOeuvresByGalerie(galerieId); // Correction: Utiliser la méthode getOeuvresByGalerie existante
    }

    @Override
    public List<Oeuvre> getPendingApprovalOeuvres() {
        // Vous devrez ajouter une méthode correspondante dans votre OeuvreDAO
        // Exemple (si vous l'ajoutez) : return oeuvreDAO.findOeuvresByApprovalStatus(false);
        // En attendant, vous pouvez retourner null ou une liste vide si cette fonctionnalité n'est pas encore implémentée.
        return null; // À implémenter
    }

    // Nouvelle méthode pour récupérer les œuvres par ID utilisateur (correspondant à la demande précédente)
    public List<Oeuvre> getAllOeuvresByUserId(int userId) {
        return oeuvreDAO.getAllOeuvresByUserId(userId);
    }
}