package model.payment;

import java.time.LocalDateTime;

public class CODPayment extends Payment {
    public CODPayment(String paymentId, String trackingId, double amount) {
        super(paymentId, trackingId, amount, PaymentStatus.PENDING, LocalDateTime.now());
    }

    @Override
    public String getMethodName() {
        return "CASH_ON_DELIVERY";
    }
}
