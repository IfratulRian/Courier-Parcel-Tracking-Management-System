package service.report;

import model.parcel.Parcel;
import model.parcel.ParcelStatus;
import model.payment.Payment;
import model.payment.PaymentStatus;
import model.user.DeliveryAgent;
import service.parcel.ParcelManager;
import service.payment.PaymentService;
import service.user.UserManager;
import java.util.List;

public class ReportManager {
    private final ParcelManager parcelManager;
    private final PaymentService paymentService;
    private final UserManager userManager;

    public ReportManager(ParcelManager parcelManager, PaymentService paymentService,
                         UserManager userManager) {
        this.parcelManager = parcelManager;
        this.paymentService = paymentService;
        this.userManager = userManager;
    }

    public int getTotalParcelCount() {
        return parcelManager.getParcels().size();
    }

    public int getDeliveredParcelCount() {
        return parcelManager.countByStatus(ParcelStatus.DELIVERED);
    }

    public int getPendingParcelCount() {
        int pending = 0;
        for (Parcel parcel : parcelManager.getParcels()) {
            if (parcel.getStatus() != ParcelStatus.DELIVERED
                    && parcel.getStatus() != ParcelStatus.CANCELLED
                    && parcel.getStatus() != ParcelStatus.RETURNED) {
                pending++;
            }
        }
        return pending;
    }

    public int getCancelledParcelCount() {
        return parcelManager.countByStatus(ParcelStatus.CANCELLED);
    }

    public double getTotalRevenue() {
        double total = 0.0;
        for (Payment payment : paymentService.getPayments()) {
            if (payment.getStatus() == PaymentStatus.PAID) {
                total += payment.getAmount();
            }
        }
        return total;
    }

    public void printAgentPerformanceReport() {
        System.out.println("\n--- Delivery Agent Performance ---");
        for (DeliveryAgent agent : userManager.getAgents()) {
            List<Parcel> assigned = parcelManager.getAgentParcels(agent.getId());
            int delivered = 0;
            int cancelled = 0;
            int returned = 0;
            for (Parcel parcel : assigned) {
                if (parcel.getStatus() == ParcelStatus.DELIVERED) {
                    delivered++;
                } else if (parcel.getStatus() == ParcelStatus.CANCELLED) {
                    cancelled++;
                } else if (parcel.getStatus() == ParcelStatus.RETURNED) {
                    returned++;
                }
            }
            System.out.println(agent.getId() + " | " + agent.getName()
                    + " | Assigned=" + assigned.size()
                    + " | Delivered=" + delivered
                    + " | Cancelled=" + cancelled
                    + " | Returned=" + returned);
        }
    }

    public void printSummary() {
        System.out.println("\n--- Parcel Report ---");
        System.out.println("Total parcels: " + getTotalParcelCount());
        System.out.println("Delivered parcels: " + getDeliveredParcelCount());
        System.out.println("Pending parcels: " + getPendingParcelCount());
        System.out.println("Cancelled parcels: " + getCancelledParcelCount());
        System.out.println("Total collected revenue: " + String.format("%.2f", getTotalRevenue()));
    }
}
