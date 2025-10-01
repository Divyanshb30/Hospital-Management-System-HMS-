package com.hospital.management.common;

import com.hospital.management.common.utils.PasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        String password = "Doc10@123"; // Change this to your desired password
        String encodedPassword = PasswordEncoder.encodePassword(password);

        System.out.println("Password: " + password);
        System.out.println("Encoded: " + encodedPassword);

        // Test verification
        boolean isValid = PasswordEncoder.verifyPassword(password, encodedPassword);
        System.out.println("Verification: " + isValid);
    }
}
