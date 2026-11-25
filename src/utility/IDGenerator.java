package utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDGenerator {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    private static final Pattern TRAILING_NUMBER = Pattern.compile("(\\d+)$");

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

    public static void reserve(String identifier) {
        if (identifier == null) {
            return;
        }
        Matcher matcher = TRAILING_NUMBER.matcher(identifier);
        if (matcher.find()) {
            int next = Integer.parseInt(matcher.group(1)) + 1;
            COUNTER.updateAndGet(current -> Math.max(current, next));
        }
    }
}
