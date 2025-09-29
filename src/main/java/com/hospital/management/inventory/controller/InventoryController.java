package com.hospital.management.inventory.controller;

import com.hospital.management.inventory.model.Equipment;
import com.hospital.management.inventory.model.Medicine;
import com.hospital.management.inventory.model.Supplier;
import com.hospital.management.inventory.service.InventoryService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * InventoryController (CLI)
 * -------------------------
 * Minimal console menu aligned with the current InventoryService:
 *  - Add/List Medicines
 *  - Add/List Equipment
 *  - Add/List Suppliers
 */
public class InventoryController {

    private final InventoryService inventoryService;
    private final Scanner scanner = new Scanner(System.in);

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /** Start the interactive menu loop. */
    public void startMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n===== Inventory Management =====");
            System.out.println("1) Add Medicine");
            System.out.println("2) List Medicines");
            System.out.println("3) Add Equipment");
            System.out.println("4) List Equipment");
            System.out.println("5) Add Supplier");
            System.out.println("6) List Suppliers");
            System.out.println("0) Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> addMedicine();
                    case "2" -> listMedicines();
                    case "3" -> addEquipment();
                    case "4" -> listEquipment();
                    case "5" -> addSupplier();
                    case "6" -> listSuppliers();
                    case "0" -> running = false;
                    default -> System.out.println("❌ Invalid choice, try again.");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
            }
        }
    }

    // -------------------- Medicines --------------------

    private void addMedicine() {
        System.out.println("\n-- Add Medicine --");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();

        int qty = readInt("Quantity in stock: ");
        BigDecimal unitPrice = readBigDecimal("Unit price (e.g., 199.50): ");

        Medicine med = new Medicine();
        med.setName(name);
        med.setQuantityInStock(qty);
        med.setUnitPrice(unitPrice);
        med.setStatus("ACTIVE");
        med.setCreatedAt(LocalDateTime.now());
        med.setUpdatedAt(LocalDateTime.now());

        Long id = inventoryService.addMedicine(med);
        System.out.println("✅ Medicine saved" + (id != null ? (" with id=" + id) : "") + ".");
    }

    private void listMedicines() {
        System.out.println("\n-- Medicines --");
        List<Medicine> meds = inventoryService.listAllMedicines();
        if (meds == null || meds.isEmpty()) {
            System.out.println("(none)");
            return;
        }
        meds.forEach(System.out::println);
    }

    // -------------------- Equipment --------------------

    private void addEquipment() {
        System.out.println("\n-- Add Equipment --");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Category (optional): ");
        String category = scanner.nextLine().trim();

        int qty = readInt("Quantity in stock: ");
        BigDecimal unitCost = readBigDecimal("Unit cost (optional; blank to skip): ", true);

        Equipment eq = new Equipment();
        eq.setName(name);
        eq.setCategory(category.isBlank() ? null : category);
        eq.setQuantityInStock(qty);
        eq.setUnitCost(unitCost);
        eq.setStatus("ACTIVE");
        eq.setCreatedAt(LocalDateTime.now());
        eq.setUpdatedAt(LocalDateTime.now());

        Long id = inventoryService.addEquipment(eq);
        System.out.println("✅ Equipment saved" + (id != null ? (" with id=" + id) : "") + ".");
    }

    private void listEquipment() {
        System.out.println("\n-- Equipment --");
        List<Equipment> list = inventoryService.listAllEquipment();
        if (list == null || list.isEmpty()) {
            System.out.println("(none)");
            return;
        }
        list.forEach(System.out::println);
    }

    // -------------------- Suppliers --------------------

    private void addSupplier() {
        System.out.println("\n-- Add Supplier --");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Contact person (optional): ");
        String cp = scanner.nextLine().trim();

        System.out.print("Phone (optional): ");
        String phone = scanner.nextLine().trim();

        System.out.print("Email (optional): ");
        String email = scanner.nextLine().trim();

        System.out.print("Address (optional): ");
        String addr = scanner.nextLine().trim();

        System.out.print("GST Number (optional): ");
        String gst = scanner.nextLine().trim();

        Supplier s = new Supplier();
        s.setName(name);
        s.setContactPerson(cp.isBlank() ? null : cp);
        s.setPhone(phone.isBlank() ? null : phone);
        s.setEmail(email.isBlank() ? null : email);
        s.setAddress(addr.isBlank() ? null : addr);
        s.setGstNumber(gst.isBlank() ? null : gst);
        s.setStatus("ACTIVE");
        s.setCreatedAt(LocalDateTime.now());
        s.setUpdatedAt(LocalDateTime.now());

        Long id = inventoryService.addSupplier(s);
        System.out.println("✅ Supplier saved" + (id != null ? (" with id=" + id) : "") + ".");
    }

    private void listSuppliers() {
        System.out.println("\n-- Suppliers --");
        List<Supplier> list = inventoryService.listAllSuppliers();
        if (list == null || list.isEmpty()) {
            System.out.println("(none)");
            return;
        }
        list.forEach(System.out::println);
    }

    // -------------------- Input helpers --------------------

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private BigDecimal readBigDecimal(String prompt) {
        return readBigDecimal(prompt, false);
    }

    private BigDecimal readBigDecimal(String prompt, boolean allowBlank) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (allowBlank && s.isBlank()) return null;
            try {
                return new BigDecimal(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (e.g., 199.99).");
            }
        }
    }
}
