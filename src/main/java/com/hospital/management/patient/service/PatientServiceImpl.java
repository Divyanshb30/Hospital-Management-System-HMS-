package com.hospital.management.patient.service;

import com.hospital.management.patient.dao.PatientDAO;
import com.hospital.management.patient.dao.PatientDAOImpl;
import com.hospital.management.patient.model.Patient;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service implementation for Patient operations.
 * Uses simple lambdas/streams for validation & normalization.
 */
public class PatientServiceImpl implements PatientService {

    private final PatientDAO patientDAO;

    // --- Basic validators using lambdas ---
    private final Predicate<String> nonBlank = s -> s != null && !s.isBlank();
    private final Predicate<String> validPhone = p -> nonBlank.test(p) && p.replaceAll("\\D", "").length() >= 10;
    private final Predicate<String> validEmail = e -> nonBlank.test(e) && e.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private final Predicate<LocalDate> validDob = d -> d == null || !d.isAfter(LocalDate.now());

    public PatientServiceImpl(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    /** Convenience ctor (simple default wiring). Replace with DI/factory later. */
    public PatientServiceImpl() {
        this(new PatientDAOImpl());
    }

    @Override
    public Long registerPatient(Patient patient) {
        Objects.requireNonNull(patient, "patient must not be null");

        // Normalize strings (trim) via small lambda
        trimAllStrings(patient);

        // Minimal business validation
        if (!nonBlank.test(patient.getFirstName())) {
            throw new IllegalArgumentException("First name is required");
        }
        if (!nonBlank.test(patient.getLastName())) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (!validDob.test(patient.getDateOfBirth())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
        if (patient.getPhone() != null && !validPhone.test(patient.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if (patient.getEmail() != null && !validEmail.test(patient.getEmail())) {
            throw new IllegalArgumentException("Invalid email");
        }

        // Default status if missing
        if (!nonBlank.test(patient.getStatus())) {
            patient.setStatus("ACTIVE");
        }

        return patientDAO.insert(patient);
    }

    @Override
    public Optional<Patient> getPatientById(Long id) {
        if (id == null) return Optional.empty();
        return patientDAO.findById(id);
    }

    @Override
    public List<Patient> getAllPatients() {
        // Example stream: sort by lastName, then firstName (null-safe)
        return patientDAO.findAll().stream()
                .sorted(Comparator
                        .comparing((Patient p) -> safeLower(p.getLastName()))
                        .thenComparing(p -> safeLower(p.getFirstName())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> searchPatients(String query) {
        if (query == null) query = "";
        final String q = query.trim();
        // Delegate to DAO search; post-filter via stream (defensive)
        return patientDAO.search(q).stream()
                .filter(distinctById()) // ensure no duplicates
                .collect(Collectors.toList());
    }

    @Override
    public boolean updatePatient(Patient patient) {
        Objects.requireNonNull(patient, "patient must not be null");
        if (patient.getId() == null) {
            throw new IllegalArgumentException("Patient id is required for update");
        }

        trimAllStrings(patient);

        if (patient.getPhone() != null && !validPhone.test(patient.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if (patient.getEmail() != null && !validEmail.test(patient.getEmail())) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (!validDob.test(patient.getDateOfBirth())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }

        return patientDAO.update(patient);
    }

    @Override
    public boolean dischargePatient(Long id) {
        return getPatientById(id)
                .map(p -> {
                    p.setStatus("DISCHARGED");
                    return patientDAO.update(p);
                })
                .orElse(false);
    }

    @Override
    public boolean deletePatient(Long id) {
        if (id == null) return false;
        // Example rule: don’t delete if already discharged in last X days (stub for future)
        return patientDAO.deleteById(id);
    }

    @Override
    public long getTotalPatients() {
        return patientDAO.count();
    }

    // --- Helpers ---

    /** Trim all string fields with small mapping lambdas. */
    private void trimAllStrings(Patient p) {
        if (p.getFirstName() != null) p.setFirstName(p.getFirstName().trim());
        if (p.getLastName() != null) p.setLastName(p.getLastName().trim());
        if (p.getPhone() != null) p.setPhone(p.getPhone().trim());
        if (p.getEmail() != null) p.setEmail(p.getEmail().trim().toLowerCase());
        if (p.getAddress() != null) p.setAddress(p.getAddress().trim());
        if (p.getBloodGroup() != null) p.setBloodGroup(p.getBloodGroup().trim().toUpperCase());
        if (p.getStatus() != null) p.setStatus(p.getStatus().trim().toUpperCase());
        if (p.getGender() != null) p.setGender(p.getGender().trim().toUpperCase());
    }

    /** Distinct-by-id predicate using a concurrent set; handy in streams. */
    private Predicate<Patient> distinctById() {
        Set<Long> seen = Collections.synchronizedSet(new HashSet<>());
        return p -> {
            Long key = p.getId();
            if (key == null) return true; // keep transient objects
            return seen.add(key);
        };
    }

    private String safeLower(String s) {
        return s == null ? "" : s.toLowerCase();
    }
}