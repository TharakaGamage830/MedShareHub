package com.medshare.hub.repository;

import com.medshare.hub.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PatientRepository - Data access for Patient entities
 * 
 * Provides queries for patient lookup by various identifiers.
 * 
 * @author MedShare Development Team
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find patient by Medical Record Number (MRN)
     * Primary identifier for patient lookup
     */
    Optional<Patient> findByMrn(String mrn);

    /**
     * Check if MRN already exists (for registration)
     */
    boolean existsByMrn(String mrn);

    /**
     * Find patient by user ID (if patient has portal access)
     */
    Optional<Patient> findByUser_UserId(Long userId);

    /**
     * Search patients by name (partial match)
     */
    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    java.util.List<Patient> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Find patients by email
     */
    Optional<Patient> findByEmail(String email);
}
