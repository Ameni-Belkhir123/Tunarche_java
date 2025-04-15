package TunArche.test;

import TunArche.entities.Evaluation;
import TunArche.entities.Formation;
import TunArche.services.FormationImpl;
import TunArche.tools.MyConnection;
import TunArche.services.EvaluationImpl;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class mainTest {
    public static void main(String[] args) {
        MyConnection m1 = MyConnection.getInstance();

        FormationImpl formationImpl = new FormationImpl();
        EvaluationImpl evaluationService = new EvaluationImpl();

        // Récupérer toutes les formations
        List<Formation> formations = formationImpl.showAll();

        // Afficher les formations
        System.out.println("Liste des formations : ");
        for (Formation formation : formations) {
            System.out.println("ID: " + formation.getId());
            System.out.println("image" + formation.getImage_name());
            System.out.println("Titre: " + formation.getTitre());
            System.out.println("Description: " + formation.getDescription());
            System.out.println("Date Début: " + formation.getDatedebut());
            System.out.println("Date Fin: " + formation.getDatefin());
            System.out.println("Nombre de Places: " + formation.getNbrplaces());
            System.out.println("Lien: " + formation.getLink());
            List<Evaluation> evaluations = evaluationService.showByFormation(formation.getId());
            if (evaluations.isEmpty()) {
                System.out.println("Évaluations : Aucune évaluation trouvée.");
            } else {
                System.out.println("Évaluations : ");
                for (Evaluation e : evaluations) {
                    System.out.println(" → " + e.getCommentaire() + " (" + e.getNote() + ")");
                }
            }

            System.out.println("--------------------------------------------------");
        }

        // 📅 Définir les dates
        Date dateDebut = new Date(); // Aujourd'hui
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateDebut);
        cal.add(Calendar.DAY_OF_MONTH, 1); // Demain
        Date dateFin = cal.getTime();

        // 📂 Simuler le FileChooser (image sur le bureau)
        String imagePath = "D:\\ameni\\Tun-Arche\\src\\main\\java\\TunArche\\images\\vm01_1200x628-tt-width-1200-height-630-fill-0-crop-1-bgcolor-ffffff-lazyload-1.jpg"; // adapte ce nom
        File imageFile = new File(imagePath);

        if (!imageFile.exists()) {
            System.out.println("❌ Image non trouvée à : " + imagePath);
            return;
        }

//        // 📦 Création de la formation
//        Formation newFormation = new Formation();
//        newFormation.setTitre("Atelier art en plastique");
//        newFormation.setDescription("Formation complète sur comment recycler les plastique et creer un tableau");
//        newFormation.setDatedebut(dateDebut);
//        newFormation.setDatefin(dateFin);
//        newFormation.setNbrplaces(20);
//        newFormation.setLink("https://exemple.com/javafx");
//        newFormation.setImage_name(imageFile.getName());
//        newFormation.setImage_size((int) imageFile.length());
//        newFormation.setUpdated_at(new Date());
//
//        // ✅ Ajout dans la base
//        formationImpl.create(newFormation);

        // 🔍 Affichage après insertion
        System.out.println("\n📚 Liste des formations après ajout :");
        List<Formation> formationss = formationImpl.showAll();

        for (Formation f : formationss) {
            System.out.println("ID: " + f.getId());
            System.out.println("Titre: " + f.getTitre());
            System.out.println("Description: " + f.getDescription());
            System.out.println("Date Début: " + f.getDatedebut());
            System.out.println("Date Fin: " + f.getDatefin());
            System.out.println("Places: " + f.getNbrplaces());
            System.out.println("Lien: " + f.getLink());
            System.out.println("Image: " + f.getImage_name());
            System.out.println("Taille (octets): " + f.getImage_size());
            System.out.println("Mis à jour: " + f.getUpdated_at());

            List<Evaluation> evaluations = evaluationService.showByFormation(f.getId());
            if (evaluations.isEmpty()) {
                System.out.println("Pas d’évaluations.");
            } else {
                for (Evaluation e : evaluations) {
                    System.out.println(" → Evaluation: " + e.getCommentaire() + " (" + e.getNote() + ")");
                }
            }
            System.out.println("-----------------------------------------------------");
        }

        Formation formationToUpdate = new Formation();
        formationToUpdate.setId(13);  // ID de la formation à modifier
        formationToUpdate.setTitre("formation art en plastique");
        formationToUpdate.setDescription("formation avanccée dans l'art de plastique et le recyclave");
        formationToUpdate.setDatedebut(dateDebut);
        formationToUpdate.setDatefin(dateFin);
        formationToUpdate.setNbrplaces(20);
        formationToUpdate.setLink("https://exemple.com/javafx");
        formationToUpdate.setImage_name(imageFile.getName());
        formationToUpdate.setImage_size((int) imageFile.length());
        formationToUpdate.setUpdated_at(new Date());

        formationImpl.update(formationToUpdate);

        System.out.println("\n📚 Liste des formations après modifiction :");
        Formation formation = formationImpl.findById(13);
        if (formation != null) {
            System.out.println("ID: " + formation.getId());
            System.out.println("Titre: " + formation.getTitre());
            System.out.println("Description: " + formation.getDescription());
            System.out.println("Date Début: " + formation.getDatedebut());
            System.out.println("Date Fin: " + formation.getDatefin());
            System.out.println("Nombre de Places: " + formation.getNbrplaces());
            System.out.println("Lien: " + formation.getLink());
            System.out.println("Image: " + formation.getImage_name());
            System.out.println("--------------------------------------------------");
        } else {
            System.out.println("❌ Aucune formation trouvée avec l’ID 13.");
        }


        int formationIdToDelete = 13;

// Supprimer la formation
        formationImpl.delete(formationIdToDelete);
        System.out.println("✅ Formation avec ID " + formationIdToDelete + " supprimée.");

// Affichage des formations après suppression
        System.out.println("\n📚 Liste des formations après suppression :");
        for (Formation formationn : formations) {
            System.out.println("ID: " + formationn.getId());
            System.out.println("Titre: " + formationn.getTitre());
            System.out.println("Description: " + formationn.getDescription());
            System.out.println("Date Début: " + formationn.getDatedebut());
            System.out.println("Date Fin: " + formationn.getDatefin());
            System.out.println("Nombre de Places: " + formationn.getNbrplaces());
            System.out.println("Lien: " + formationn.getLink());
            System.out.println("Image: " + formationn.getImage_name());
            System.out.println("--------------------------------------------------");
        }

        // Création d'une évaluation pour une formation existante (ex: id = 2)
        Formation formationns = formationImpl.findById(2);

        if (formationns != null) {
            Evaluation nouvelleEvaluation = new Evaluation();
            nouvelleEvaluation.setCommentaire("Très bonne formation !");
            nouvelleEvaluation.setNote(5); // note sur 5
            nouvelleEvaluation.setFormation(formationns); // lien vers la formation

            EvaluationImpl evaluationImpl = new EvaluationImpl();
            evaluationImpl.create(nouvelleEvaluation);

            System.out.println("✅ Évaluation ajoutée à la formation : " + formationns.getTitre());
        } else {
            System.out.println("❌ Formation avec ID 2 introuvable.");
        }

        int formationIdToDisplay = 2; // l'ID de la formation que tu veux afficher

        Formation f = formationImpl.findById(formationIdToDisplay);

        if (f != null) {
            System.out.println("📘 Formation :");
            System.out.println("ID: " + f.getId());
            System.out.println("Titre: " + f.getTitre());
            System.out.println("Description: " + f.getDescription());
            System.out.println("Date Début: " + f.getDatedebut());
            System.out.println("Date Fin: " + f.getDatefin());
            System.out.println("Nombre de Places: " + f.getNbrplaces());
            System.out.println("Lien: " + f.getLink());
            System.out.println("Image: " + f.getImage_name());


            List<Evaluation> evaluations = evaluationService.showByFormation(f.getId());

            if (evaluations.isEmpty()) {
                System.out.println("→ Pas d’évaluations pour cette formation.");
            } else {
                System.out.println("🗨️ Évaluations :");
                for (Evaluation e : evaluations) {
                    System.out.println(" - Commentaire: " + e.getCommentaire());
                    System.out.println("   Note: " + e.getNote() + "/5");
                }
            }
        } else {
            System.out.println("❌ Formation avec ID " + formationIdToDisplay + " introuvable.");
        }



    }
}
