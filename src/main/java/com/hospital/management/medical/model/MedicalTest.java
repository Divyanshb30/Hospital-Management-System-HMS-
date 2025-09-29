package com.hospital.management.medical.model;

import java.time.LocalDate;

public class MedicalTest {
    private int testId;
    private int medicalRecordId;
    private String testType;
    private String result;
    private LocalDate resultDate;
    private String status;

    public MedicalTest() {}
    public MedicalTest(int medicalRecordId, String type) {
        this.medicalRecordId = medicalRecordId; this.testType = type;
    }
    // getters/setters

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDate getResultDate() {
        return resultDate;
    }

    public void setResultDate(LocalDate resultDate) {
        this.resultDate = resultDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override public String toString() { return testId + " | test:" + testType + " | " + status; }
}
