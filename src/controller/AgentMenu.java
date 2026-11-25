package controller;

import exception.InvalidStatusException;
import exception.InvalidPaymentException;
import exception.RecordNotFoundException;
import model.parcel.Parcel;
import model.parcel.ParcelStatus;
import model.user.DeliveryAgent;
import service.delivery.DeliveryService;
import utility.InputValidator;
import java.util.Scanner;

public class AgentMenu {
    private final Scanner scanner;
    private final DeliveryAgent agent;
    private final DeliveryService deliveryService;

    public AgentMenu(Scanner scanner, DeliveryAgent agent,
                     DeliveryService deliveryService) {
        this.scanner = scanner;
        this.agent = agent;
        this.deliveryService = deliveryService;
    }

    public void show() {
        while (true) {
            System.out.println("\n--- Delivery Agent Menu ---");
            System.out.println("Logged in: " + agent.getName());
            System.out.println("Enter the number beside the action you want:");
            System.out.println("1. List parcels assigned to you");
            System.out.println("2. Update one of your assigned parcels");
            System.out.println("3. View tracking history for one of your assigned parcels");
            System.out.println("4. Log out");
            System.out.print("Menu choice (1-4): ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1": viewAssigned(); break;
                    case "2": updateStatus(); break;
                    case "3": viewHistory(); break;
                    case "4": return;
                    default: System.out.println("Invalid menu option.");
                }
            } catch (Exception exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
    }

    private void viewAssigned() {
        java.util.List<Parcel> parcels =
                deliveryService.getAssignedParcels(agent.getId());
        if (parcels.isEmpty()) {
            System.out.println("No assigned parcels.");
            return;
        }
        for (Parcel parcel : parcels) {
            System.out.println(parcel.getTrackingId() + " | " + parcel.getStatus()
                    + " | Receiver=" + parcel.getReceiverName());
        }
    }

    private void updateStatus()
            throws InvalidStatusException, RecordNotFoundException, InvalidPaymentException {
        System.out.println("Enter the parcel's tracking ID, then select its next valid status.");
        String trackingId = InputValidator.readRequiredString(scanner, "Tracking ID: ");
        System.out.println("1=PICKED_UP 2=IN_TRANSIT 3=ARRIVED_AT_HUB");
        System.out.println("4=OUT_FOR_DELIVERY 5=DELIVERED 6=RETURNED");
        String choice = InputValidator.readRequiredString(scanner, "Status: ");
        ParcelStatus status;
        switch (choice) {
            case "1": status = ParcelStatus.PICKED_UP; break;
            case "2": status = ParcelStatus.IN_TRANSIT; break;
            case "3": status = ParcelStatus.ARRIVED_AT_HUB; break;
            case "4": status = ParcelStatus.OUT_FOR_DELIVERY; break;
            case "5": status = ParcelStatus.DELIVERED; break;
            case "6": status = ParcelStatus.RETURNED; break;
            default:
                throw new InvalidStatusException("Invalid status choice.");
        }
        String comment = InputValidator.readRequiredString(scanner, "Comment: ");
        boolean updated = deliveryService.updateParcelStatus(trackingId, status,
                agent.getId(), agent.getBranchId(), comment);
        if (updated) {
            System.out.println("Status updated.");
        }
    }

    private void viewHistory() throws RecordNotFoundException {
        String trackingId = InputValidator.readRequiredString(scanner, "Tracking ID: ");
        java.util.List<Parcel> parcels =
                deliveryService.getAssignedParcels(agent.getId());
        Parcel found = null;
        for (Parcel parcel : parcels) {
            if (parcel.getTrackingId().equalsIgnoreCase(trackingId)) {
                found = parcel;
                break;
            }
        }
        if (found == null) {
            throw new RecordNotFoundException("This parcel is not assigned to you.");
        }
        found.getTrackingHistory().forEach(record ->
                System.out.println(record.getDateTime() + " | "
                        + record.getOldStatus() + " -> " + record.getNewStatus()
                        + " | " + record.getComment()));
    }
}
