package service.parcel;

import model.parcel.Parcel;
import model.parcel.ParcelStatus;
import java.util.ArrayList;
import java.util.List;

public class ParcelManager {
    private final List<Parcel> parcels = new ArrayList<>();

    public void addParcel(Parcel parcel) {
        parcels.add(parcel);
    }

    public Parcel findByTrackingId(String trackingId) {
        for (Parcel parcel : parcels) {
            if (parcel.getTrackingId().equalsIgnoreCase(trackingId)) {
                return parcel;
            }
        }
        return null;
    }

    public List<Parcel> getParcels() {
        return parcels;
    }

    public List<Parcel> getCustomerParcels(String customerId) {
        List<Parcel> result = new ArrayList<>();
        for (Parcel parcel : parcels) {
            if (parcel.getSenderId().equals(customerId)) {
                result.add(parcel);
            }
        }
        return result;
    }

    public List<Parcel> getAgentParcels(String agentId) {
        List<Parcel> result = new ArrayList<>();
        for (Parcel parcel : parcels) {
            if (parcel.getAgentId().equalsIgnoreCase(agentId)) {
                result.add(parcel);
            }
        }
        return result;
    }

    public int countByStatus(ParcelStatus status) {
        int count = 0;
        for (Parcel parcel : parcels) {
            if (parcel.getStatus() == status) {
                count++;
            }
        }
        return count;
    }
}
