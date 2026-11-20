package service.delivery;

import exception.InvalidStatusException;
import exception.RecordNotFoundException;
import model.parcel.Parcel;
import model.parcel.ParcelStatus;
import model.user.DeliveryAgent;
import service.parcel.ParcelManager;
import service.parcel.TrackingService;
import service.user.UserManager;
import java.util.List;

public class DeliveryService {
    private final DeliveryManager deliveryManager;
    private final TrackingService trackingService;
    private final ParcelManager parcelManager;
    private final UserManager userManager;

    public DeliveryService(DeliveryManager deliveryManager, TrackingService trackingService,
                           ParcelManager parcelManager, UserManager userManager) {
        this.deliveryManager = deliveryManager;
        this.trackingService = trackingService;
        this.parcelManager = parcelManager;
        this.userManager = userManager;
    }

    public boolean assignParcel(String trackingId, String agentId)
            throws RecordNotFoundException {
        Parcel parcel = parcelManager.findByTrackingId(trackingId);
        if (parcel == null) {
            throw new RecordNotFoundException("Parcel not found.");
        }
        DeliveryAgent agent = userManager.findAgentById(agentId);
        if (agent == null || !agent.isActive()) {
            throw new RecordNotFoundException("Active delivery agent not found.");
        }
        return deliveryManager.assignParcel(trackingId, agentId);
    }

    public boolean reassignParcel(String trackingId, String agentId)
            throws RecordNotFoundException {
        return assignParcel(trackingId, agentId);
    }

    public List<Parcel> getAssignedParcels(String agentId) {
        return deliveryManager.getAssignedParcels(agentId);
    }

    public boolean updateParcelStatus(String trackingId, ParcelStatus newStatus,
                                      String agentId, String branchId, String comment)
            throws InvalidStatusException {
        return trackingService.updateStatus(trackingId, newStatus, agentId, branchId, comment);
    }
}
