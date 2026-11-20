package utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private IDGenerator() {
    }

    public static String generateTrackingId() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "TRK-" + datePart + "-" + String.format("%04d", COUNTER.getAndIncrement());
    }

    public static String generateUserId(String role) {
        return role.substring(0, 1).toUpperCase() + "-" +
                String.format("%04d", COUNTER.getAndIncrement());
    }

    public static String generateBranchId() {
        return "BR-" + String.format("%03d", COUNTER.getAndIncrement());
    }

    public static String generatePaymentId() {
        return "PAY-" + String.format("%05d", COUNTER.getAndIncrement());
    }
}
