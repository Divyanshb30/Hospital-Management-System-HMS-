package com.hospital.management.ui;

import com.hospital.management.common.utils.InputValidator;
import com.hospital.management.common.exceptions.ValidationException;
import java.io.Console;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Enhanced InputHandler with comprehensive validation and password masking
 */
public class InputHandler {
    private static InputHandler instance;
    private final Scanner scanner = new Scanner(System.in);
    private final Console console = System.console();

    private InputHandler() {}

    public static synchronized InputHandler getInstance() {
        if (instance == null) {
            instance = new InputHandler();
        }
        return instance;
    }

    // ==================== BASIC INPUT METHODS ====================

    public String getString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Secure password input with masking (dots/asterisks)
     * Falls back to regular input if console not available
     */
    public String getPasswordInput(String prompt) {
        System.out.print(prompt);

        if (console != null) {
            // Use console for password masking
            char[] passwordChars = console.readPassword();
            return new String(passwordChars);
        } else {
            // Fallback for IDEs that don't support console
            System.out.print("(Note: Password will be visible) ");
            return scanner.nextLine().trim();
        }
    }

    public int getInt(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("❌ Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }

    // ==================== USERNAME VALIDATION (Test Case 6.1) ====================

    /**
     * Username validation: 6-20 characters, alphanumeric and underscore only
     */
    public String getValidatedUsername(String prompt) {
        while (true) {
            String input = getString(prompt);

            try {
                // Test Case: Length validation (6-20 characters)
                if (input.length() < 6) {
                    System.out.println("❌ Username must be at least 6 characters long");
                    continue;
                }

                if (input.length() > 20) {
                    System.out.println("❌ Username cannot exceed 20 characters");
                    continue;
                }

                // Test Case: Character validation (alphanumeric and underscore only)
                if (!input.matches("^[a-zA-Z0-9_]+$")) {
                    System.out.println("❌ Username can only contain letters, numbers, and underscores");
                    continue;
                }

                return input;

            } catch (Exception e) {
                System.out.println("❌ Invalid username format. Please try again.");
            }
        }
    }

    // ==================== PASSWORD VALIDATION (Test Case 6.1) ====================

    /**
     * Password validation: 8-15 characters, must contain letters, numbers, and special chars
     */
    public String getValidatedPassword(String prompt) {
        while (true) {
            String password = getPasswordInput(prompt);

            try {
                // Test Case: Length validation (8-15 characters)
                if (password.length() < 8) {
                    System.out.println("❌ Password must be at least 8 characters long");
                    continue;
                }

                if (password.length() > 15) {
                    System.out.println("❌ Password cannot exceed 15 characters");
                    continue;
                }

                // Test Case: Must contain letters AND numbers AND special characters
                boolean hasLetter = password.matches(".*[a-zA-Z].*");
                boolean hasDigit = password.matches(".*\\d.*");
                boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

                if (!hasLetter || !hasDigit || !hasSpecial) {
                    System.out.println("❌ Password must contain letters, numbers, and special characters");
                    continue;
                }

                return password;

            } catch (Exception e) {
                System.out.println("❌ Invalid password format. Please try again.");
            }
        }
    }

    // ==================== EMAIL VALIDATION (Test Case 6.1) ====================

    /**
     * Email validation: letters, numbers, underscores, periods, dashes, and @ symbol
     */
    public String getValidatedEmail(String prompt) {
        while (true) {
            String email = getString(prompt);

            try {
                // Test Case: Valid email characters only
                if (!email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    System.out.println("❌ Email can only contain letters, numbers, underscores, periods, dashes, and @ symbol");
                    continue;
                }

                if (InputValidator.isValidEmail(email)) {
                    return email;
                } else {
                    System.out.println("❌ Invalid email format. Please use format: user@domain.com");
                }

            } catch (Exception e) {
                System.out.println("❌ Invalid email format. Please try again.");
            }
        }
    }

    // ==================== NAME VALIDATION ====================

    public String getValidatedName(String prompt) {
        while (true) {
            String name = getString(prompt);
            if (InputValidator.isValidName(name)) {
                return name;
            }
            System.out.println("❌ Name should contain only letters and spaces (2-50 characters)");
        }
    }

    // ==================== PHONE VALIDATION ====================

    public String getValidatedPhone(String prompt) {
        while (true) {
            String phone = getString(prompt);
            if (InputValidator.isValidPhone(phone)) {
                return phone;
            }
            System.out.println("❌ Invalid phone number format. Use 10 digits or international format");
        }
    }

    // ==================== GENERIC VALIDATION ====================

    public String getValidatedInput(String prompt, Predicate<String> validator, String errorMessage) {
        while (true) {
            String input = getString(prompt);
            if (validator.test(input)) {
                return input;
            }
            System.out.println("❌ " + errorMessage);
        }
    }

    // ==================== DATE AND OTHER INPUTS ====================

    public String getOptionalInput(String prompt) {
        String input = getString(prompt);
        return input.isEmpty() ? null : input;
    }

    public LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                String input = getString(prompt);
                LocalDate date = LocalDate.parse(input);

                if (date.isBefore(LocalDate.now())) {
                    System.out.println("❌ Date cannot be in the past");
                    continue;
                }

                return date;
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format. Please use YYYY-MM-DD format.");
            }
        }
    }

    public <E extends Enum<E>> E getEnumInput(String prompt, Class<E> enumClass) {
        while (true) {
            try {
                System.out.println(prompt + " options: " + java.util.Arrays.toString(enumClass.getEnumConstants()));
                String input = getString("Enter " + prompt + ": ").toUpperCase();
                return Enum.valueOf(enumClass, input);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid option. Please choose from the available options.");
            }
        }
    }

    // ==================== VALIDATION TEST METHODS ====================

    /**
     * Test username validation according to test cases
     */
    public boolean testUsernameValidation(String username) {
        // Length: 6-20 characters
        if (username.length() < 6 || username.length() > 20) {
            return false;
        }

        // Characters: alphanumeric and underscore only
        return username.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * Test password validation according to test cases
     */
    public boolean testPasswordValidation(String password) {
        // Length: 8-15 characters
        if (password.length() < 8 || password.length() > 15) {
            return false;
        }

        // Must contain letters AND numbers AND special characters
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        return hasLetter && hasDigit && hasSpecial;
    }

    /**
     * Test email validation according to test cases
     */
    public boolean testEmailValidation(String email) {
        // Valid characters: letters, numbers, underscores, periods, dashes, @ symbol
        return email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
}
