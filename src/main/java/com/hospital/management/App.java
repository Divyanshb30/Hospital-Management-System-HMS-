package com.hospital.management;

import com.hospital.management.doctor.controller.DoctorController;

public class App {
    public static void main(String[] args) {
        System.out.println("Welcome to Hospital Management System!");
        System.out.println("Application is starting... Wait for a while... Testing");
        new DoctorController().doctorMenu();
        
        // Your application logic will go here
    }
}
