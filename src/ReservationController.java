import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReservationController {
    private final FileDataStore dataStore;
    private final List<FlightSchedule> flights;
    private final List<Reservation> reservations;
    private final List<Payment> payments;

    public ReservationController(Path dataDirectory) throws IOException {
        this.dataStore = new FileDataStore(dataDirectory);
        this.dataStore.initialize();
        this.flights = new ArrayList<>(dataStore.loadFlights());
        this.payments = new ArrayList<>(dataStore.loadPayments());
        this.reservations = new ArrayList<>(dataStore.loadReservations(flights, payments));
    }

    public List<FlightSchedule> searchFlights(String origin, String destination, LocalDate departureDate) {
        String originNorm = normalize(origin);
        String destinationNorm = normalize(destination);
        return flights.stream()
                .filter(f -> originNorm.isBlank() || normalize(f.getOrigin()).contains(originNorm))
                .filter(f -> destinationNorm.isBlank() || normalize(f.getDestination()).contains(destinationNorm))
                .filter(f -> departureDate == null || f.getDeparture().toLocalDate().equals(departureDate))
                .sorted(Comparator.comparing(FlightSchedule::getDeparture))
                .collect(Collectors.toList());
    }

    public Reservation bookReservation(String flightId, String passengerName, String passengerEmail, String ticketType)
            throws IOException {
        FlightSchedule flight = findFlight(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight ID not found."));

        Passenger passenger = new Passenger(passengerName.trim(), passengerEmail.trim());
        Ticket ticket = createTicket(ticketType, generateId("TKT"));
        Reservation reservation = new Reservation(
                generateId("RES"),
                passenger,
                flight,
                ticket,
                LocalDateTime.now(),
                ReservationStatus.BOOKED,
                null,
                BigDecimal.ZERO
        );
        reservations.add(reservation);
        persist();
        return reservation;
    }

    public Reservation confirmReservation(String reservationId, BigDecimal amount) throws IOException {
        Reservation reservation = findReservation(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation ID not found."));
        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new IllegalStateException("Canceled reservations cannot be confirmed.");
        }
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Reservation is already confirmed.");
        }
        if (amount.compareTo(reservation.getFare()) != 0) {
            throw new IllegalArgumentException("Payment amount must equal fare: $" + reservation.getFare().toPlainString());
        }

        Payment payment = new Payment(generateId("PAY"), reservationId, amount, LocalDateTime.now());
        payments.add(payment);
        reservation.confirm(payment);
        persist();
        return reservation;
    }

    public String cancelReservation(String reservationId) throws IOException {
        Reservation reservation = findReservation(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation ID not found."));
        BigDecimal refund = reservation.cancel(LocalDateTime.now());
        persist();
        if (refund.compareTo(BigDecimal.ZERO) > 0) {
            return "Reservation canceled. Refund amount: $" + refund.toPlainString();
        }
        return "Reservation canceled. No refund is available under the ticket rules.";
    }

    public List<Reservation> getReservations() {
        return new ArrayList<>(reservations);
    }

    public Optional<Reservation> findReservation(String reservationId) {
        return reservations.stream()
                .filter(r -> r.getReservationId().equalsIgnoreCase(reservationId.trim()))
                .findFirst();
    }

    private Optional<FlightSchedule> findFlight(String flightId) {
        return flights.stream()
                .filter(f -> f.getId().equalsIgnoreCase(flightId.trim()))
                .findFirst();
    }

    private Ticket createTicket(String ticketType, String ticketNumber) {
        String normalized = normalize(ticketType);
        if (normalized.contains("first")) {
            return new FirstClassTicket(ticketNumber);
        }
        if (normalized.contains("coach")) {
            return new CoachTicket(ticketNumber);
        }
        return new EconomyTicket(ticketNumber);
    }

    private String generateId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private void persist() throws IOException {
        dataStore.savePayments(payments);
        dataStore.saveReservations(reservations);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
