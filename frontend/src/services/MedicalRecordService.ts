import api from '../api/axios';

// Interface matching Backend Entity
export interface MedicalRecord {
    recordId: number;
    recordType: string;
    title: string;
    description: string;
    sensitivityLevel: string;
    createdAt: string;
}

export const MedicalRecordService = {
    getPatientRecords: async (
        patientId: number,
        isEmergency: boolean = false,
        justification?: string
    ) => {
        const params: any = {};
        if (isEmergency) {
            params.isEmergency = true;
            params.justification = justification;
        }

        const response = await api.get(`/records/patient/${patientId}`, { params });
        // Handle Spring Data REST pagination structure if present, or direct array
        return response.data.content || response.data;
    },

    getRecordById: async (
        recordId: number,
        isEmergency: boolean = false,
        justification?: string
    ) => {
        const params: any = {};
        if (isEmergency) {
            params.isEmergency = true;
            params.justification = justification;
        }
        const response = await api.get(`/records/${recordId}`, { params });
        return response.data;
    }
};
