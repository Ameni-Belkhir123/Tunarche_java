package TunArche.entities;
import TunArche.tools.MyConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class user {

    private Integer id;

    private String name;

    private String lastName;

    private String email;

    private String password;

    private String role;

    private boolean isVerified;

    private String verificationToken;

    private Date codeSentAt;

    private String phone;
    private Connection connection;

    public user(){
        this.connection = MyConnection.getInstance().getCnx(); // ou autre méthode

    };

    // Constructors
    public user(String name, String lastName, String email, String password, String phone) {}

    @Override
    public String toString() {
        return "user{" +
                ", lastName='" + lastName + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", isVerified=" + isVerified +
                '}';
    }
    public static user login(String email, String password) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try {
            Connection connection = MyConnection.getInstance().getCnx(); // récupérer la connexion
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                if (BCrypt.checkpw(password, hashedPassword)) {
                    user user = new user();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setRole(rs.getString("role"));
                    user.setVerified(rs.getBoolean("is_verified"));
                    // autres champs si besoin

                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public user(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter pour l'email

    public user(Integer id, String name, String lastName, String email,String phone, String password, String role, boolean isVerified, String verificationToken, Date codeSentAt) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.isVerified = isVerified;
        this.verificationToken = verificationToken;
        this.codeSentAt = codeSentAt;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public Date getCodeSentAt() {
        return codeSentAt;
    }

    public void setCodeSentAt(Date codeSentAt) {
        this.codeSentAt = codeSentAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
