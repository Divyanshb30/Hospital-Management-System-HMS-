package com.hospital.management.doctor.controller;

import com.hospital.management.doctor.model.*;
import com.hospital.management.doctor.service.DoctorService;
import com.hospital.management.doctor.service.DoctorServiceImpl;

import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class DoctorController {

    private final DoctorService service = new DoctorServiceImpl();
    private final Scanner sc = new Scanner(System.in);

    public void doctorMenu() {
        while (true) {
            System.out.println("\n=== Doctor Management ===");
            System.out.println("1. Add Doctor");
            System.out.println("2. View All Doctors");
            System.out.println("3. Update Doctor");
            System.out.println("4. Delete Doctor");
            System.out.println("5. Specializations Menu");
            System.out.println("6. Schedules Menu");
            System.out.println("7. Stats Menu");
            System.out.println("8. Back");
            System.out.print("Choice: ");
            int ch = Integer.parseInt(sc.nextLine());
            switch (ch) {
                case 1 -> addDoctor();
                case 2 -> viewDoctors();
                case 3 -> updateDoctor();
                case 4 -> deleteDoctor();
                case 5 -> specializationsMenu();
                case 6 -> schedulesMenu();
                case 7 -> statsMenu();
                case 8 -> { return; }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void addDoctor() {
        System.out.print("Name: "); String name = sc.nextLine();
        System.out.print("Specialization ID (or press enter): "); String sid = sc.nextLine();
        Integer specId = sid.isBlank() ? null : Integer.valueOf(sid);
        System.out.print("Contact: "); String contact = sc.nextLine();
        System.out.print("Department: "); String dept = sc.nextLine();
        Doctor d = new Doctor(name, specId, contact, dept);
        int id = service.createDoctor(d);
        System.out.println("Doctor created with id: " + id);
    }

    private void viewDoctors() {
        List<Doctor> list = service.getAllDoctors();
        if (list.isEmpty()) System.out.println("No doctors.");
        else list.forEach(System.out::println);
    }

    private void updateDoctor() {
        System.out.print("Doctor ID: "); int id = Integer.parseInt(sc.nextLine());
        Doctor d = service.getDoctor(id);
        if (d == null) { System.out.println("Not found"); return; }
        System.out.print("Name (" + d.getName() + "): "); String name = sc.nextLine(); if(!name.isBlank()) d.setName(name);
        System.out.print("SpecId (" + d.getSpecializationId() + "): "); String sid = sc.nextLine(); if(!sid.isBlank()) d.setSpecializationId(Integer.valueOf(sid));
        System.out.print("Contact (" + d.getContact() + "): "); String contact = sc.nextLine(); if(!contact.isBlank()) d.setContact(contact);
        System.out.print("Department (" + d.getDepartment() + "): "); String dept = sc.nextLine(); if(!dept.isBlank()) d.setDepartment(dept);
        service.updateDoctor(d);
        System.out.println("Updated.");
    }

    private void deleteDoctor() {
        System.out.print("Doctor ID to delete: "); int id = Integer.parseInt(sc.nextLine());
        service.deleteDoctor(id);
        System.out.println("Deleted if existed.");
    }

    private void specializationsMenu() {
        while (true) {
            System.out.println("\n--- Specializations ---");
            System.out.println("1. Add Specialization");
            System.out.println("2. List Specializations");
            System.out.println("3. Delete Specialization");
            System.out.println("4. Back");
            System.out.print("Choice: ");
            int ch = Integer.parseInt(sc.nextLine());
            switch (ch) {
                case 1 -> {
                    System.out.print("Name: "); String name = sc.nextLine();
                    System.out.print("Description: "); String desc = sc.nextLine();
                    int id = service.addSpecialization(name, desc);
                    System.out.println("Added specialization id: " + id);
                }
                case 2 -> {
                    List<DoctorSpecialization> list = service.listSpecializations();
                    list.forEach(System.out::println);
                }
                case 3 -> {
                    System.out.print("Specialization ID to delete: ");
                    int id = Integer.parseInt(sc.nextLine());
                    if(service.deleteSpecialization(id)) System.out.println("Deleted.");
                    else System.out.println("Specialization not found with id: " + id);

                }
                case 4 -> { return; }
                default -> System.out.println("Invalid");
            }
        }
    }

    private void schedulesMenu() {
        while (true) {
            System.out.println("\n--- Schedules ---");
            System.out.println("1. Add Schedule");
            System.out.println("2. View Schedules for Doctor");
            System.out.println("3. Delete Schedule");
            System.out.println("4. Back");
            System.out.print("Choice: ");
            int ch = Integer.parseInt(sc.nextLine());
            switch (ch) {
                case 1 -> {
                    System.out.print("Doctor ID: "); int did = Integer.parseInt(sc.nextLine());
                    System.out.print("Day (e.g. Monday): "); String day = sc.nextLine();
                    System.out.print("Start (HH:mm): "); LocalTime st = LocalTime.parse(sc.nextLine());
                    System.out.print("End (HH:mm): "); LocalTime et = LocalTime.parse(sc.nextLine());
                    DoctorSchedule s = new DoctorSchedule(did, day, st, et);
                    int id = service.addSchedule(s);
                    System.out.println("Added schedule id: " + id);
                }
                case 2 -> {
                    System.out.print("Doctor ID: "); int did = Integer.parseInt(sc.nextLine());
                    List<DoctorSchedule> list = service.getSchedulesForDoctor(did);
                    if (list.isEmpty()) System.out.println("No schedule");
                    else for (DoctorSchedule ds : list) System.out.println(ds);
                }
                case 3 -> {
                    System.out.print("Schedule ID to delete: ");
                    int sid = Integer.parseInt(sc.nextLine());
                    service.deleteSchedule(sid);
                    System.out.println("Deleted.");
                }
                case 4 -> { return; }
                default -> System.out.println("Invalid");
            }
        }
    }

    private void statsMenu() {
        System.out.print("Doctor ID for stats: "); int id = Integer.parseInt(sc.nextLine());
        DoctorStats stats = service.computeStats(id);
        System.out.println(stats);
        System.out.print("Store upsert to doctor_stats table? (y/n): ");
        if ("y".equalsIgnoreCase(sc.nextLine())) {
            service.refreshAndStoreStats(id);
            System.out.println("Stats refreshed & stored.");
        }
    }
}
