package com.hospital.management.interfaces;

import com.hospital.management.models.Doctor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<Doctor> getDoctorsByDepartment(Long departmentId);
    Optional<Doctor> findDoctorById(Long doctorId);
    List<LocalTime> getAvailableTimeSlots(Long doctorId, LocalDate appointmentDate);
    boolean isDoctorAvailable(Long doctorId, LocalDate date, LocalTime time);
    Doctor getDoctorById(Long doctorId);
    boolean updateDoctor(Doctor doctor);
    boolean updateDoctorQualification(Long doctorId, String qualification);
    boolean updateConsultationFee(Long doctorId, java.math.BigDecimal consultationFee);

}
