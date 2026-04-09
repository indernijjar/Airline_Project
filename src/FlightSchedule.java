import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FlightSchedule {
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String id;
    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDateTime departure;
    private final LocalDateTime arrival;
    private final BigDecimal baseFare;
    private final int capacity;

    public FlightSchedule(String id, String flightNumber, String origin, String destination,
                          LocalDateTime departure, LocalDateTime arrival,
                          BigDecimal baseFare, int capacity) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departure = departure;
        this.arrival = arrival;
        this.baseFare = baseFare;
        this.capacity = capacity;
    }

    public String getId() { return id; }
    public String getFlightNumber() { return flightNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public LocalDateTime getDeparture() { return departure; }
    public LocalDateTime getArrival() { return arrival; }
    public BigDecimal getBaseFare() { return baseFare; }
    public int getCapacity() { return capacity; }

    public String toDisplayString() {
        return String.format("[%s] %s %s -> %s | Depart: %s | Arrive: %s | Base Fare: $%s | Capacity: %d",
                id, flightNumber, origin, destination,
                departure.format(DISPLAY_FMT), arrival.format(DISPLAY_FMT),
                baseFare.toPlainString(), capacity);
    }
}
