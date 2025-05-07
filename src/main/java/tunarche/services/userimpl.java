package TunArche.services;

import TunArche.entities.user;
import TunArche.interfaces.Iuser;
import TunArche.tools.MyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class userimpl implements Iuser<user> {

    @Override
    public void create(user u) {
        try {
            // Hachage du mot de passe
            String hashedPassword = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt());

            // Requête SQL pour insérer un utilisateur
            String req = "INSERT INTO user (name, last_name, email, password, role, is_verified, verification_token, code_sent_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(req);

            // Paramétrer les autres valeurs
            st.setString(1, u.getName());
            st.setString(2, u.getLastName());
            st.setString(3, u.getEmail());

            // Utiliser le mot de passe haché
            st.setString(4, hashedPassword);

            st.setString(5, u.getRole());
            st.setBoolean(6, u.isVerified());
            st.setString(7, u.getVerificationToken());

            // Gérer la date du code envoyé
            if (u.getCodeSentAt() != null) {
                st.setTimestamp(8, new java.sql.Timestamp(u.getCodeSentAt().getTime()));
            } else {
                st.setTimestamp(8, null);
            }

            // Exécuter la requête
            st.executeUpdate();
            System.out.println("✅ Utilisateur ajouté avec mot de passe haché !");

        } catch (Exception e) {
            System.out.println("Erreur create : " + e.getMessage());
        }
    }

    @Override
    public void update(user u) {
        try {
            String hashedPassword = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt()); // Hachage ici 👈

            String req = "UPDATE user SET name=?, last_name=?, email=?, password=?, role=?, is_verified=?, verification_token=?, code_sent_at=? WHERE id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(req);
            st.setString(1, u.getName());
            st.setString(2, u.getLastName());
            st.setString(3, u.getEmail());
            st.setString(4, hashedPassword); // On enregistre le mot de passe haché 👈
            st.setString(5, u.getRole());
            st.setBoolean(6, u.isVerified());
            st.setString(7, u.getVerificationToken());

            if (u.getCodeSentAt() != null) {
                st.setTimestamp(8, new java.sql.Timestamp(u.getCodeSentAt().getTime()));
            } else {
                st.setTimestamp(8, null);
            }

            st.setInt(9, u.getId());
            st.executeUpdate();
            System.out.println("✅ Utilisateur modifié !");
        } catch (Exception e) {
            System.out.println("Erreur update : " + e.getMessage());
        }
    }
    @Override
    public void delete(int id) {
        try {
            String req = "DELETE FROM user WHERE id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(req);
            st.setInt(1, id);
            st.executeUpdate();
            System.out.println("🗑️ Utilisateur supprimé !");
        } catch (Exception e) {
            System.out.println("Erreur delete : " + e.getMessage());
        }
    }

    @Override
    public user findById(int id) {
        try {
            String req = "SELECT * FROM user WHERE id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(req);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new user(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("is_verified"),
                        rs.getString("verification_token"),
                        rs.getTimestamp("code_sent_at")
                );
            }
        } catch (Exception e) {
            System.out.println("Erreur findById : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<user> showAll() {
        List<user> users = new ArrayList<>();
        try {
            String req = "SELECT * FROM user";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(req);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                user u = new user(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("is_verified"),
                        rs.getString("verification_token"),
                        rs.getTimestamp("code_sent_at")
                );
                users.add(u);
            }
        } catch (Exception e) {
            System.out.println("Erreur showAll : " + e.getMessage());
        }
        return users;
    }
}
