package TunArche.services;

import TunArche.entities.Event;
import TunArche.interfaces.IEvent;
import TunArche.tools.MyConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventImpl implements IEvent<Event> {

    private Connection cnx = MyConnection.getInstance().getCnx();

    @Override
    public void create(Event e) {
        String req = "INSERT INTO event (name_event, date_start, date_end, place_event, discription, price, total_tickets, sold_tickets) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, e.getNameEvent());
            ps.setDate(2, e.getDateStart() != null ? java.sql.Date.valueOf(e.getDateStart()) : null);
            ps.setDate(3, e.getDateEnd() != null ? java.sql.Date.valueOf(e.getDateEnd()) : null);
            ps.setString(4, e.getPlaceEvent());
            ps.setString(5, e.getDescription());
            ps.setInt(6, e.getPrice());
            ps.setInt(7, e.getTotalTickets());
            ps.setInt(8, e.getSoldTickets());
            ps.executeUpdate();
            System.out.println("✅ Événement ajouté !");
        } catch (SQLException ex) {
            System.out.println("Erreur création événement : " + ex.getMessage());
        }
    }

    public void updateSoldTickets(int eventId, int soldTickets) {
        String sql = "UPDATE event SET sold_tickets = ? WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, soldTickets);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean eventNameExists(String name) {
        String req = "SELECT COUNT(*) FROM event WHERE LOWER(name_event) = LOWER(?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la vérification du nom d'événement : " + ex.getMessage());
        }
        return false;
    }

    @Override
    public void update(Event e) {
        String req = "UPDATE event SET name_event=?, date_start=?, date_end=?, place_event=?, discription=?, price=?, total_tickets=?, sold_tickets=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, e.getNameEvent());
            ps.setDate(2, e.getDateStart() != null ? java.sql.Date.valueOf(e.getDateStart()) : null);
            ps.setDate(3, e.getDateEnd() != null ? java.sql.Date.valueOf(e.getDateEnd()) : null);
            ps.setString(4, e.getPlaceEvent());
            ps.setString(5, e.getDescription());
            ps.setInt(6, e.getPrice());
            ps.setInt(7, e.getTotalTickets());
            ps.setInt(8, e.getSoldTickets());
            ps.setInt(9, e.getId());
            ps.executeUpdate();
            System.out.println("✅ Événement mis à jour !");
        } catch (SQLException ex) {
            System.out.println("Erreur mise à jour événement : " + ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String req = "DELETE FROM event WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("🗑️ Événement supprimé !");
        } catch (SQLException ex) {
            System.out.println("Erreur suppression événement : " + ex.getMessage());
        }
    }

    @Override
    public Event getById(int id) {
        String req = "SELECT * FROM event WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                java.sql.Date sqlDateStart = rs.getDate("date_start");
                LocalDate dateStart = sqlDateStart != null ? sqlDateStart.toLocalDate() : null;
                java.sql.Date sqlDateEnd = rs.getDate("date_end");
                LocalDate dateEnd = sqlDateEnd != null ? sqlDateEnd.toLocalDate() : null;

                return new Event(
                        rs.getInt("id"),
                        rs.getString("name_event"),
                        dateStart,
                        dateEnd,
                        rs.getString("place_event"),
                        rs.getString("discription"),
                        rs.getInt("price"),
                        rs.getInt("total_tickets"),
                        rs.getInt("sold_tickets")
                );
            }
        } catch (SQLException ex) {
            System.out.println("Erreur récupération événement : " + ex.getMessage());
        }
        return null;
    }

    @Override
    public Event getevent(int id) {
        String req = "SELECT * FROM event WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                java.sql.Date sqlDateStart = rs.getDate("date_start");
                LocalDate dateStart = sqlDateStart != null ? sqlDateStart.toLocalDate() : null;
                java.sql.Date sqlDateEnd = rs.getDate("date_end");
                LocalDate dateEnd = sqlDateEnd != null ? sqlDateEnd.toLocalDate() : null;

                return new Event(
                        rs.getInt("id"),
                        rs.getString("name_event"),
                        dateStart,
                        dateEnd,
                        rs.getString("place_event"),
                        rs.getString("discription"),
                        rs.getInt("price"),
                        rs.getInt("total_tickets"),
                        rs.getInt("sold_tickets")
                );
            }
        } catch (SQLException ex) {
            System.out.println("Erreur récupération événement : " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Event> getAll() {
        List<Event> list = new ArrayList<>();
        String req = "SELECT * FROM event";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                java.sql.Date sqlDateStart = rs.getDate("date_start");
                LocalDate dateStart = sqlDateStart != null ? sqlDateStart.toLocalDate() : null;
                java.sql.Date sqlDateEnd = rs.getDate("date_end");
                LocalDate dateEnd = sqlDateEnd != null ? sqlDateEnd.toLocalDate() : null;

                Event e = new Event(
                        rs.getInt("id"),
                        rs.getString("name_event"),
                        dateStart,
                        dateEnd,
                        rs.getString("place_event"),
                        rs.getString("discription"),
                        rs.getInt("price"),
                        rs.getInt("total_tickets"),
                        rs.getInt("sold_tickets")
                );
                list.add(e);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur récupération liste des événements : " + ex.getMessage());
        }
        return list;
    }
}