package TunArche.services;

import TunArche.entities.user;
import TunArche.interfaces.Iuser;
import TunArche.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class userimpl implements Iuser<user> {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tunarche";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @Override
    public void create(user u) {
        try {
            String hashedPassword = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt());

            String req = "INSERT INTO user (name, last_name, email, phone, password,  role, is_verified, verification_token, code_sent_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(req);

            st.setString(1, u.getName());
            st.setString(2, u.getLastName());
            st.setString(3, u.getEmail());
            st.setString(4, u.getPhone());
            st.setString(5, hashedPassword);
            st.setString(6, u.getRole());
            st.setBoolean(7, u.isVerified());
            st.setString(8, u.getVerificationToken());

            if (u.getCodeSentAt() != null) {
                st.setTimestamp(9, new java.sql.Timestamp(u.getCodeSentAt().getTime()));
            } else {
                st.setTimestamp(9, null);
            }

            st.executeUpdate();
            System.out.println("✅ Utilisateur ajouté avec mot de passe haché !");
        } catch (Exception e) {
            System.out.println("Erreur create : " + e.getMessage());
        }
    }
    public boolean isEmailExists(String email) {
        // Perform a query to check if the email exists in the database
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void update(user u) {
        try {
            String hashedPassword = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt());

            String req = "UPDATE user SET name=?, last_name=?, email=?, phone=?, password=?,  role=?, is_verified=?, verification_token=?, code_sent_at=? WHERE id=?";
            PreparedStatement st = MyConnection.getInstance().getCnx().prepareStatement(req);

            st.setString(1, u.getName());
            st.setString(2, u.getLastName());
            st.setString(3, u.getEmail());
            st.setString(4, u.getPhone());
            st.setString(5, hashedPassword);
            st.setString(6, u.getRole());
            st.setBoolean(7, u.isVerified());
            st.setString(8, u.getVerificationToken());

            if (u.getCodeSentAt() != null) {
                st.setTimestamp(9, new java.sql.Timestamp(u.getCodeSentAt().getTime()));
            } else {
                st.setTimestamp(9, null);
            }

            st.setInt(10, u.getId());
            st.executeUpdate();
            System.out.println("✅ Utilisateur modifié !");
        } catch (Exception e) {
            System.out.println("Erreur update : " + e.getMessage());
        }
    }
    public boolean updatePasswordByEmail(String email, String hashedPassword) {
        String sql = "UPDATE user SET password = ? WHERE email = ?";
        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
                        rs.getString("phone"),
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
                        rs.getString("phone"),
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
