package TunArche.test;

import TunArche.entities.Billet;
import TunArche.entities.Event;
import TunArche.services.BilletImpl;
import TunArche.services.EventImpl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class MainCrudTest {
    public static void main(String[] args) {

        // Test de la classe Event
        EventImpl eventService = new EventImpl();

        // ✅ Create
        LocalDate startDate = LocalDate.of(2025, 4, 12); // Example date for new event
        LocalDate endDate = LocalDate.of(2025, 4, 14);
        Event newEvent = new Event(0, "Tech Expo", startDate, endDate, "Tunis", "Salon de la technologie", 50, 100, 0);
        eventService.create(newEvent);

        // 📋 Read All
        List<Event> events = eventService.getAll();
        System.out.println("📋 Liste des événements :");
        for (Event e : events) {
            System.out.println(e);
        }

        // ✏️ Update
        if (!events.isEmpty()) {
            Event firstEvent = eventService.getevent(14);
            if (firstEvent != null) {
                firstEvent.setPrice(75);
                firstEvent.setSoldTickets(10);
                firstEvent.setPlaceEvent("arina");
                firstEvent.setDateStart(LocalDate.of(2025, 6, 14));
                firstEvent.setDateEnd(LocalDate.of(2025, 7, 14));
                eventService.update(firstEvent);
                System.out.println("✏️ Événement mis à jour : " + firstEvent);
            } else {
                System.out.println("Événement avec ID 14 non trouvé.");
            }
        }

        // ❌ Delete
        if (!events.isEmpty()) {
            int id = 10;
            eventService.delete(id);
            System.out.println("🗑️ Événement supprimé avec ID : " + id);
        }

        // -----------------------------------------------------

        // Test de la classe Billet
        BilletImpl billetService = new BilletImpl();

        // ✅ Create
        LocalDateTime emissionDateTime = LocalDateTime.of(2025, 4, 12, 10, 0); // Example date and time
        Timestamp emissionTimestamp = Timestamp.valueOf(emissionDateTime);
        Billet billet = new Billet(0, 14, 1, "TUN123", emissionTimestamp, "Carte bancaire", "VIP");
        billetService.create(billet);

        // 📋 Read All
        List<Billet> billets = billetService.getAll();
        System.out.println("📋 Liste des billets :");
        for (Billet b : billets) {
            System.out.println(b);
        }

        // ✏️ Update
        if (!billets.isEmpty()) {
            Billet firstBillet = billetService.getById(25);
            if (firstBillet != null) {
                firstBillet.setModePaiement("Espèces");
                LocalDateTime updatedEmissionDateTime = LocalDateTime.of(2025, 7, 14, 10, 0);
                firstBillet.setDateEmission(Timestamp.valueOf(updatedEmissionDateTime));
                firstBillet.setNumero("9dd9");
                firstBillet.setType("normal");
                billetService.update(firstBillet);
                System.out.println("✏️ Billet mis à jour : " + firstBillet);
            } else {
                System.out.println("Billet avec ID 25 non trouvé.");
            }
        }

        // ❌ Delete
        if (!billets.isEmpty()) {
            int id = 25;
            billetService.delete(id);
            System.out.println("🗑️ Billet supprimé avec ID : " + id);
        }
    }
}