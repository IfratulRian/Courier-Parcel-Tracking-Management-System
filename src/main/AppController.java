package main;

import controller.AdminMenu;
import controller.AgentMenu;
import controller.CustomerMenu;
import controller.MainMenu;
import exception.DuplicateIdException;
import exception.RecordNotFoundException;
import filehandling.DataLoader;
import model.user.Admin;
import model.user.Customer;
import model.user.DeliveryAgent;
import service.branch.BranchManager;
import service.branch.BranchService;
import service.delivery.DeliveryManager;
import service.delivery.DeliveryService;
import service.parcel.ParcelManager;
import service.parcel.ParcelService;
import service.parcel.TrackingService;
import service.payment.PaymentService;
import service.report.ReportManager;
import service.user.UserManager;
import service.user.UserService;
import java.util.Scanner;

public class AppController {
    private final Scanner scanner = new Scanner(System.in);
    private final UserManager userManager = new UserManager();
    private final ParcelManager parcelManager = new ParcelManager();
    private final BranchManager branchManager = new BranchManager();
    private final TrackingService trackingService =
            new TrackingService(parcelManager);
    private final DeliveryManager deliveryManager =
            new DeliveryManager(parcelManager);
    private final PaymentService paymentService =
            new PaymentService(parcelManager);

    private final UserService userService = new UserService(userManager);
    private final ParcelService parcelService =
            new ParcelService(parcelManager, branchManager, userManager);
    private final BranchService branchService = new BranchService(branchManager);
    private final DeliveryService deliveryService =
            new DeliveryService(deliveryManager, trackingService,
                    parcelManager, userManager, paymentService, branchManager);
    private final ReportManager reportManager =
            new ReportManager(parcelManager, paymentService, userManager);
    private final DataLoader dataLoader = new DataLoader();
    private final Admin admin =
            new Admin("ADMIN-001", "System Administrator", "admin", "admin123");

    public void start() {
        dataLoader.loadAll(userManager, branchManager, parcelManager, paymentService);
        createDefaultBranchIfEmpty();
        new MainMenu(scanner, this).show();
    }

    private void createDefaultBranchIfEmpty() {
        if (branchManager.getBranches().isEmpty()) {
            try {
                branchService.registerBranch("Main Office",
                        "Central Address", "Dhaka", "01700000000");
            } catch (DuplicateIdException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    public void adminLogin() {
        System.out.println("Enter the demo administrator credentials shown on the main menu.");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        if (!userService.loginAdmin(admin, username, password)) {
            System.out.println("Invalid administrator credentials.");
            return;
        }
        new AdminMenu(scanner, userService, userManager, branchService,
                parcelManager, deliveryService, paymentService, reportManager).show();
    }

    public void customerRegister() {
        System.out.println("Create your account. Enter each value when prompted.");
        String name = readRequired("Name");
        String phone = readRequired("Phone");
        String email = readRequired("Email");
        String username = readRequired("Username");
        String password = readRequired("Password");
        String address = readRequired("Address");
        try {
            Customer customer = userService.registerCustomer(
                    name, phone, email, username, password, address);
            System.out.println("Registration successful. Customer ID: "
                    + customer.getId());
        } catch (DuplicateIdException exception) {
            System.out.println("Registration failed: " + exception.getMessage());
        }
    }

    public void customerLogin() {
        String username = readRequired("Username");
        String password = readRequired("Password");
        try {
            Customer customer = userService.loginCustomer(username, password);
            new CustomerMenu(scanner, customer, parcelService, trackingService,
                    branchService, paymentService, userService).show();
        } catch (RecordNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void agentLogin() {
        String username = readRequired("Username");
        String password = readRequired("Password");
        try {
            DeliveryAgent agent = userService.loginAgent(username, password);
            new AgentMenu(scanner, agent, deliveryService).show();
        } catch (RecordNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private String readRequired(String label) {
        while (true) {
            System.out.print(label + ": ");
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println(label + " cannot be empty.");
        }
    }

    public void exit() {
        dataLoader.saveAll(userManager, branchManager, parcelManager, paymentService);
        System.out.println("Data saved. Thank you.");
        scanner.close();
    }
}
