package filehandling;

import model.branch.Branch;
import model.parcel.ExpressParcel;
import model.parcel.Parcel;
import model.parcel.ParcelStatus;
import model.parcel.TrackingRecord;
import model.parcel.StandardParcel;
import model.payment.CODPayment;
import model.payment.Payment;
import model.payment.PaymentStatus;
import model.payment.PrepaidPayment;
import model.user.Customer;
import model.user.DeliveryAgent;
import model.user.Person;
import service.branch.BranchManager;
import service.parcel.ParcelManager;
import service.payment.PaymentService;
import service.user.UserManager;
import utility.DateTimeUtility;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    private static final String USER_FILE = "data/users.txt";
    private static final String BRANCH_FILE = "data/branches.txt";
    private static final String PARCEL_FILE = "data/parcels.txt";
    private static final String PAYMENT_FILE = "data/payments.txt";
    private static final String TRACKING_FILE = "data/tracking.txt";

    public void loadAll(UserManager userManager, BranchManager branchManager,
                        ParcelManager parcelManager, PaymentService paymentService) {
        loadUsers(userManager);
        loadBranches(branchManager);
        loadParcels(parcelManager);
        loadTracking(parcelManager);
        loadPayments(parcelManager, paymentService);
    }

    private void loadUsers(UserManager userManager) {
        for (String line : FileManager.readLines(USER_FILE)) {
            String[] values = line.split("\\|", -1);
            if (values.length < 9) continue;
            boolean active = Boolean.parseBoolean(values[8]);
            if (values[1].equals("CUSTOMER")) {
                Customer customer = new Customer(values[0], values[2], values[3],
                        values[4], values[5], values[6], values[7]);
                customer.setActive(active);
                userManager.addUser(customer);
            } else if (values[1].equals("AGENT")) {
                DeliveryAgent agent = new DeliveryAgent(values[0], values[2], values[3],
                        values[4], values[5], values[6], values[7]);
                agent.setActive(active);
                userManager.addUser(agent);
            }
        }
    }

    private void loadBranches(BranchManager branchManager) {
        for (String line : FileManager.readLines(BRANCH_FILE)) {
            String[] values = line.split("\\|", -1);
            if (values.length < 6) continue;
            Branch branch = new Branch(values[0], values[1], values[2],
                    values[3], values[4]);
            branch.setActive(Boolean.parseBoolean(values[5]));
            branchManager.addBranch(branch);
        }
    }

    private void loadParcels(ParcelManager parcelManager) {
        for (String line : FileManager.readLines(PARCEL_FILE)) {
            String[] values = line.split("\\|", -1);
            if (values.length < 15) continue;
            double weight = Double.parseDouble(values[6]);
            int zone = Integer.parseInt(values[7]);
            Parcel parcel;
            if (values[1].equalsIgnoreCase("EXPRESS")) {
                parcel = new ExpressParcel(values[0], values[2], values[3],
                        values[4], values[5], weight, zone, values[8], values[9]);
            } else {
                parcel = new StandardParcel(values[0], values[2], values[3],
                        values[4], values[5], weight, zone, values[8], values[9]);
            }
            parcel.setCurrentBranchId(values[10]);
            parcel.setAgentId(values[11]);
            parcel.setStatus(ParcelStatus.valueOf(values[12]));
            parcel.setCreatedDate(DateTimeUtility.parse(values[14]));
            parcelManager.addParcel(parcel);
        }
    }

    private void loadTracking(ParcelManager parcelManager) {
        for (String line : FileManager.readLines(TRACKING_FILE)) {
            String[] values = line.split("\\|", -1);
            if (values.length < 7) continue;
            Parcel parcel = parcelManager.findByTrackingId(values[0]);
            if (parcel == null) continue;
            ParcelStatus oldStatus = values[1].isEmpty()
                    ? null : ParcelStatus.valueOf(values[1]);
            parcel.addTrackingRecord(new TrackingRecord(values[0], oldStatus,
                    ParcelStatus.valueOf(values[2]),
                    DateTimeUtility.parse(values[3]), values[4],
                    values[5], values[6]));
        }
    }

    private void loadPayments(ParcelManager parcelManager, PaymentService paymentService) {
        for (String line : FileManager.readLines(PAYMENT_FILE)) {
            String[] values = line.split("\\|", -1);
            if (values.length < 7) continue;
            Payment payment;
            double amount = Double.parseDouble(values[4]);
            if (values[2].equalsIgnoreCase("PREPAID")) {
                payment = new PrepaidPayment(values[0], values[1], amount);
            } else {
                payment = new CODPayment(values[0], values[1], amount);
            }
            payment.setStatus(PaymentStatus.valueOf(values[3]));
            payment.setPaymentDate(DateTimeUtility.parse(values[5]));
            payment.setReceiptNumber(values[6]);
            paymentService.getPayments().add(payment);
        }
    }

    public void saveAll(UserManager userManager, BranchManager branchManager,
                        ParcelManager parcelManager, PaymentService paymentService) {
        saveUsers(userManager);
        saveBranches(branchManager);
        saveParcels(parcelManager);
        savePayments(paymentService);
        saveTracking(parcelManager);
    }

    private void saveUsers(UserManager userManager) {
        List<String> lines = new ArrayList<>();
        for (Person person : userManager.getUsers()) {
            if (person instanceof Customer) {
                Customer customer = (Customer) person;
                lines.add(customer.getId() + "|" + "CUSTOMER" + "|" + customer.getName()
                        + "|" + customer.getPhone() + "|" + customer.getEmail()
                        + "|" + customer.getUsername() + "|" + customer.getPassword()
                        + "|" + customer.getAddress() + "|" + customer.isActive());
            } else if (person instanceof DeliveryAgent) {
                DeliveryAgent agent = (DeliveryAgent) person;
                lines.add(agent.getId() + "|" + "AGENT" + "|" + agent.getName()
                        + "|" + agent.getPhone() + "|" + agent.getEmail()
                        + "|" + agent.getUsername() + "|" + agent.getPassword()
                        + "|" + agent.getBranchId() + "|" + agent.isActive());
            }
        }
        FileManager.writeLines(USER_FILE, lines);
    }

    private void saveBranches(BranchManager branchManager) {
        List<String> lines = new ArrayList<>();
        for (Branch branch : branchManager.getBranches()) {
            lines.add(branch.getId() + "|" + branch.getName() + "|" + branch.getAddress()
                    + "|" + branch.getCity() + "|" + branch.getPhone()
                    + "|" + branch.isActive());
        }
        FileManager.writeLines(BRANCH_FILE, lines);
    }

    private void saveParcels(ParcelManager parcelManager) {
        List<String> lines = new ArrayList<>();
        for (Parcel parcel : parcelManager.getParcels()) {
            lines.add(parcel.getTrackingId() + "|" + parcel.getCategory()
                    + "|" + parcel.getSenderId() + "|" + parcel.getReceiverName()
                    + "|" + parcel.getReceiverPhone() + "|" + parcel.getReceiverAddress()
                    + "|" + parcel.getWeight() + "|" + parcel.getZone()
                    + "|" + parcel.getOriginBranchId() + "|" + parcel.getDestinationBranchId()
                    + "|" + parcel.getCurrentBranchId() + "|" + parcel.getAgentId()
                    + "|" + parcel.getStatus() + "|" + parcel.calculateCharge()
                    + "|" + parcel.getCreatedDate().format(DateTimeUtility.STORAGE_FORMAT));
        }
        FileManager.writeLines(PARCEL_FILE, lines);
    }

    private void savePayments(PaymentService paymentService) {
        List<String> lines = new ArrayList<>();
        for (Payment payment : paymentService.getPayments()) {
            lines.add(payment.getPaymentId() + "|" + payment.getTrackingId()
                    + "|" + payment.getMethodName() + "|" + payment.getStatus()
                    + "|" + payment.getAmount() + "|"
                    + payment.getPaymentDate().format(DateTimeUtility.STORAGE_FORMAT)
                    + "|" + payment.getReceiptNumber());
        }
        FileManager.writeLines(PAYMENT_FILE, lines);
    }

    private void saveTracking(ParcelManager parcelManager) {
        List<String> lines = new ArrayList<>();
        for (Parcel parcel : parcelManager.getParcels()) {
            for (model.parcel.TrackingRecord record : parcel.getTrackingHistory()) {
                String oldStatus = record.getOldStatus() == null ? "" : record.getOldStatus().name();
                lines.add(parcel.getTrackingId() + "|" + oldStatus + "|"
                        + record.getNewStatus().name() + "|"
                        + record.getDateTime().format(DateTimeUtility.STORAGE_FORMAT)
                        + "|" + record.getBranchId() + "|" + record.getAgentId()
                        + "|" + record.getComment());
            }
        }
        FileManager.writeLines(TRACKING_FILE, lines);
    }
}
