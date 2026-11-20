package model.parcel;

public class ExpressParcel extends Parcel {
    public ExpressParcel() {
        super();
    }

    public ExpressParcel(String trackingId, String senderId, String receiverName, String receiverPhone,
                         String receiverAddress, double weight, int zone,
                         String originBranchId, String destinationBranchId) {
        super(trackingId, senderId, receiverName, receiverPhone, receiverAddress,
                weight, zone, originBranchId, destinationBranchId);
    }

    @Override
    public double calculateCharge() {
        return 150.0 + (getWeight() * 35.0) + (getZone() * 30.0);
    }

    @Override
    public double calculateCharge(int weightUnit) {
        return 150.0 + (weightUnit * 35.0) + (getZone() * 30.0);
    }

    @Override
    public ParcelCategory getCategory() {
        return ParcelCategory.EXPRESS;
    }
}
