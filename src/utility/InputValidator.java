package utility;

import java.util.Scanner;

public class InputValidator {
    private InputValidator() {
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    public static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    public static String readRequiredString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    public static String validateStoredText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        if (value.indexOf('|') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
            throw new IllegalArgumentException(fieldName
                    + " cannot contain | or line breaks.");
        }
        return value.trim();
    }
}
