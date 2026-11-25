package controller;

import exception.DuplicateIdException;
import exception.RecordNotFoundException;
import model.branch.Branch;
import model.parcel.Parcel;
import model.parcel.ParcelStatus;
import model.user.DeliveryAgent;
import service.branch.BranchService;
import service.delivery.DeliveryService;
import service.parcel.ParcelManager;
import service.payment.PaymentService;
import service.report.ReportManager;
import service.user.UserManager;
import service.user.UserService;
import utility.InputValidator;
import java.util.Scanner;
import java.util.List;

public class AdminMenu {
    private final Scanner scanner;
    private final UserService userService;
    private final UserManager userManager;
    private final BranchService branchService;
    private final ParcelManager parcelManager;
    private final DeliveryService deliveryService;
    private final PaymentService paymentService;
    private final ReportManager reportManager;

    public AdminMenu(Scanner scanner, UserService userService, UserManager userManager,
                     BranchService branchService, ParcelManager parcelManager,
                     DeliveryService deliveryService, PaymentService paymentService,
                     ReportManager reportManager) {
        this.scanner = scanner;
        this.userService = userService;
        this.userManager = userManager;
        this.branchService = branchService;
        this.parcelManager = parcelManager;
        this.deliveryService = deliveryService;
        this.paymentService = paymentService;
        this.reportManager = reportManager;
    }

    public void show() {
        while (true) {
            System.out.println("\n--- Administrator Menu ---");
            System.out.println("Enter the number beside the action you want:");
            System.out.println("1. Register an agent (requires an existing branch)");
            System.out.println("2. Deactivate an agent using their agent ID");
            System.out.println("3. List agents and their IDs");
            System.out.println("4. Register a branch");
            System.out.println("5. List branches and their IDs");
            System.out.println("6. Assign a parcel to an agent using both IDs");
            System.out.println("7. List all parcels and their tracking IDs");
            System.out.println("8. View a parcel payment receipt using its tracking ID");
            System.out.println("9. View parcel and agent reports");
            System.out.println("10. Log out");
            System.out.print("Menu choice (1-10): ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1": registerAgent(); break;
                    case "2": removeAgent(); break;
                    case "3": viewAgents(); break;
                    case "4": registerBranch(); break;
                    case "5": viewBranches(); break;
                    case "6": assignParcel(); break;
                    case "7": viewParcels(); break;
                    case "8": viewPayment(); break;
                    case "9": showReports(); break;
                    case "10": return;
                    default: System.out.println("Invalid menu option.");
                }
            } catch (Exception exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
    }

    private void registerAgent() throws DuplicateIdException, RecordNotFoundException {
        System.out.println("Create the agent's login details and select an existing branch.");
        String name = InputValidator.readRequiredString(scanner, "Agent name: ");
        String phone = InputValidator.readRequiredString(scanner, "Phone: ");
        String email = InputValidator.readRequiredString(scanner, "Email: ");
        String username = InputValidator.readRequiredString(scanner, "Username: ");
        String password = InputValidator.readRequiredString(scanner, "Password: ");
        String branchId = InputValidator.readRequiredString(scanner, "Branch ID: ");
        Branch branch = branchService.getBranchById(branchId);
        if (!branch.isActive()) {
            throw new RecordNotFoundException("Branch is inactive.");
        }
        DeliveryAgent agent = userService.registerAgent(name, phone, email,
                username, password, branchId);
        System.out.println("Agent registered with ID: " + agent.getId());
    }

    private void removeAgent() throws RecordNotFoundException {
        String agentId = InputValidator.readRequiredString(scanner, "Agent ID: ");
        userService.removeAgent(agentId);
        System.out.println("Delivery agent removed.");
    }

    private void viewAgents() {
        List<DeliveryAgent> agents = userManager.getAgents();
        if (agents.isEmpty()) {
            System.out.println("No delivery agents found.");
            return;
        }
        for (DeliveryAgent agent : agents) {
            System.out.println(agent.getId() + " | " + agent.getName()
                    + " | " + agent.getBranchId()
                    + " | Active=" + agent.isActive());
        }
    }

    private void registerBranch() throws DuplicateIdException {
        System.out.println("Enter the branch details. The system will generate its branch ID.");
        String name = InputValidator.readRequiredString(scanner, "Branch name: ");
        String address = InputValidator.readRequiredString(scanner, "Address: ");
        String city = InputValidator.readRequiredString(scanner, "City: ");
        String phone = InputValidator.readRequiredString(scanner, "Phone: ");
        Branch branch = branchService.registerBranch(name, address, city, phone);
        System.out.println("Branch registered with ID: " + branch.getId());
    }

    private void viewBranches() throws RecordNotFoundException {
        if (branchService.getAllBranches().isEmpty()) {
            System.out.println("No branches found.");
            return;
        }
        for (Branch branch : branchService.getAllBranches()) {
            System.out.println(branch.getId() + " | " + branch.getName()
                    + " | " + branch.getCity() + " | Active=" + branch.isActive());
        }
    }

    private void assignParcel() throws RecordNotFoundException {
        String trackingId = InputValidator.readRequiredString(scanner, "Tracking ID: ");
        String agentId = InputValidator.readRequiredString(scanner, "Agent ID: ");
        if (deliveryService.assignParcel(trackingId, agentId)) {
            System.out.println("Parcel assigned successfully.");
        }
    }

    private void viewParcels() {
        if (parcelManager.getParcels().isEmpty()) {
            System.out.println("No parcels found.");
            return;
        }
        for (Parcel parcel : parcelManager.getParcels()) {
            printParcel(parcel);
        }
    }

    private void printParcel(Parcel parcel) {
        System.out.println(parcel.getTrackingId() + " | " + parcel.getCategory()
                + " | " + parcel.getStatus() + " | Branch=" + parcel.getCurrentBranchId()
                + " | Agent=" + (parcel.getAgentId().isEmpty() ? "Not assigned" : parcel.getAgentId())
                + " | Charge=" + String.format("%.2f", parcel.calculateCharge()));
    }

    private void viewPayment() throws Exception {
        String trackingId = InputValidator.readRequiredString(scanner, "Tracking ID: ");
        System.out.println(paymentService.generateReceipt(trackingId));
    }

    private void showReports() {
        reportManager.printSummary();
        reportManager.printAgentPerformanceReport();
    }
}
