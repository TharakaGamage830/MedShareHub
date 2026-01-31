import api from '../api/axios';

export interface AccessLog {
    logId: number;
    action: 'READ' | 'WRITE' | 'DELETE' | 'EXPORT' | 'LOGIN' | 'LOGOUT';
    resourceType: string;
    decision: 'PERMIT' | 'DENY';
    createdAt: string;
    ipAddress?: string;
    isEmergency?: boolean;
    justification?: string;
    user?: {
        userId: number;
        email: string; // Assuming basic user info is populated
    };
    patient?: {
        patientId: number;
        firstName: string;
        lastName: string;
    };
}

export const AuditService = {
    getPatientAccessLogs: async (patientId: number) => {
        const response = await api.get(`/audit/patient/${patientId}`);
        // Handle Spring Page if necessary
        return response.data.content || response.data;
    },

    getUserAccessLogs: async (userId: number) => {
        const response = await api.get(`/audit/user/${userId}`);
        return response.data.content || response.data;
    }
};
