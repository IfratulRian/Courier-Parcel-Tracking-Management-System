package service.parcel;

import exception.InvalidStatusException;
import model.parcel.Parcel;
import model.parcel.ParcelStatus;
import model.parcel.TrackingRecord;
import utility.InputValidator;
import java.time.LocalDateTime;
import java.util.List;

public class TrackingService {
    private final ParcelManager parcelManager;

    public TrackingService(ParcelManager parcelManager) {
        this.parcelManager = parcelManager;
    }

    public boolean isValidTransition(ParcelStatus oldStatus, ParcelStatus newStatus) {
        if (oldStatus == ParcelStatus.BOOKED) {
            return newStatus == ParcelStatus.PICKED_UP
                    || newStatus == ParcelStatus.CANCELLED;
        }
        if (oldStatus == ParcelStatus.PICKED_UP) {
            return newStatus == ParcelStatus.IN_TRANSIT
                    || newStatus == ParcelStatus.CANCELLED;
        }
        if (oldStatus == ParcelStatus.IN_TRANSIT) {
            return newStatus == ParcelStatus.ARRIVED_AT_HUB
                    || newStatus == ParcelStatus.RETURNED;
        }
        if (oldStatus == ParcelStatus.ARRIVED_AT_HUB) {
            return newStatus == ParcelStatus.OUT_FOR_DELIVERY
                    || newStatus == ParcelStatus.IN_TRANSIT;
        }
        if (oldStatus == ParcelStatus.OUT_FOR_DELIVERY) {
            return newStatus == ParcelStatus.DELIVERED
                    || newStatus == ParcelStatus.RETURNED;
        }
        return false;
    }

    public boolean updateStatus(String trackingId, ParcelStatus newStatus,
                                String agentId, String branchId, String comment)
            throws InvalidStatusException {
        if (comment != null && !comment.isEmpty()) {
            comment = InputValidator.validateStoredText(comment, "Tracking comment");
        }
        Parcel parcel = parcelManager.findByTrackingId(trackingId);
        if (parcel == null) {
            throw new InvalidStatusException("Parcel not found.");
        }
        ParcelStatus oldStatus = parcel.getStatus();
        if (!isValidTransition(oldStatus, newStatus)) {
            throw new InvalidStatusException(
                    "Invalid transition from " + oldStatus + " to " + newStatus + ".");
        }
        parcel.setStatus(newStatus);
        parcel.setAgentId(agentId);
        if (branchId != null && !branchId.trim().isEmpty()) {
            parcel.setCurrentBranchId(branchId);
        }
        parcel.addTrackingRecord(new TrackingRecord(trackingId, oldStatus, newStatus,
                LocalDateTime.now(), parcel.getCurrentBranchId(),
                agentId == null ? "" : agentId, comment == null ? "" : comment));
        return true;
    }

    public List<TrackingRecord> getTrackingHistory(String trackingId) {
        Parcel parcel = parcelManager.findByTrackingId(trackingId);
        if (parcel == null) {
            return List.of();
        }
        return parcel.getTrackingHistory();
    }
}
