package model.payment;

import java.time.LocalDateTime;

public class Payment implements PaymentMethod {
    private String paymentId;
    private String trackingId;
    private double amount;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String receiptNumber;

    public Payment(String paymentId, String trackingId, double amount,
                   PaymentStatus status, LocalDateTime paymentDate) {
        this.paymentId = paymentId;
        this.trackingId = trackingId;
        this.amount = amount;
        this.status = status;
        this.paymentDate = paymentDate;
        this.receiptNumber = "RCPT-" + paymentId;
    }

    public String getPaymentId() { return paymentId; }
    public String getTrackingId() { return trackingId; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    @Override
    public String getMethodName() {
        return "PAYMENT";
    }

    @Override
    public boolean processPayment(double amount) {
        if (amount <= 0) {
            return false;
        }
        this.amount = amount;
        this.status = PaymentStatus.PAID;
        this.paymentDate = LocalDateTime.now();
        return true;
    }
}
