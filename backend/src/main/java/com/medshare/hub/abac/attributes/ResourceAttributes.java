package com.medshare.hub.abac.attributes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ResourceAttributes - Attributes of the resource being accessed
 * 
 * These attributes describe the medical record or other resource
 * that the user is trying to access.
 * 
 * Key attributes:
 * - resourceId: Unique identifier of the resource
 * - resourceType: Type of resource (MEDICAL_RECORD, PATIENT, etc.)
 * - patientId: Which patient the resource belongs to
 * - sensitivityLevel: Data sensitivity (PUBLIC, STANDARD, PSYCHIATRIC, HIV,
 * CRITICAL)
 * - recordType: Type of medical record (LAB_RESULT, PRESCRIPTION, etc.)
 * - createdBy: User who created the resource
 * 
 * @author MedShare Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAttributes {

    private Long resourceId;
    private String resourceType;
    private Long patientId;
    private String sensitivityLevel;
    private String recordType;
    private Long createdBy;

    /**
     * Check if resource is highly sensitive
     */
    public boolean isHighlySensitive() {
        return "PSYCHIATRIC".equalsIgnoreCase(sensitivityLevel) ||
                "HIV".equalsIgnoreCase(sensitivityLevel) ||
                "CRITICAL".equalsIgnoreCase(sensitivityLevel);
    }

    /**
     * Check if resource has specific sensitivity level
     */
    public boolean hasSensitivityLevel(String level) {
        return this.sensitivityLevel != null && this.sensitivityLevel.equalsIgnoreCase(level);
    }
}
