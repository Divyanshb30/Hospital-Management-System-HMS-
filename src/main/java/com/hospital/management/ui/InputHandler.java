package com.hospital.management.ui;

import java.util.Scanner;
import java.io.Console;

/**
 * Simple input handler for UI layer only
 */
public class InputHandler {
    private static InputHandler instance;
    private final Scanner scanner = new Scanner(System.in);
    private final Console console = System.console();

    private InputHandler() {}

    public static InputHandler getInstance() {
        if (instance == null) {
            instance = new InputHandler();
        }
        return instance;
    }

    public String getString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
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
}

