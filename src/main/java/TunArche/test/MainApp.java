package TunArche.test;

import java.util.List;
import TunArche.entities.Commantaire;
import TunArche.entities.Publication;
import TunArche.services.CommantaireImpl;
import TunArche.services.PublicationImpl;
import TunArche.tools.MyConnection;

public class MainApp {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("🔌 TEST DE CONNEXION À LA BASE DE DONNÉES");
        System.out.println("===========================================");
        MyConnection m1 = MyConnection.getInstance();
        if (m1 != null) {
            System.out.println("✅ Connexion à la base de données réussie !");
        } else {
            System.out.println("❌ Échec de la connexion à la base de données.");
            return;
        }

        PublicationImpl publicationService = new PublicationImpl();
        CommantaireImpl commantaireService = new CommantaireImpl();

        System.out.println("\n===========================================");
        System.out.println("📘 TEST CRUD SUR PUBLICATION");
        System.out.println("===========================================");

        // Créer une publication
        Publication pub1 = newPublication("Titre Publication Test", "Description test", "imageTest.jpg", 10, 2, 5);
        publicationService.create(pub1);
        System.out.println("➕ Publication ajoutée : " + pub1.getTitre());

        // Lire une publication
        Publication pub2 = publicationService.findById(3);
        if (pub2 != null) {
            System.out.println("🔍 Publication trouvée : " + pub2.getTitre());
        } else {
            System.out.println("⚠️ Publication avec ID 3 non trouvée.");
        }

        // Mettre à jour une publication
        if (pub2 != null) {
            pub2.setTitre("Titre mis à jour");
            publicationService.update(pub2);
            System.out.println("🔄 Publication mise à jour : " + pub2.getTitre());
        }

        // Supprimer la publication créée au début
        publicationService.delete(pub1.getId());
        System.out.println("🗑️ Publication supprimée avec succès.");

        // Afficher toutes les publications
        System.out.println("\n📋 Liste des publications après mise à jour :");
        List<Publication> allPublications = publicationService.showAll();
        if (allPublications != null && !allPublications.isEmpty()) {
            for (Publication pub : allPublications) {
                System.out.println("📝 " + pub.getTitre() + " - " + pub.getDescription());
            }
        } else {
            System.out.println("❌ Aucune publication trouvée.");
        }

        System.out.println("\n===========================================");
        System.out.println("💬 TEST CRUD SUR COMMENTAIRE");
        System.out.println("===========================================");

        // Créer un commentaire
        Commantaire comm1 = newCommantaire("Contenu du commentaire pour publication 1", 1);
        commantaireService.create(comm1);
        System.out.println("➕ Commentaire ajouté !");

        // Lire un commentaire
        Commantaire comm2 = commantaireService.findById(2);
        if (comm2 != null) {
            System.out.println("🔍 Commentaire trouvé : " + comm2.getContenu());
        } else {
            System.out.println("⚠️ Commentaire avec ID 2 non trouvé.");
        }

        // Mettre à jour un commentaire
        if (comm2 != null) {
            comm2.setContenu("Contenu du commentaire mis à jour");
            commantaireService.update(comm2);
            System.out.println("🔄 Commentaire mis à jour : " + comm2.getContenu());
        }

        // Supprimer le commentaire créé
        commantaireService.delete(comm1.getId());
        System.out.println("🗑️ Commentaire supprimé avec succès.");

        // Afficher tous les commentaires pour une publication
        System.out.println("\n📋 Liste des commentaires pour la publication 1 :");
        List<Commantaire> allCommantaires = commantaireService.showByPublication(1);
        if (allCommantaires != null && !allCommantaires.isEmpty()) {
            for (Commantaire comm : allCommantaires) {
                System.out.println("💭 " + comm.getContenu());
            }
        } else {
            System.out.println("❌ Aucun commentaire trouvé pour la publication 1.");
        }

        System.out.println("\n✅ Fin des tests !");
    }

    private static Publication newPublication(String titre, String description, String image, int likes, int rating, int par2) {
        Publication pub = new Publication();
        pub.setTitre(titre);
        pub.setDescription(description);
        pub.setImage(image);
        pub.setLikes(likes);
        pub.setRating(rating);
        return pub;
    }

    private static Commantaire newCommantaire(String contenu, int id_pub_id) {
        PublicationImpl publicationService = new PublicationImpl();
        Publication publication = publicationService.findById(id_pub_id);
        if (publication == null) {
            throw new IllegalArgumentException("❌ La publication avec ID " + id_pub_id + " n'existe pas.");
        }

        Commantaire comm = new Commantaire();
        comm.setContenu(contenu);
        comm.setPublicationId(id_pub_id);
        return comm;
    }
}
