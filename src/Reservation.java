import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reservation {
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String reservationId;
    private final Passenger passenger;
    private final FlightSchedule flight;
    private final Ticket ticket;
    private final LocalDateTime createdAt;
    private ReservationStatus status;
    private Payment payment;
    private BigDecimal refundAmount;

    public Reservation(String reservationId, Passenger passenger, FlightSchedule flight, Ticket ticket,
                       LocalDateTime createdAt, ReservationStatus status, Payment payment,
                       BigDecimal refundAmount) {
        this.reservationId = reservationId;
        this.passenger = passenger;
        this.flight = flight;
        this.ticket = ticket;
        this.createdAt = createdAt;
        this.status = status;
        this.payment = payment;
        this.refundAmount = refundAmount == null ? BigDecimal.ZERO : refundAmount;
    }

    public String getReservationId() { return reservationId; }
    public Passenger getPassenger() { return passenger; }
    public FlightSchedule getFlight() { return flight; }
    public Ticket getTicket() { return ticket; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public ReservationStatus getStatus() { return status; }
    public Payment getPayment() { return payment; }
    public BigDecimal getRefundAmount() { return refundAmount; }

    public BigDecimal getFare() {
        return ticket.calculateFare(flight.getBaseFare());
    }

    public void confirm(Payment payment) {
        this.payment = payment;
        this.status = ReservationStatus.CONFIRMED;
    }

    public BigDecimal cancel(LocalDateTime cancelTime) {
        if (status == ReservationStatus.CANCELED) {
            return refundAmount;
        }
        BigDecimal refund = BigDecimal.ZERO;
        if (payment != null && ticket.isRefundable(cancelTime, flight.getDeparture())) {
            refund = getFare();
        }
        this.refundAmount = refund;
        this.status = ReservationStatus.CANCELED;
        return refund;
    }

    public String toDisplayString() {
        return String.format(
                "Reservation %s | %s | %s | %s | Fare: $%s | Status: %s | Departure: %s",
                reservationId,
                passenger.getName(),
                flight.getFlightNumber(),
                ticket.getTypeName(),
                getFare().toPlainString(),
                status,
                flight.getDeparture().format(DISPLAY_FMT)
        );
    }
}
