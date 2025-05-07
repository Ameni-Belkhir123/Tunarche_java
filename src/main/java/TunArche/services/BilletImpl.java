package TunArche.services;

import TunArche.entities.Billet;
import TunArche.interfaces.IBillet;
import TunArche.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BilletImpl implements IBillet<Billet> {

    private Connection cnx = MyConnection.getInstance().getCnx();

    @Override
    public void create(Billet b) {
        String req = "INSERT INTO billet (event_id, buyer_id, numero, date_emission, mode_paiement, type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, b.getEventId());
            ps.setInt(2, b.getBuyerId());
            ps.setString(3, b.getNumero());
            ps.setTimestamp(4, new Timestamp(b.getDateEmission().getTime())); // ✅ Timestamp here
            ps.setString(5, b.getModePaiement());
            ps.setString(6, b.getType());
            ps.executeUpdate();
            System.out.println("✅ Billet ajouté !");
        } catch (SQLException ex) {
            System.out.println("Erreur ajout billet : " + ex.getMessage());
        }
    }

    @Override
    public void update(Billet b) {
        String req = "UPDATE billet SET event_id=?, buyer_id=?, numero=?, date_emission=?, mode_paiement=?, type=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, b.getEventId());
            ps.setInt(2, b.getBuyerId());
            ps.setString(3, b.getNumero());
            ps.setTimestamp(4, new Timestamp(b.getDateEmission().getTime())); // ✅ Timestamp here
            ps.setString(5, b.getModePaiement());
            ps.setString(6, b.getType());
            ps.setInt(7, b.getId());
            ps.executeUpdate();
            System.out.println("✅ Billet mis à jour !");
        } catch (SQLException ex) {
            System.out.println("Erreur mise à jour billet : " + ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String req = "DELETE FROM billet WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("🗑️ Billet supprimé !");
        } catch (SQLException ex) {
            System.out.println("Erreur suppression billet : " + ex.getMessage());
        }
    }

    @Override
    public Billet getById(int id) {
        String req = "SELECT * FROM billet WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Timestamp dateEmission = rs.getTimestamp("date_emission");
                return new Billet(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getInt("buyer_id"),
                        rs.getString("numero"),
                        dateEmission,
                        rs.getString("mode_paiement"),
                        rs.getString("type")
                );
            }
        } catch (SQLException ex) {
            System.out.println("Erreur récupération billet : " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Billet> getAll() {
        List<Billet> list = new ArrayList<>();
        String req = "SELECT * FROM billet";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Timestamp dateEmission = rs.getTimestamp("date_emission");
                Billet b = new Billet(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getInt("buyer_id"),
                        rs.getString("numero"),
                        dateEmission,
                        rs.getString("mode_paiement"),
                        rs.getString("type")
                );
                list.add(b);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur récupération billets : " + ex.getMessage());
        }
        return list;
    }
}
