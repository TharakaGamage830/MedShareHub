import api from '../api/axios';

export interface Consent {
    consentId: number;
    patientId: number;
    providerId?: number;
    grantedToOrganization?: string;
    dataType: 'ALL' | 'LAB_RESULTS' | 'PRESCRIPTIONS' | 'VISIT_NOTES' | 'IMAGING' | 'BILLING';
    purpose: 'TREATMENT' | 'INSURANCE' | 'RESEARCH' | 'FAMILY_ACCESS' | 'OTHER';
    status: 'ACTIVE' | 'REVOKED' | 'EXPIRED'; // Derived in frontend or backend need to return it? Backend entity has boolean 'revoked' and 'expiresAt'.
    revoked: boolean;
    expiresAt: string;
}

export const ConsentService = {
    getPatientConsents: async (patientId: number) => {
        const response = await api.get(`/consents/patient/${patientId}`);
        // Transform backend entity to frontend interface if needed
        return response.data;
    },

    createConsent: async (consentData: {
        patientId: number;
        grantedToUserId?: number;
        grantedToOrganization?: string;
        dataType: string;
        purpose: string;
        expiresAt: string;
    }) => {
        const response = await api.post('/consents', consentData);
        return response.data;
    },

    revokeConsent: async (consentId: number, patientId: number) => {
        const response = await api.delete(`/consents/${consentId}`, {
            params: { patientId }
        });
        return response.data;
    }
};
