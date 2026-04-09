import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class EconomyTicket extends Ticket {
    public EconomyTicket(String ticketNumber) {
        super(ticketNumber, "Economy");
    }

    @Override
    public BigDecimal getFareMultiplier() {
        return new BigDecimal("0.70");
    }

    @Override
    public boolean isRefundable(LocalDateTime cancelTime, LocalDateTime departureTime) {
        return ChronoUnit.DAYS.between(cancelTime, departureTime) >= 14;
    }
}
