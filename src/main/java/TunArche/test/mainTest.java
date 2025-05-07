package TunArche.test;

import TunArche.entities.Evaluation;
import TunArche.entities.Formation;
import TunArche.services.FormationImpl;
import TunArche.tools.MyConnection;
import TunArche.services.EvaluationImpl;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class mainTest {
    public static void main(String[] args) {
        MyConnection m1 = MyConnection.getInstance();

        FormationImpl formationService = new FormationImpl();
        EvaluationImpl evaluationService = new EvaluationImpl();

        // 1. Affichage de toutes les formations existantes
        displayAllFormations(formationService, evaluationService);

        // Chemin de l'image de test (à adapter)
        String imagePath = "chemin/vers/votre/image.jpg";
        File imageFile = new File(imagePath);

        if (!imageFile.exists()) {
            System.out.println("❌ Image non trouvée à : " + imagePath);
            return;
        }

        // 2. Test CRUD pour les formations
        testFormationCRUD(formationService, imageFile);

        // 3. Test des évaluations
        testEvaluations(formationService, evaluationService);
    }

    /**
     * Affiche toutes les formations avec leurs évaluations
     */
    private static void displayAllFormations(FormationImpl formationService, EvaluationImpl evaluationService) {
        System.out.println("\n=== LISTE COMPLÈTE DES FORMATIONS ===");
        List<Formation> formations = formationService.showAll();

        if (formations.isEmpty()) {
            System.out.println("Aucune formation trouvée.");
            return;
        }

        for (Formation formation : formations) {
            displayFormationDetails(formation, evaluationService);
            System.out.println("--------------------------------------------");
        }
    }

    /**
     * Affiche les détails d'une formation
     */
    private static void displayFormationDetails(Formation formation, EvaluationImpl evaluationService) {
        System.out.println("\nID: " + formation.getId());
        System.out.println("Titre: " + formation.getTitre());
        System.out.println("Description: " + formation.getDescription());
        System.out.println("Date Début: " + formation.getDatedebut());
        System.out.println("Date Fin: " + formation.getDatefin());
        System.out.println("Places: " + formation.getNbrplaces());
        System.out.println("Lien: " + formation.getLink());
        System.out.println("Image: " + formation.getImage_name());
        System.out.println("Taille image: " + formation.getImage_size() + " octets");
        System.out.println("Mis à jour: " + formation.getUpdated_at());

        // Affichage des évaluations
        List<Evaluation> evaluations = evaluationService.showByFormation(formation.getId());
        if (evaluations.isEmpty()) {
            System.out.println("Aucune évaluation pour cette formation.");
        } else {
            System.out.println("\nÉvaluations (" + evaluations.size() + "):");
            for (Evaluation eval : evaluations) {
                System.out.println(" - " + eval.getCommentaire() + " (" + eval.getNote() + "/5)");
            }
        }
    }

    /**
     * Test complet des opérations CRUD sur les formations
     */
    private static void testFormationCRUD(FormationImpl formationService, File imageFile) {
        System.out.println("\n=== TEST CRUD FORMATIONS ===");

        // Création d'une nouvelle formation
        Formation newFormation = new Formation();
        newFormation.setTitre("Atelier d'art plastique");
        newFormation.setDescription("Formation avancée sur le recyclage créatif");
        newFormation.setDatedebut(LocalDate.now());
        newFormation.setDatefin(LocalDate.now().plus(1, ChronoUnit.WEEKS));
        newFormation.setNbrplaces(15);
        newFormation.setLink("https://exemple.com/art-plastique");
        newFormation.setImage_name(imageFile.getName());
        newFormation.setImage_size((int) imageFile.length());
        newFormation.setUpdated_at(LocalDate.now());

        // Insertion
        formationService.create(newFormation);
        System.out.println("✅ Formation créée avec succès");

        // Récupération après création
        List<Formation> formationsAfterCreate = formationService.showAll();
        Formation createdFormation = formationsAfterCreate.get(formationsAfterCreate.size() - 1);
        System.out.println("\nDétails de la formation créée:");
        displayFormationDetails(createdFormation, new EvaluationImpl());

        // Modification
        createdFormation.setTitre("Atelier d'art plastique - Niveau avancé");
        createdFormation.setDescription("Techniques innovantes de recyclage artistique");
        createdFormation.setNbrplaces(20);
        formationService.update(createdFormation);
        System.out.println("\n✅ Formation mise à jour");

        // Suppression
        formationService.delete(createdFormation.getId());
        System.out.println("\n✅ Formation supprimée (ID: " + createdFormation.getId() + ")");
    }

    /**
     * Test des opérations sur les évaluations
     */
    private static void testEvaluations(FormationImpl formationService, EvaluationImpl evaluationService) {
        System.out.println("\n=== TEST ÉVALUATIONS ===");

        // Trouver une formation existante pour y ajouter une évaluation
        List<Formation> formations = formationService.showAll();
        if (formations.isEmpty()) {
            System.out.println("Aucune formation disponible pour tester les évaluations");
            return;
        }

        Formation targetFormation = formations.get(0);
        System.out.println("Formation sélectionnée pour le test: " + targetFormation.getTitre());

        // Création d'une nouvelle évaluation
        Evaluation newEvaluation = new Evaluation();
        newEvaluation.setCommentaire("Excellente formation, contenu très enrichissant!");
        newEvaluation.setNote(5);
        newEvaluation.setFormation(targetFormation);

        evaluationService.create(newEvaluation);
        System.out.println("✅ Évaluation ajoutée avec succès");

        // Affichage des évaluations après ajout
        System.out.println("\nDétails de la formation avec les nouvelles évaluations:");
        displayFormationDetails(targetFormation, evaluationService);
    }
}
