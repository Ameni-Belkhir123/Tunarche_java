package TunArche.services;

import TunArche.entities.Billet;
import TunArche.entities.Event;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarService {

    private final EventImpl eventService = new EventImpl();
    private final BilletImpl billetService = new BilletImpl();

    public List<Event> getUserEvents(int userId) {
        List<Billet> billets = billetService.getAll();
        List<Event> userEvents = new ArrayList<>();

        // Log the billets fetched
        System.out.println("Fetched billets for user " + userId + ": " + billets.size() + " billets");
        for (Billet billet : billets) {
            System.out.println("Billet: event_id=" + billet.getEventId() + ", buyer_id=" + billet.getBuyerId());
            if (billet.getBuyerId() == userId) {
                Event event = eventService.getById(billet.getEventId());
                if (event != null) {
                    System.out.println("Adding event for billet: " + event.getNameEvent() + " on " + event.getDateStart());
                    userEvents.add(event);
                }
            }
        }

        return userEvents;
    }

    public List<Event> getUserEventsOnDate(int userId, LocalDate date) {
        return getUserEvents(userId).stream()
                .filter(event -> event.getDateStart() != null && event.getDateStart().equals(date))
                .collect(Collectors.toList());
    }
}