import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private final String paymentId;
    private final String reservationId;
    private final BigDecimal amount;
    private final LocalDateTime paidAt;

    public Payment(String paymentId, String reservationId, BigDecimal amount, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    public String getPaymentId() { return paymentId; }
    public String getReservationId() { return reservationId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getPaidAt() { return paidAt; }
}
