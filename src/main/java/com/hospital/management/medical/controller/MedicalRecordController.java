package com.hospital.management.medical.controller;

import com.hospital.management.medical.model.MedicalRecord;
import com.hospital.management.medical.service.MedicalRecordService;
import com.hospital.management.medical.service.MedicalRecordServiceImpl;

import java.util.List;
import java.util.Scanner;

public class MedicalRecordController {
    private final MedicalRecordService service=new MedicalRecordServiceImpl();
    private final Scanner sc=new Scanner(System.in);

    public void menu(){
        while(true){
            System.out.println("\n=== Medical Records ===");
            System.out.println("1. Create Record");
            System.out.println("2. View Records by Patient");
            System.out.println("3. View All Records");
            System.out.println("4. Update Record Notes");
            System.out.println("5. Delete Record");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            int ch=Integer.parseInt(sc.nextLine());
            switch(ch){
                case 1->create();
                case 2->viewByPatient();
                case 3->viewAll();
                case 4->update();
                case 5->delete();
                case 0-> {return;}
                default->System.out.println("Invalid");
            }
        }
    }
    private void create(){
        System.out.print("Patient ID: ");int pid=Integer.parseInt(sc.nextLine());
        System.out.print("Doctor ID (or blank): ");String d=sc.nextLine();
        Integer did=d.isBlank()?null:Integer.valueOf(d);
        System.out.print("Chief complaint: ");String cc=sc.nextLine();
        System.out.print("Notes: ");String notes=sc.nextLine();
        MedicalRecord r=new MedicalRecord(pid,did,cc,notes);
        int id=service.create(r);
        System.out.println("Created MedicalRecord id:"+id);
    }
    private void viewByPatient(){
        System.out.print("Patient ID: ");int pid=Integer.parseInt(sc.nextLine());
        List<MedicalRecord>list=service.getByPatient(pid);
        list.forEach(System.out::println);
    }
    private void viewAll(){service.getAll().forEach(System.out::println);}
    private void update(){
        System.out.print("MedicalRecord ID: ");int id=Integer.parseInt(sc.nextLine());
        MedicalRecord r=service.get(id);
        if(r==null){System.out.println("Not found");return;}
        System.out.println("Current notes:"+r.getNotes());
        System.out.print("New notes: ");String notes=sc.nextLine();
        r.setNotes(notes);service.update(r);System.out.println("Updated.");
    }
    private void delete(){
        System.out.print("MedicalRecord ID to delete: ");int id=Integer.parseInt(sc.nextLine());
        service.delete(id);System.out.println("Deleted if existed.");
    }
}
