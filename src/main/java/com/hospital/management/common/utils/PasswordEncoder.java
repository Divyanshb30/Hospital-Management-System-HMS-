package com.hospital.management.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password encoding and verification
 */
public final class PasswordEncoder {

    private PasswordEncoder() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    /**
     * Generate a random salt
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hash password with salt
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Hash password with generated salt (returns salt:hash)
     */
    public static String encodePassword(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return salt + ":" + hash;
    }

    /**
     * Verify password against encoded password
     */
    public static boolean verifyPassword(String password, String encodedPassword) {
        if (password == null || encodedPassword == null) {
            return false;
        }

        String[] parts = encodedPassword.split(":");
        if (parts.length != 2) {
            return false;
        }

        String salt = parts[0];
        String expectedHash = parts[1];
        String actualHash = hashPassword(password, salt);

        return expectedHash.equals(actualHash);
    }

    /**
     * Generate a random password
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length should be at least 8 characters");
        }

        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*";
        String allChars = uppercase + lowercase + digits + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password
        for (int i = 0; i < password.length(); i++) {
            int randomIndex = random.nextInt(password.length());
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(randomIndex));
            password.setCharAt(randomIndex, temp);
        }

        return password.toString();
    }
}
