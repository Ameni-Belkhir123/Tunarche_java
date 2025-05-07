package TunArche.test;

import TunArche.entities.user;
import TunArche.services.userimpl;
import TunArche.tools.MyConnection;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class mainUserTest {
    public static void main(String[] args) {
        MyConnection m1 = MyConnection.getInstance(); // Connexion DB&

        userimpl userService = new userimpl();

        System.out.println("📋 Liste des utilisateurs existants :");
        List<user> users = userService.showAll();
        for (user u : users) {
            System.out.println(u);
        }

        // 📅 Génération d’une date
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -10); // Exemple : code envoyé il y a 10 minutes
        Date codeSentAt = cal.getTime();

        // ➕ Création d’un nouvel utilisateur
        user newUser = new user();
        newUser.setName("marwen");
        newUser.setLastName("azouzi");
        newUser.setEmail("marwen.azouzi@esprit.tn");
        newUser.setPassword("123");
        newUser.setRole("user");
        newUser.setVerified(false);
        newUser.setVerificationToken("abc123token");
        newUser.setCodeSentAt(codeSentAt);

        // ✅ Ajout à la base
        userService.create(newUser);

        System.out.println("\n📋 Liste des utilisateurs après ajout :");
        List<user> updatedUsers = userService.showAll();
        for (user u : updatedUsers) {
            System.out.println(u);
        }

        // 📝 Modifier un utilisateur existant (change l'ID selon ta DB)
//        user userToUpdate = userService.findById(2); // Dernier utilisateur ajouté
//        userToUpdate.setName("marwen Modifiée");
//        userToUpdate.setVerified(true);
//        userToUpdate.setEmail("marwen1.azouzi@esprit.tn");
//        userToUpdate.setPassword("123");
//        userToUpdate.setRole("user");
//        userService.update(userToUpdate);
//
//        System.out.println("\n🔄 Utilisateur après modification :");
//        user updatedUser = userService.findById(userToUpdate.getId());
//        if (updatedUser != null) {
//            System.out.println(updatedUser);
//        } else {
//            System.out.println("❌ Utilisateur non trouvé.");
//        }
//
//        // 🗑️ Supprimer l'utilisateur
//        userService.delete(2);
//        System.out.println("\n🧹 Liste des utilisateurs après suppression :");
//        List<user> finalUsers = userService.showAll();
//        for (user u : finalUsers) {
//            System.out.println(u);
//        }
    }
}
