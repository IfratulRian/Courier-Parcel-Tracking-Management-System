package model.payment;

import java.time.LocalDateTime;

public class PrepaidPayment extends Payment {
    public PrepaidPayment(String paymentId, String trackingId, double amount) {
        super(paymentId, trackingId, amount, PaymentStatus.PENDING, LocalDateTime.now());
    }

    @Override
    public String getMethodName() {
        return "PREPAID";
    }
}
