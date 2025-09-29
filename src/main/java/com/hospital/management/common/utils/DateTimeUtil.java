package com.hospital.management.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

/**
 * Utility class for date and time operations with Java 8 features
 */
public final class DateTimeUtil {

    private DateTimeUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Common date/time formatters
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    /**
     * Get current date as formatted string
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * Get current date and time as formatted string
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    /**
     * Format LocalDate to string
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    /**
     * Format LocalDateTime to string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "";
    }

    /**
     * Format LocalDateTime for display
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_FORMATTER) : "";
    }

    /**
     * Parse date string to LocalDate
     */
    public static LocalDate parseDate(String dateStr) throws DateTimeParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new DateTimeParseException("Date string cannot be empty", dateStr, 0);
        }
        return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
    }

    /**
     * Parse datetime string to LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) throws DateTimeParseException {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            throw new DateTimeParseException("DateTime string cannot be empty", dateTimeStr, 0);
        }
        return LocalDateTime.parse(dateTimeStr.trim(), DATETIME_FORMATTER);
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.equals(LocalDate.now());
    }

    /**
     * Check if datetime is in the future
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Check if datetime is in the past
     */
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Calculate age from birth date
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Get business hours time slots (9 AM to 5 PM, 30-minute intervals)
     */
    public static List<LocalTime> getBusinessHoursTimeSlots() {
        List<LocalTime> timeSlots = new ArrayList<>();
        LocalTime start = LocalTime.of(9, 0);  // 9:00 AM
        LocalTime end = LocalTime.of(17, 0);   // 5:00 PM

        LocalTime current = start;
        while (current.isBefore(end)) {
            timeSlots.add(current);
            current = current.plusMinutes(30);
        }

        return timeSlots;
    }

    /**
     * Check if time is within business hours
     */
    public static boolean isBusinessHours(LocalTime time) {
        return time != null &&
                !time.isBefore(LocalTime.of(9, 0)) &&
                time.isBefore(LocalTime.of(17, 0));
    }

    /**
     * Check if date is a weekday
     */
    public static boolean isWeekday(LocalDate date) {
        if (date == null) return false;
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    /**
     * Get next business day
     */
    public static LocalDate getNextBusinessDay(LocalDate date) {
        if (date == null) return LocalDate.now().plusDays(1);

        LocalDate nextDay = date.plusDays(1);
        while (!isWeekday(nextDay)) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }

    /**
     * Format duration in minutes to readable format
     */
    public static String formatDuration(long minutes) {
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            if (remainingMinutes == 0) {
                return hours + " hour" + (hours > 1 ? "s" : "");
            } else {
                return hours + " hour" + (hours > 1 ? "s" : "") + " " +
                        remainingMinutes + " minutes";
            }
        }
    }
}
