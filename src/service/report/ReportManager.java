package service.report;

import model.parcel.Parcel;
import model.parcel.ParcelStatus;
import model.parcel.ParcelCategory;
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

    public double getRevenueByMethod(String method) {
        double total = 0.0;
        for (Payment payment : paymentService.getPayments()) {
            if (payment.getStatus() == PaymentStatus.PAID
                    && payment.getMethodName().equalsIgnoreCase(method)) {
                total += payment.getAmount();
            }
        }
        return total;
    }

    public int getParcelCountByCategory(ParcelCategory category) {
        int count = 0;
        for (Parcel parcel : parcelManager.getParcels()) {
            if (parcel.getCategory() == category) {
                count++;
            }
        }
        return count;
    }

    public int getParcelCountByZone(int zone) {
        int count = 0;
        for (Parcel parcel : parcelManager.getParcels()) {
            if (parcel.getZone() == zone) {
                count++;
            }
        }
        return count;
    }

    public void printAgentPerformanceReport() {
        System.out.println("\n--- Delivery Agent Performance ---");
        for (DeliveryAgent agent : userManager.getAgents()) {
            List<Parcel> assigned = parcelManager.getAgentParcels(agent.getId());
            int delivered = 0;
            int cancelled = 0;
            int returned = 0;
            for (Parcel parcel : parcelManager.getParcels()) {
                for (model.parcel.TrackingRecord record : parcel.getTrackingHistory()) {
                    if (!agent.getId().equalsIgnoreCase(record.getAgentId())) {
                        continue;
                    }
                    if (record.getNewStatus() == ParcelStatus.DELIVERED) {
                        delivered++;
                    } else if (record.getNewStatus() == ParcelStatus.CANCELLED) {
                        cancelled++;
                    } else if (record.getNewStatus() == ParcelStatus.RETURNED) {
                        returned++;
                    }
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
        System.out.println("Prepaid revenue: "
                + String.format("%.2f", getRevenueByMethod("PREPAID")));
        System.out.println("Cash on Delivery revenue: "
                + String.format("%.2f", getRevenueByMethod("CASH_ON_DELIVERY")));
        System.out.println("Standard parcels: "
                + getParcelCountByCategory(ParcelCategory.STANDARD));
        System.out.println("Express parcels: "
                + getParcelCountByCategory(ParcelCategory.EXPRESS));
        System.out.println("Parcels by zone:");
        for (int zone = 1; zone <= 5; zone++) {
            System.out.println("  Zone " + zone + ": " + getParcelCountByZone(zone));
        }
        System.out.println("Parcel statuses:");
        for (ParcelStatus status : ParcelStatus.values()) {
            System.out.println("  " + status + ": " + parcelManager.countByStatus(status));
        }
    }
}
