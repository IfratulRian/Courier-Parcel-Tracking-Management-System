package model.parcel;

import java.time.LocalDateTime;

public class TrackingRecord {
    private String trackingId;
    private ParcelStatus oldStatus;
    private ParcelStatus newStatus;
    private LocalDateTime dateTime;
    private String branchId;
    private String agentId;
    private String comment;

    public TrackingRecord(String trackingId, ParcelStatus oldStatus, ParcelStatus newStatus,
                          LocalDateTime dateTime, String branchId, String agentId, String comment) {
        this.trackingId = trackingId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.dateTime = dateTime;
        this.branchId = branchId;
        this.agentId = agentId;
        this.comment = comment;
    }

    public String getTrackingId() { return trackingId; }
    public ParcelStatus getOldStatus() { return oldStatus; }
    public ParcelStatus getNewStatus() { return newStatus; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getBranchId() { return branchId; }
    public String getAgentId() { return agentId; }
    public String getComment() { return comment; }
}
