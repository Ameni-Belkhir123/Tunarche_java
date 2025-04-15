package TunArche.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private final String url = "jdbc:mysql://localhost:3308/tunarche";
    private final String login = "root";
    private final String pwd = "";
    private Connection cnx;
    private static MyConnection instance;

    public static MyConnection getInstance() {
        if (instance == null) {
            synchronized (MyConnection.class) {
                if (instance == null) {
                    instance = new MyConnection();
                }
            }
        }
        return instance;
    }

    private MyConnection() {
        try {
            System.out.println("🔄 Tentative de connexion à la base de données...");
            cnx = DriverManager.getConnection(url, login, pwd);
            System.out.println("✅ Connexion établie avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Échec de la connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = DriverManager.getConnection(url, login, pwd);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur de vérification de connexion : " + e.getMessage());
        }
        return cnx;
    }
}