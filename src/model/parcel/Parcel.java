package model.parcel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Parcel {
    private String trackingId;
    private String senderId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private double weight;
    private int zone;
    private String originBranchId;
    private String destinationBranchId;
    private String currentBranchId;
    private String agentId;
    private ParcelStatus status;
    private LocalDateTime createdDate;
    private final List<TrackingRecord> trackingHistory = new ArrayList<>();

    protected Parcel() {
        this.status = ParcelStatus.BOOKED;
        this.createdDate = LocalDateTime.now();
    }

    protected Parcel(String trackingId, String senderId, String receiverName, String receiverPhone,
                     String receiverAddress, double weight, int zone,
                     String originBranchId, String destinationBranchId) {
        this();
        this.trackingId = trackingId;
        this.senderId = senderId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverAddress = receiverAddress;
        this.weight = weight;
        this.zone = zone;
        this.originBranchId = originBranchId;
        this.destinationBranchId = destinationBranchId;
        this.currentBranchId = originBranchId;
        this.agentId = "";
    }

    public abstract double calculateCharge();
    public abstract double calculateCharge(int weightUnit);
    public abstract ParcelCategory getCategory();

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public String getReceiverAddress() { return receiverAddress; }
    public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public int getZone() { return zone; }
    public void setZone(int zone) { this.zone = zone; }
    public String getOriginBranchId() { return originBranchId; }
    public void setOriginBranchId(String originBranchId) { this.originBranchId = originBranchId; }
    public String getDestinationBranchId() { return destinationBranchId; }
    public void setDestinationBranchId(String destinationBranchId) { this.destinationBranchId = destinationBranchId; }
    public String getCurrentBranchId() { return currentBranchId; }
    public void setCurrentBranchId(String currentBranchId) { this.currentBranchId = currentBranchId; }
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId == null ? "" : agentId; }
    public ParcelStatus getStatus() { return status; }
    public void setStatus(ParcelStatus status) { this.status = status; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public List<TrackingRecord> getTrackingHistory() { return trackingHistory; }
    public void addTrackingRecord(TrackingRecord record) { trackingHistory.add(record); }
}
