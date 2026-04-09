import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FirstClassTicket extends Ticket {
    public FirstClassTicket(String ticketNumber) {
        super(ticketNumber, "First Class");
    }

    @Override
    public BigDecimal getFareMultiplier() {
        return new BigDecimal("1.00");
    }

    @Override
    public boolean isRefundable(LocalDateTime cancelTime, LocalDateTime departureTime) {
        return true;
    }
}
