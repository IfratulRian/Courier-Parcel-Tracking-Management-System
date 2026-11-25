package service.delivery;

import exception.InvalidStatusException;
import exception.InvalidPaymentException;
import exception.RecordNotFoundException;
import model.parcel.Parcel;
import model.parcel.ParcelStatus;
import model.payment.Payment;
import model.user.DeliveryAgent;
import service.parcel.ParcelManager;
import service.parcel.TrackingService;
import service.branch.BranchManager;
import service.payment.PaymentService;
import service.user.UserManager;
import java.util.List;

public class DeliveryService {
    private final DeliveryManager deliveryManager;
    private final TrackingService trackingService;
    private final ParcelManager parcelManager;
    private final UserManager userManager;
    private final PaymentService paymentService;
    private final BranchManager branchManager;

    public DeliveryService(DeliveryManager deliveryManager, TrackingService trackingService,
                           ParcelManager parcelManager, UserManager userManager,
                           PaymentService paymentService, BranchManager branchManager) {
        this.deliveryManager = deliveryManager;
        this.trackingService = trackingService;
        this.parcelManager = parcelManager;
        this.userManager = userManager;
        this.paymentService = paymentService;
        this.branchManager = branchManager;
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
        if (agent.getBranchId() == null) {
            throw new RecordNotFoundException("Agent is not assigned to a branch.");
        }
        model.branch.Branch branch = branchManager.findById(agent.getBranchId());
        if (branch == null || !branch.isActive()) {
            throw new RecordNotFoundException("Agent's branch is not active.");
        }
        if (parcel.getStatus() == ParcelStatus.DELIVERED
                || parcel.getStatus() == ParcelStatus.CANCELLED
                || parcel.getStatus() == ParcelStatus.RETURNED) {
            throw new RecordNotFoundException("A completed parcel cannot be assigned.");
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
            throws InvalidStatusException, RecordNotFoundException, InvalidPaymentException {
        Parcel parcel = parcelManager.findByTrackingId(trackingId);
        if (parcel == null) {
            throw new InvalidStatusException("Parcel not found.");
        }
        DeliveryAgent agent = userManager.findAgentById(agentId);
        if (agent == null || !agent.isActive()
                || !parcel.getAgentId().equalsIgnoreCase(agentId)) {
            throw new RecordNotFoundException("This parcel is not assigned to an active agent.");
        }
        if (branchId == null || agent.getBranchId() == null
                || !agent.getBranchId().equalsIgnoreCase(branchId)) {
            throw new InvalidStatusException("Status updates must use the agent's assigned branch.");
        }
        model.branch.Branch branch = branchManager.findById(agent.getBranchId());
        if (branch == null || !branch.isActive()) {
            throw new RecordNotFoundException("Agent's branch is not active.");
        }
        if (newStatus == ParcelStatus.DELIVERED) {
            if (!trackingService.isValidTransition(parcel.getStatus(), newStatus)) {
                throw new InvalidStatusException(
                        "Invalid transition from " + parcel.getStatus() + " to " + newStatus + ".");
            }
            paymentService.validateForDelivery(trackingId);
        }
        boolean updated = trackingService.updateStatus(
                trackingId, newStatus, agentId, branchId, comment);
        if (updated && newStatus == ParcelStatus.DELIVERED) {
            Payment payment = paymentService.getPaymentByTrackingId(trackingId);
            if (payment.getMethodName().equals("CASH_ON_DELIVERY")) {
                paymentService.processCODPayment(trackingId);
            }
        }
        return updated;
    }
}
