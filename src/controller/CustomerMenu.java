package controller;

import exception.InvalidParcelException;
import exception.InvalidPaymentException;
import exception.RecordNotFoundException;
import model.parcel.Parcel;
import model.parcel.ParcelCategory;
import model.payment.Payment;
import model.user.Customer;
import service.branch.BranchService;
import service.parcel.ParcelService;
import service.parcel.TrackingService;
import service.payment.PaymentService;
import service.user.UserService;
import utility.InputValidator;
import java.util.List;
import java.util.Scanner;

public class CustomerMenu {
    private final Scanner scanner;
    private final Customer customer;
    private final ParcelService parcelService;
    private final TrackingService trackingService;
    private final BranchService branchService;
    private final PaymentService paymentService;
    private final UserService userService;

    public CustomerMenu(Scanner scanner, Customer customer, ParcelService parcelService,
                        TrackingService trackingService, BranchService branchService,
                        PaymentService paymentService, UserService userService) {
        this.scanner = scanner;
        this.customer = customer;
        this.parcelService = parcelService;
        this.trackingService = trackingService;
        this.branchService = branchService;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    public void show() {
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("Logged in: " + customer.getName());
            System.out.println("Choose an action by entering its number:");
            System.out.println("1. Book a parcel (you will enter receiver, weight, zone, branches and payment)");
            System.out.println("2. List parcels booked by your account");
            System.out.println("3. See a parcel's current status and tracking history");
            System.out.println("4. Cancel your parcel if it has not entered transit");
            System.out.println("5. View a parcel's payment receipt");
            System.out.println("6. Mark a prepaid payment as paid (demo)");
            System.out.println("7. Change your profile details");
            System.out.println("8. Log out to the main menu");
            System.out.print("Menu choice (1-8): ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1": createParcel(); break;
                    case "2": viewMyParcels(); break;
                    case "3": trackParcel(); break;
                    case "4": cancelParcel(); break;
                    case "5": viewPayment(); break;
                    case "6": processPrepaid(); break;
                    case "7": updateProfile(); break;
                    case "8": return;
                    default: System.out.println("Invalid menu option.");
                }
            } catch (Exception exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
    }

    private void createParcel()
            throws InvalidParcelException, RecordNotFoundException {
        System.out.println("\nBook a parcel. Enter each requested value and press Enter.");
        String receiverName = InputValidator.readRequiredString(scanner, "Receiver name: ");
        String receiverPhone = InputValidator.readRequiredString(scanner, "Receiver phone: ");
        String receiverAddress = InputValidator.readRequiredString(scanner, "Receiver address: ");
        double weight = InputValidator.readDouble(scanner, "Parcel weight in kilograms (for example 2.5): ");
        if (weight <= 0) {
            throw new InvalidParcelException("Weight must be greater than zero.");
        }
        System.out.println("Delivery zones are distance bands: 1 is nearest, 5 is farthest.");
        int zone = InputValidator.readInt(scanner, "Enter delivery zone (1-5): ");
        System.out.println("Available branches:");
        if (branchService.getAllBranches().isEmpty()) {
            throw new RecordNotFoundException("At least one branch is required.");
        }
        branchService.getAllBranches().forEach(System.out::println);
        String originId = InputValidator.readRequiredString(scanner, "Origin branch ID: ");
        String destinationId = InputValidator.readRequiredString(scanner, "Destination branch ID: ");
        branchService.getBranchById(originId);
        branchService.getBranchById(destinationId);
        System.out.println("Parcel category: 1=Standard (lower charge), 2=Express (faster service).");
        System.out.print("Category choice (1-2): ");
        String categoryChoice = scanner.nextLine().trim();
        if (!categoryChoice.equals("1") && !categoryChoice.equals("2")) {
            throw new InvalidParcelException("Choose 1 for Standard or 2 for Express.");
        }
        ParcelCategory category = categoryChoice.equals("2")
                ? ParcelCategory.EXPRESS : ParcelCategory.STANDARD;

        Parcel parcel = parcelService.createParcel(customer.getId(), receiverName,
                receiverPhone, receiverAddress, weight, zone,
                originId, destinationId, category);

        System.out.println("Parcel created.");
        System.out.println("Tracking ID: " + parcel.getTrackingId());
        System.out.println("Calculated charge: " +
                String.format("%.2f", parcel.calculateCharge()));
        System.out.println("Payment: 1=Prepaid (pay now in this demo), 2=Cash on Delivery.");
        System.out.print("Payment choice (1-2): ");
        String paymentChoice = scanner.nextLine().trim();
        if (!paymentChoice.equals("1") && !paymentChoice.equals("2")) {
            throw new InvalidParcelException("Choose 1 for Prepaid or 2 for Cash on Delivery.");
        }
        String method = paymentChoice.equals("2") ? "COD" : "PREPAID";
        try {
            Payment payment = paymentService.createPayment(parcel.getTrackingId(), method);
            System.out.println("Payment created. Receipt: " + payment.getReceiptNumber());
        } catch (InvalidPaymentException exception) {
            throw new InvalidParcelException(exception.getMessage());
        }
    }

    private void viewMyParcels() {
        List<Parcel> parcels = parcelService.getCustomerParcels(customer.getId());
        if (parcels.isEmpty()) {
            System.out.println("You have no parcels.");
            return;
        }
        for (Parcel parcel : parcels) {
            System.out.println(parcel.getTrackingId() + " | " + parcel.getCategory()
                    + " | " + parcel.getStatus() + " | Branch="
                    + parcel.getCurrentBranchId());
        }
    }

    private void trackParcel() throws RecordNotFoundException {
        String trackingId = InputValidator.readRequiredString(scanner, "Tracking ID: ");
        Parcel parcel = parcelService.getParcelByTrackingId(trackingId);
        System.out.println("Status: " + parcel.getStatus());
        if (parcel.getTrackingHistory().isEmpty()) {
            System.out.println("No status history.");
            return;
        }
        parcel.getTrackingHistory().forEach(record ->
                System.out.println(record.getDateTime() + " | "
                        + record.getOldStatus() + " -> " + record.getNewStatus()
                        + " | " + record.getComment()));
    }

    private void cancelParcel()
            throws InvalidParcelException, RecordNotFoundException {
        String trackingId = InputValidator.readRequiredString(scanner, "Tracking ID: ");
        Parcel parcel = parcelService.getParcelByTrackingId(trackingId);
        if (!parcel.getSenderId().equals(customer.getId())) {
            throw new InvalidParcelException("You can cancel only your own parcel.");
        }
        parcelService.cancelParcel(trackingId);
        System.out.println("Parcel cancelled.");
    }

    private void viewPayment() throws RecordNotFoundException {
        String trackingId = InputValidator.readRequiredString(scanner, "Tracking ID: ");
        System.out.println(paymentService.generateReceipt(trackingId));
    }

    private void processPrepaid()
            throws RecordNotFoundException, InvalidPaymentException {
        String trackingId = InputValidator.readRequiredString(scanner, "Tracking ID: ");
        if (paymentService.processPrepaidPayment(trackingId)) {
            System.out.println("Payment marked as PAID.");
        }
    }

    private void updateProfile() {
        String name = InputValidator.readRequiredString(scanner, "New name: ");
        String phone = InputValidator.readRequiredString(scanner, "New phone: ");
        String email = InputValidator.readRequiredString(scanner, "New email: ");
        String address = InputValidator.readRequiredString(scanner, "New address: ");
        userService.updateCustomerProfile(customer, name, phone, email, address);
        System.out.println("Profile updated.");
    }
}
