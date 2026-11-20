package controller;

import main.AppController;
import java.util.Scanner;

public class MainMenu {
    private final Scanner scanner;
    private final AppController appController;

    public MainMenu(Scanner scanner, AppController appController) {
        this.scanner = scanner;
        this.appController = appController;
    }

    public void show() {
        System.out.println("Welcome!");
        System.out.println("This is a text-based application. Type the number of a menu option");
        System.out.println("and press Enter. When a prompt appears, type the requested value.");
        System.out.println();
        System.out.println("Quick start for a customer:");
        System.out.println("  1) Choose 2 to create a customer account.");
        System.out.println("  2) Choose 3 to log in, then choose 1 to book a parcel.");
        System.out.println("  3) Keep the tracking ID shown after booking to track the parcel.");
        System.out.println();
        System.out.println("Demo administrator login: username=admin, password=admin123");
        System.out.println("The administrator can create branches and register delivery agents.");

        while (true) {
            System.out.println("\n====== Courier & Parcel Tracking System ======");
            System.out.println("Enter the number beside the action you want:");
            System.out.println("1. Admin Login");
            System.out.println("2. Create a new customer account");
            System.out.println("3. Log in as a registered customer");
            System.out.println("4. Log in as a registered delivery agent");
            System.out.println("5. Save data and exit");
            System.out.print("Menu choice (1-5): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    appController.adminLogin();
                    break;
                case "2":
                    appController.customerRegister();
                    break;
                case "3":
                    appController.customerLogin();
                    break;
                case "4":
                    appController.agentLogin();
                    break;
                case "5":
                    appController.exit();
                    return;
                default:
                    System.out.println("Invalid menu option.");
            }
        }
    }
}
