package com.medshare.hub.repository;

import com.medshare.hub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - Data access for User entities
 * 
 * Provides queries for authentication and ABAC attribute retrieval.
 * 
 * @author MedShare Development Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email for authentication
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email already exists (for registration)
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role (for admin queries)
     */
    @Query("SELECT u FROM User u WHERE u.role = :role")
    java.util.List<User> findByRole(@Param("role") User.UserRole role);

    /**
     * Find users by department (for ABAC queries)
     */
    @Query("SELECT u FROM User u WHERE u.department = :department")
    java.util.List<User> findByDepartment(@Param("department") String department);

    /**
     * Find emergency-certified users (for break-glass access)
     */
    @Query("SELECT u FROM User u WHERE u.emergencyCertified = true")
    java.util.List<User> findEmergencyCertifiedUsers();
}
