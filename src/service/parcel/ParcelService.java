package service.parcel;

import exception.InvalidParcelException;
import exception.RecordNotFoundException;
import model.parcel.ExpressParcel;
import model.parcel.Parcel;
import model.parcel.ParcelCategory;
import model.parcel.ParcelStatus;
import model.parcel.StandardParcel;
import model.parcel.TrackingRecord;
import utility.IDGenerator;
import java.time.LocalDateTime;
import java.util.List;

public class ParcelService {
    private final ParcelManager parcelManager;

    public ParcelService(ParcelManager parcelManager) {
        this.parcelManager = parcelManager;
    }

    public Parcel createParcel(String senderId, String receiverName, String receiverPhone,
                               String receiverAddress, double weight, int zone,
                               String originBranchId, String destinationBranchId,
                               ParcelCategory category) throws InvalidParcelException {
        if (weight <= 0) {
            throw new InvalidParcelException("Weight must be greater than zero.");
        }
        if (zone < 1 || zone > 5) {
            throw new InvalidParcelException("Zone must be from 1 to 5.");
        }
        if (receiverName.trim().isEmpty() || receiverAddress.trim().isEmpty()) {
            throw new InvalidParcelException("Receiver name and address are required.");
        }

        String trackingId = IDGenerator.generateTrackingId();
        Parcel parcel;
        if (category == ParcelCategory.EXPRESS) {
            parcel = new ExpressParcel(trackingId, senderId, receiverName, receiverPhone,
                    receiverAddress, weight, zone, originBranchId, destinationBranchId);
        } else {
            parcel = new StandardParcel(trackingId, senderId, receiverName, receiverPhone,
                    receiverAddress, weight, zone, originBranchId, destinationBranchId);
        }
        parcel.addTrackingRecord(new TrackingRecord(trackingId, null, ParcelStatus.BOOKED,
                LocalDateTime.now(), originBranchId, "", "Parcel booked"));
        parcelManager.addParcel(parcel);
        return parcel;
    }

    public Parcel getParcelByTrackingId(String trackingId)
            throws RecordNotFoundException {
        Parcel parcel = parcelManager.findByTrackingId(trackingId);
        if (parcel == null) {
            throw new RecordNotFoundException("Parcel not found.");
        }
        return parcel;
    }

    public List<Parcel> getCustomerParcels(String customerId) {
        return parcelManager.getCustomerParcels(customerId);
    }

    public boolean cancelParcel(String trackingId)
            throws InvalidParcelException, RecordNotFoundException {
        Parcel parcel = getParcelByTrackingId(trackingId);
        ParcelStatus oldStatus = parcel.getStatus();
        if (parcel.getStatus() != ParcelStatus.BOOKED
                && parcel.getStatus() != ParcelStatus.PICKED_UP) {
            throw new InvalidParcelException(
                    "Parcel cannot be cancelled after it leaves the eligible stage.");
        }
        parcel.setStatus(ParcelStatus.CANCELLED);
        parcel.addTrackingRecord(new TrackingRecord(trackingId,
                oldStatus, ParcelStatus.CANCELLED,
                LocalDateTime.now(), parcel.getCurrentBranchId(),
                parcel.getAgentId(), "Parcel cancelled"));
        return true;
    }

    public double calculateCharge(Parcel parcel) {
        return parcel.calculateCharge();
    }
}
