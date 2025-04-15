package TunArche.test;

import TunArche.entities.Concours;
import TunArche.entities.Participation;
import TunArche.services.Concourslmpl;
import TunArche.services.Participationlmpl;
import TunArche.tools.MyConnection;

import java.time.LocalDate;
import java.util.List;

public class mainTest {
    public static void main(String[] args) {
        // 🔌 Connexion à la base de données
        MyConnection m1 = MyConnection.getInstance();
        if (m1 == null) {
            System.out.println("❌ Échec de la connexion à la base de données");
            return;
        }

        Concourslmpl concoursService = new Concourslmpl();
        Participationlmpl participationService = new Participationlmpl();

        // 1️⃣ TEST CRUD CONCOURS
        testCrudConcours(concoursService);

        // 2️⃣ TEST CRUD PARTICIPATION
        testCrudParticipation(participationService);
    }

    private static void testCrudConcours(Concourslmpl concoursService) {
        System.out.println("\n🏆 TEST CRUD CONCOURS");

        // 📋 Affichage initial
        afficherTousConcours(concoursService);

        // ➕ CREATE
        Concours newConcours = new Concours();
        newConcours.setTitre("Concours de dessin 2025");
        newConcours.setDescription("Concours national de dessin pour jeunes talents");
        newConcours.setDateDebut(LocalDate.parse("2025-05-01"));  // Utilisation de setDateDebut
        newConcours.setDateFin(LocalDate.parse("2025-07-01"));    // Utilisation de setDateFin

        concoursService.create(newConcours);
        System.out.println("\n✅ Concours ajouté avec succès");

        // 🔍 READ after CREATE
        System.out.println("\n📋 Liste après ajout :");
        afficherTousConcours(concoursService);

        // ✏️ UPDATE
        List<Concours> concoursList = concoursService.showAll();
        if (!concoursList.isEmpty()) {
            Concours concoursToUpdate = concoursList.get(0);
            concoursToUpdate.setTitre("Concours art en plastique (mis à jour)");
            concoursService.update(concoursToUpdate);

            System.out.println("\n✏️ Concours après modification :");
            Concours updated = concoursService.findById(concoursToUpdate.getId());
            afficherConcours(updated);
        }

        // 🗑️ DELETE
        if (!concoursList.isEmpty()) {
            int idToDelete = concoursList.get(0).getId();
            concoursService.delete(idToDelete);
            System.out.println("\n🗑️ Concours avec ID " + idToDelete + " supprimé");
        }

        // 🔍 FINAL CHECK
        System.out.println("\n📋 Liste finale des concours :");
        afficherTousConcours(concoursService);
    }

    private static void testCrudParticipation(Participationlmpl participationService) {
        System.out.println("\n🎨 TEST CRUD PARTICIPATION");

        // ➕ CREATE
        Participation newParticipation = new Participation();
        newParticipation.setConcours_id(1); // ID concours existant
        newParticipation.setOeuvre_id(101);
        newParticipation.setDate_inscription("2025-04-13");
        newParticipation.setNom_artiste("Sahar Artiste");
        newParticipation.setEmail_artiste("sahar@example.com");
        newParticipation.setNbr_votes(15);
        newParticipation.setImage_path("images/oeuvre1.jpg");

        participationService.create(newParticipation);
        System.out.println("\n✅ Participation ajoutée avec succès");

        // 🔍 READ
        System.out.println("\n📋 Participations pour concours ID 1 :");
        List<Participation> participations = participationService.showByConcours(1);
        participations.forEach(mainTest::afficherParticipation);

        // ✏️ UPDATE
        if (!participations.isEmpty()) {
            Participation p = participations.get(0);
            p.setNom_artiste("Sahar Recycl'Art");
            participationService.update(p);

            System.out.println("\n✏️ Participation après modification :");
            Participation updated = participationService.findById(p.getId());
            afficherParticipation(updated);
        }

        // 🗑️ DELETE
        if (!participations.isEmpty()) {
            int idToDelete = participations.get(0).getId();
            participationService.delete(idToDelete);
            System.out.println("\n🗑️ Participation avec ID " + idToDelete + " supprimée");
        }

        // 🔍 FINAL CHECK
        System.out.println("\n📋 Participations restantes :");
        participationService.showByConcours(1).forEach(mainTest::afficherParticipation);
    }

    // Méthodes d'affichage
    private static void afficherTousConcours(Concourslmpl service) {
        List<Concours> liste = service.showAll();
        if (liste.isEmpty()) {
            System.out.println("Aucun concours disponible");
        } else {
            liste.forEach(mainTest::afficherConcours);
        }
    }

    private static void afficherConcours(Concours c) {
        System.out.println("┌──────────────────────────────");
        System.out.println("│ ID: " + c.getId());
        System.out.println("│ Titre: " + c.getTitre());
        System.out.println("│ Description: " + c.getDescription());
        System.out.println("│ Date Début: " + c.getDateDebut());  // Utilisation de getDateDebut
        System.out.println("│ Date Fin: " + c.getDateFin());      // Utilisation de getDateFin
        System.out.println("└──────────────────────────────");
    }

    private static void afficherParticipation(Participation p) {
        System.out.println("┌──────────────────────────────");
        System.out.println("│ ID: " + p.getId());
        System.out.println("│ Concours ID: " + p.getConcours_id());
        System.out.println("│ Oeuvre ID: " + p.getOeuvre_id());
        System.out.println("│ Artiste: " + p.getNom_artiste());
        System.out.println("│ Email: " + p.getEmail_artiste());
        System.out.println("│ Votes: " + p.getNbr_votes());
        System.out.println("│ Image: " + p.getImage_path());
        System.out.println("└──────────────────────────────");
    }
}
