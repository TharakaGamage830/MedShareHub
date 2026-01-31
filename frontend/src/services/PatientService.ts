import api from '../api/axios';

export interface Patient {
    patientId: number;
    firstName: string;
    lastName: string;
    dateOfBirth: string;
    gender: string;
    mrn: string;
    email: string;
    phone: string;
    address: string;
}

export const PatientService = {
    getPatientById: async (id: number) => {
        const response = await api.get(`/patients/${id}`);
        return response.data;
    },

    getPatientByMrn: async (mrn: string) => {
        const response = await api.get(`/patients/mrn/${mrn}`);
        return response.data;
    },

    searchPatients: async (query: string) => {
        const response = await api.get(`/patients/search`, { params: { query } });
        return response.data;
    },

    // Stub for now, could be search with empty query or a dedicated endpoint if added
    getRecentPatients: async () => {
        const response = await api.get(`/patients/search`, { params: { query: '' } });
        return response.data.slice(0, 5); // Just 5 for dashboard
    }
};
