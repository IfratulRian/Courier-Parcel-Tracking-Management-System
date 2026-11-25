package service.parcel;

import exception.InvalidParcelException;
import exception.RecordNotFoundException;
import model.parcel.ExpressParcel;
import model.parcel.Parcel;
import model.parcel.ParcelCategory;
import model.parcel.ParcelStatus;
import model.parcel.StandardParcel;
import model.parcel.TrackingRecord;
import model.branch.Branch;
import service.branch.BranchManager;
import service.user.UserManager;
import model.user.Customer;
import model.user.Person;
import utility.IDGenerator;
import utility.InputValidator;
import java.time.LocalDateTime;
import java.util.List;

public class ParcelService {
    private final ParcelManager parcelManager;
    private final BranchManager branchManager;
    private final UserManager userManager;

    public ParcelService(ParcelManager parcelManager, BranchManager branchManager,
                         UserManager userManager) {
        this.parcelManager = parcelManager;
        this.branchManager = branchManager;
        this.userManager = userManager;
    }

    public Parcel createParcel(String senderId, String receiverName, String receiverPhone,
                               String receiverAddress, double weight, int zone,
                               String originBranchId, String destinationBranchId,
                               ParcelCategory category)
            throws InvalidParcelException, RecordNotFoundException {
        senderId = InputValidator.validateStoredText(senderId, "Sender ID");
        receiverName = InputValidator.validateStoredText(receiverName, "Receiver name");
        receiverPhone = InputValidator.validateStoredText(receiverPhone, "Receiver phone");
        receiverAddress = InputValidator.validateStoredText(receiverAddress, "Receiver address");
        originBranchId = InputValidator.validateStoredText(originBranchId, "Origin branch ID");
        destinationBranchId = InputValidator.validateStoredText(
                destinationBranchId, "Destination branch ID");
        Person sender = userManager.findById(senderId);
        if (!(sender instanceof Customer) || !sender.isActive()) {
            throw new InvalidParcelException("An active customer account is required to book a parcel.");
        }
        if (!Double.isFinite(weight) || weight <= 0) {
            throw new InvalidParcelException("Weight must be a finite number greater than zero.");
        }
        if (zone < 1 || zone > 5) {
            throw new InvalidParcelException("Zone must be from 1 to 5.");
        }
        if (category == null) {
            throw new InvalidParcelException("Parcel category is required.");
        }
        Branch origin = branchManager.findById(originBranchId);
        Branch destination = branchManager.findById(destinationBranchId);
        if (origin == null || !origin.isActive()) {
            throw new RecordNotFoundException("Active origin branch not found.");
        }
        if (destination == null || !destination.isActive()) {
            throw new RecordNotFoundException("Active destination branch not found.");
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
        if (!Double.isFinite(parcel.calculateCharge()) || parcel.calculateCharge() <= 0) {
            throw new InvalidParcelException("Parcel charge is outside the supported range.");
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

    public boolean cancelParcel(String trackingId, String customerId)
            throws InvalidParcelException, RecordNotFoundException {
        Parcel parcel = getParcelByTrackingId(trackingId);
        if (customerId == null || !parcel.getSenderId().equals(customerId)) {
            throw new RecordNotFoundException("Parcel not found for your account.");
        }
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
