import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CoachTicket extends Ticket {
    public CoachTicket(String ticketNumber) {
        super(ticketNumber, "Coach");
    }

    @Override
    public BigDecimal getFareMultiplier() {
        return new BigDecimal("0.85");
    }

    @Override
    public boolean isRefundable(LocalDateTime cancelTime, LocalDateTime departureTime) {
        return true;
    }
}
