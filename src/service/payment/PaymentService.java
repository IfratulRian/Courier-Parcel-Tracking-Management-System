package service.payment;

import exception.InvalidPaymentException;
import exception.RecordNotFoundException;
import model.payment.CODPayment;
import model.payment.Payment;
import model.payment.PaymentStatus;
import model.payment.PrepaidPayment;
import model.parcel.Parcel;
import model.parcel.ParcelStatus;
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
        if (parcel.getStatus() != ParcelStatus.BOOKED
                || !Double.isFinite(parcel.calculateCharge())
                || parcel.calculateCharge() <= 0) {
            throw new InvalidPaymentException(
                    "A valid payment can only be created for a newly booked parcel.");
        }
        for (Payment payment : payments) {
            if (payment.getTrackingId().equalsIgnoreCase(trackingId)) {
                throw new InvalidPaymentException("A payment already exists for this parcel.");
            }
        }
        Payment payment;
        if (method != null && method.equalsIgnoreCase("PREPAID")) {
            payment = new PrepaidPayment(
                    IDGenerator.generatePaymentId(), trackingId, parcel.calculateCharge());
        } else if (method != null && (method.equalsIgnoreCase("COD")
                || method.equalsIgnoreCase("CASH_ON_DELIVERY"))) {
            payment = new CODPayment(
                    IDGenerator.generatePaymentId(), trackingId, parcel.calculateCharge());
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
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidPaymentException("Prepaid payment is not pending.");
        }
        if (!payment.processPayment(payment.getAmount())) {
            throw new InvalidPaymentException("Prepaid payment could not be processed.");
        }
        return true;
    }

    public boolean processCODPayment(String trackingId)
            throws RecordNotFoundException, InvalidPaymentException {
        Payment payment = getPaymentByTrackingId(trackingId);
        Parcel parcel = parcelManager.findByTrackingId(trackingId);
        if (!payment.getMethodName().equals("CASH_ON_DELIVERY")
                || payment.getStatus() != PaymentStatus.PENDING
                || parcel == null || parcel.getStatus() != model.parcel.ParcelStatus.DELIVERED) {
            throw new InvalidPaymentException(
                    "Cash on Delivery can only be collected for a delivered parcel.");
        }
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());
        return true;
    }

    public void validateForDelivery(String trackingId)
            throws RecordNotFoundException, InvalidPaymentException {
        Payment payment = getPaymentByTrackingId(trackingId);
        if (payment.getMethodName().equals("PREPAID")
                && payment.getStatus() == PaymentStatus.PAID) {
            return;
        }
        if (payment.getMethodName().equals("CASH_ON_DELIVERY")
                && payment.getStatus() == PaymentStatus.PENDING) {
            return;
        }
        throw new InvalidPaymentException(
                "Payment must be valid before the parcel can be delivered.");
    }

    public void validateCanCancel(String trackingId)
            throws RecordNotFoundException, InvalidPaymentException {
        Payment payment = getPaymentByTrackingId(trackingId);
        if (payment.getStatus() != PaymentStatus.PENDING
                && !(payment.getStatus() == PaymentStatus.PAID
                && payment.getMethodName().equals("PREPAID"))) {
            throw new InvalidPaymentException("This payment cannot be cancelled.");
        }
    }

    public boolean cancelPayment(String trackingId)
            throws RecordNotFoundException, InvalidPaymentException {
        validateCanCancel(trackingId);
        Payment payment = getPaymentByTrackingId(trackingId);
        Parcel parcel = parcelManager.findByTrackingId(trackingId);
        if (parcel == null || parcel.getStatus() != ParcelStatus.CANCELLED) {
            throw new InvalidPaymentException(
                    "The parcel must be cancelled before its payment is reversed.");
        }
        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.FAILED);
        } else if (payment.getStatus() == PaymentStatus.PAID
                && payment.getMethodName().equals("PREPAID")) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            throw new InvalidPaymentException("This payment cannot be cancelled.");
        }
        payment.setPaymentDate(LocalDateTime.now());
        return true;
    }

    public Payment getPaymentByTrackingId(String trackingId)
            throws RecordNotFoundException {
        for (Payment payment : payments) {
            if (payment.getTrackingId().equalsIgnoreCase(trackingId)) {
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
