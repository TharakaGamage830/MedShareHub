package com.medshare.hub.abac.attributes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SubjectAttributes - Attributes of the user requesting access
 * 
 * These attributes are used in ABAC policy evaluation to determine
 * if a subject (user) should be granted access to a resource.
 * 
 * Key attributes:
 * - userId: Unique identifier
 * - role: User's role (DOCTOR, PATIENT, etc.)
 * - department: For department-based restrictions
 * - certifications: Professional certifications
 * - emergencyCertified: Can perform break-glass access
 * - location: Physical location (for location-based policies)
 * 
 * @author MedShare Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectAttributes {

    private Long userId;
    private String role;
    private String department;
    private String[] certifications;
    private Boolean emergencyCertified;
    private String employer;
    private String location;

    /**
     * Check if subject has a specific certification
     */
    public boolean hasCertification(String certification) {
        if (certifications == null)
            return false;
        for (String cert : certifications) {
            if (cert.equalsIgnoreCase(certification)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if subject has a specific role
     */
    public boolean hasRole(String roleName) {
        return this.role != null && this.role.equalsIgnoreCase(roleName);
    }
}
