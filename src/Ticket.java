import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public abstract class Ticket {
    private final String ticketNumber;
    private final String typeName;

    protected Ticket(String ticketNumber, String typeName) {
        this.ticketNumber = ticketNumber;
        this.typeName = typeName;
    }

    public String getTicketNumber() { return ticketNumber; }
    public String getTypeName() { return typeName; }

    public abstract BigDecimal getFareMultiplier();

    public BigDecimal calculateFare(BigDecimal baseFare) {
        return baseFare.multiply(getFareMultiplier()).setScale(2, RoundingMode.HALF_UP);
    }

    public abstract boolean isRefundable(LocalDateTime cancelTime, LocalDateTime departureTime);
}
