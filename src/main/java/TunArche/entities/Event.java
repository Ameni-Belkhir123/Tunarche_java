package TunArche.entities;

import java.time.LocalDate;

public class Event {
    private int id;
    private String nameEvent;
    private LocalDate dateStart; // Now using LocalDate
    private LocalDate dateEnd;   // Now using LocalDate
    private String placeEvent;
    private String description;
    private int price;
    private int totalTickets;
    private int soldTickets;

    // Constructor
    public Event() {}

    public Event(int id, String nameEvent, LocalDate dateStart, LocalDate dateEnd, String placeEvent, String description, int price, int totalTickets, int soldTickets) {
        this.id = id;
        this.nameEvent = nameEvent;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.placeEvent = placeEvent;
        this.description = description;
        this.price = price;
        this.totalTickets = totalTickets;
        this.soldTickets = soldTickets;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNameEvent() { return nameEvent; }
    public void setNameEvent(String nameEvent) { this.nameEvent = nameEvent; }
    public LocalDate getDateStart() { return dateStart; }
    public void setDateStart(LocalDate dateStart) { this.dateStart = dateStart; }
    public LocalDate getDateEnd() { return dateEnd; }
    public void setDateEnd(LocalDate dateEnd) { this.dateEnd = dateEnd; }
    public String getPlaceEvent() { return placeEvent; }
    public void setPlaceEvent(String placeEvent) { this.placeEvent = placeEvent; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public int getTotalTickets() { return totalTickets; }
    public void setTotalTickets(int totalTickets) { this.totalTickets = totalTickets; }
    public int getSoldTickets() { return soldTickets; }
    public void setSoldTickets(int soldTickets) { this.soldTickets = soldTickets; }
}