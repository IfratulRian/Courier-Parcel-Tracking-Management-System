package filehandling;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import model.branch.Branch;
import model.parcel.ExpressParcel;
import model.parcel.Parcel;
import model.parcel.ParcelStatus;
import model.parcel.StandardParcel;
import model.parcel.TrackingRecord;
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
import utility.IDGenerator;

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
        validateAgentBranches(userManager, branchManager);
        loadParcels(userManager, branchManager, parcelManager);
        loadTracking(parcelManager);
        loadPayments(parcelManager, paymentService);
    }

    private void loadUsers(UserManager userManager) {
        loadRows(USER_FILE, 9, values -> {
            IDGenerator.reserve(values[0]);
            boolean active = parseBoolean(values[8], USER_FILE);
            if (values[1].equals("CUSTOMER")) {
                if (userManager.findById(values[0]) != null
                        || userManager.findCustomerByUsername(values[5]) != null) {
                    throw new IllegalStateException("Duplicate user record in " + USER_FILE);
                }
                Customer customer = new Customer(values[0], values[2], values[3],
                        values[4], values[5], values[6], values[7]);
                customer.setActive(active);
                userManager.addUser(customer);
            } else if (values[1].equals("AGENT")) {
                if (userManager.findById(values[0]) != null
                        || userManager.findAgentByUsername(values[5]) != null) {
                    throw new IllegalStateException("Duplicate user record in " + USER_FILE);
                }
                DeliveryAgent agent = new DeliveryAgent(values[0], values[2], values[3],
                        values[4], values[5], values[6], values[7]);
                agent.setActive(active);
                userManager.addUser(agent);
            } else {
                throw new IllegalStateException("Unknown user role in " + USER_FILE + ": " + values[1]);
            }
        });
    }

    private void loadBranches(BranchManager branchManager) {
        loadRows(BRANCH_FILE, 6, values -> {
            IDGenerator.reserve(values[0]);
            Branch branch = new Branch(values[0], values[1], values[2],
                    values[3], values[4]);
            if (branchManager.findById(values[0]) != null) {
                throw new IllegalStateException("Duplicate branch ID in " + BRANCH_FILE);
            }
            branch.setActive(parseBoolean(values[5], BRANCH_FILE));
            branchManager.addBranch(branch);
        });
    }

    private void validateAgentBranches(UserManager userManager, BranchManager branchManager) {
        for (DeliveryAgent agent : userManager.getAgents()) {
            if (branchManager.findById(agent.getBranchId()) == null) {
                throw new IllegalStateException(
                        "Agent references missing branch " + agent.getBranchId());
            }
        }
    }

    private void loadParcels(UserManager userManager, BranchManager branchManager,
                             ParcelManager parcelManager) {
        loadRows(PARCEL_FILE, 15, values -> {
            IDGenerator.reserve(values[0]);
            if (parcelManager.findByTrackingId(values[0]) != null) {
                throw new IllegalStateException("Duplicate parcel ID in " + PARCEL_FILE);
            }
            double weight = Double.parseDouble(values[6]);
            int zone = Integer.parseInt(values[7]);
            if (!Double.isFinite(weight) || weight <= 0 || zone < 1 || zone > 5) {
                throw new IllegalStateException("Invalid weight or zone in " + PARCEL_FILE);
            }
            Person sender = userManager.findById(values[2]);
            if (!(sender instanceof Customer)) {
                throw new IllegalStateException(
                        "Parcel references missing customer " + values[2]);
            }
            for (String branchId : new String[]{values[8], values[9], values[10]}) {
                if (branchManager.findById(branchId) == null) {
                    throw new IllegalStateException(
                            "Parcel references missing branch " + branchId);
                }
            }
            if (!values[11].isEmpty()
                    && !(userManager.findById(values[11]) instanceof DeliveryAgent)) {
                throw new IllegalStateException(
                        "Parcel references missing delivery agent " + values[11]);
            }
            Parcel parcel;
            if (values[1].equalsIgnoreCase("EXPRESS")) {
                parcel = new ExpressParcel(values[0], values[2], values[3],
                        values[4], values[5], weight, zone, values[8], values[9]);
            } else if (values[1].equalsIgnoreCase("STANDARD")) {
                parcel = new StandardParcel(values[0], values[2], values[3],
                        values[4], values[5], weight, zone, values[8], values[9]);
            } else {
                throw new IllegalStateException(
                        "Unknown parcel category in " + PARCEL_FILE + ": " + values[1]);
            }
            double storedCharge = Double.parseDouble(values[13]);
            if (!Double.isFinite(storedCharge) || storedCharge <= 0) {
                throw new IllegalStateException("Invalid charge in " + PARCEL_FILE);
            }
            parcel.setCurrentBranchId(values[10]);
            parcel.setAgentId(values[11]);
            parcel.setStatus(ParcelStatus.valueOf(values[12]));
            parcel.setCreatedDate(DateTimeUtility.parse(values[14]));
            parcelManager.addParcel(parcel);
        });
    }

    private void loadTracking(ParcelManager parcelManager) {
        loadRows(TRACKING_FILE, 7, values -> {
            Parcel parcel = parcelManager.findByTrackingId(values[0]);
            if (parcel == null) {
                throw new IllegalStateException(
                        "Tracking record references missing parcel " + values[0]);
            }
            ParcelStatus oldStatus = values[1].isEmpty()
                    ? null : ParcelStatus.valueOf(values[1]);
            parcel.addTrackingRecord(new TrackingRecord(values[0], oldStatus,
                    ParcelStatus.valueOf(values[2]),
                    DateTimeUtility.parse(values[3]), values[4],
                    values[5], values[6]));
        });
    }

    private void loadPayments(ParcelManager parcelManager, PaymentService paymentService) {
        loadRows(PAYMENT_FILE, 7, values -> {
            IDGenerator.reserve(values[0]);
            if (parcelManager.findByTrackingId(values[1]) == null) {
                throw new IllegalStateException(
                        "Payment references missing parcel " + values[1]);
            }
            for (Payment existing : paymentService.getPayments()) {
                if (existing.getPaymentId().equalsIgnoreCase(values[0])
                        || existing.getTrackingId().equalsIgnoreCase(values[1])) {
                    throw new IllegalStateException("Duplicate payment record in " + PAYMENT_FILE);
                }
            }
            Payment payment;
            double amount = Double.parseDouble(values[4]);
            if (!Double.isFinite(amount) || amount <= 0) {
                throw new IllegalStateException("Invalid payment amount in " + PAYMENT_FILE);
            }
            if (values[2].equalsIgnoreCase("PREPAID")) {
                payment = new PrepaidPayment(values[0], values[1], amount);
            } else if (values[2].equalsIgnoreCase("COD")
                    || values[2].equalsIgnoreCase("CASH_ON_DELIVERY")) {
                payment = new CODPayment(values[0], values[1], amount);
            } else {
                throw new IllegalStateException(
                        "Unknown payment method in " + PAYMENT_FILE + ": " + values[2]);
            }
            payment.setStatus(PaymentStatus.valueOf(values[3]));
            payment.setPaymentDate(DateTimeUtility.parse(values[5]));
            payment.setReceiptNumber(values[6]);
            paymentService.getPayments().add(payment);
        });
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

    private void loadRows(String fileName, int expectedCount, Consumer<String[]> rowLoader) {
        List<String> lines = FileManager.readLines(fileName);
        for (int index = 0; index < lines.size(); index++) {
            try {
                String[] values = lines.get(index).split("\\|", -1);
                if (values.length != expectedCount) {
                    throw new IllegalStateException("Expected " + expectedCount
                            + " fields but found " + values.length + ".");
                }
                rowLoader.accept(values);
            } catch (RuntimeException exception) {
                throw new IllegalStateException(fileName + " line " + (index + 1)
                        + ": " + exception.getMessage(), exception);
            }
        }
    }

    private boolean parseBoolean(String value, String fileName) {
        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
            throw new IllegalStateException("Invalid active flag in " + fileName + ": " + value);
        }
        return Boolean.parseBoolean(value);
    }
}
