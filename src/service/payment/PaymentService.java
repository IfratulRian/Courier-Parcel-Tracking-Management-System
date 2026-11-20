package service.payment;

import exception.InvalidPaymentException;
import exception.RecordNotFoundException;
import model.payment.CODPayment;
import model.payment.Payment;
import model.payment.PaymentStatus;
import model.payment.PrepaidPayment;
import model.parcel.Parcel;
import service.parcel.ParcelManager;
import utility.IDGenerator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {
    private final List<Payment> payments = new ArrayList<>();
    private final ParcelManager parcelManager;

    public PaymentService(ParcelManager parcelManager) {
        this.parcelManager = parcelManager;
    }

    public Payment createPayment(String trackingId, String method)
            throws RecordNotFoundException, InvalidPaymentException {
        Parcel parcel = parcelManager.findByTrackingId(trackingId);
        if (parcel == null) {
            throw new RecordNotFoundException("Parcel not found.");
        }
        for (Payment payment : payments) {
            if (payment.getTrackingId().equals(trackingId)) {
                throw new InvalidPaymentException("A payment already exists for this parcel.");
            }
        }
        String paymentId = IDGenerator.generatePaymentId();
        Payment payment;
        if (method.equalsIgnoreCase("PREPAID")) {
            payment = new PrepaidPayment(paymentId, trackingId, parcel.calculateCharge());
        } else if (method.equalsIgnoreCase("COD")
                || method.equalsIgnoreCase("CASH_ON_DELIVERY")) {
            payment = new CODPayment(paymentId, trackingId, parcel.calculateCharge());
        } else {
            throw new InvalidPaymentException("Method must be PREPAID or COD.");
        }
        payments.add(payment);
        return payment;
    }

    public boolean processPrepaidPayment(String trackingId)
            throws RecordNotFoundException, InvalidPaymentException {
        Payment payment = getPaymentByTrackingId(trackingId);
        if (!payment.getMethodName().equals("PREPAID")) {
            throw new InvalidPaymentException("This is not a prepaid payment.");
        }
        return payment.processPayment(payment.getAmount());
    }

    public boolean processCODPayment(String trackingId)
            throws RecordNotFoundException {
        Payment payment = getPaymentByTrackingId(trackingId);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());
        return true;
    }

    public Payment getPaymentByTrackingId(String trackingId)
            throws RecordNotFoundException {
        for (Payment payment : payments) {
            if (payment.getTrackingId().equals(trackingId)) {
                return payment;
            }
        }
        throw new RecordNotFoundException("Payment not found.");
    }

    public double calculateTotalCharge(Parcel parcel) {
        return parcel.calculateCharge();
    }

    public String generateReceipt(String trackingId)
            throws RecordNotFoundException {
        Payment payment = getPaymentByTrackingId(trackingId);
        return "Receipt: " + payment.getReceiptNumber()
                + "\nParcel: " + payment.getTrackingId()
                + "\nMethod: " + payment.getMethodName()
                + "\nAmount: " + String.format("%.2f", payment.getAmount())
                + "\nStatus: " + payment.getStatus();
    }

    public List<Payment> getPayments() {
        return payments;
    }
}
