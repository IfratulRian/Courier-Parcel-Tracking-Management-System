package model.parcel;

public class StandardParcel extends Parcel {
    public StandardParcel() {
        super();
    }

    public StandardParcel(String trackingId, String senderId, String receiverName, String receiverPhone,
                          String receiverAddress, double weight, int zone,
                          String originBranchId, String destinationBranchId) {
        super(trackingId, senderId, receiverName, receiverPhone, receiverAddress,
                weight, zone, originBranchId, destinationBranchId);
    }

    @Override
    public double calculateCharge() {
        return 80.0 + (getWeight() * 25.0) + (getZone() * 20.0);
    }

    @Override
    public double calculateCharge(int weightUnit) {
        return 80.0 + (weightUnit * 25.0) + (getZone() * 20.0);
    }

    @Override
    public ParcelCategory getCategory() {
        return ParcelCategory.STANDARD;
    }
}
