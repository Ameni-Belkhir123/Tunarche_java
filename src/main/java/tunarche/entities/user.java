package TunArche.entities;
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

    // Constructors
    public user() {}

    @Override
    public String toString() {
        return "user{" +
                "isVerified=" + isVerified +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", lastName='" + lastName + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }

    public user(Integer id, String name, String lastName, String email, String password, String role, boolean isVerified, String verificationToken, Date codeSentAt) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
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
}
