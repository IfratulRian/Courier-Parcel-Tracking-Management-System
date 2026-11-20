package service.delivery;

import model.parcel.Parcel;
import service.parcel.ParcelManager;
import java.util.List;

public class DeliveryManager {
    private final ParcelManager parcelManager;

    public DeliveryManager(ParcelManager parcelManager) {
        this.parcelManager = parcelManager;
    }

    public boolean assignParcel(String trackingId, String agentId) {
        Parcel parcel = parcelManager.findByTrackingId(trackingId);
        if (parcel == null) {
            return false;
        }
        parcel.setAgentId(agentId);
        return true;
    }

    public List<Parcel> getAssignedParcels(String agentId) {
        return parcelManager.getAgentParcels(agentId);
    }
}
