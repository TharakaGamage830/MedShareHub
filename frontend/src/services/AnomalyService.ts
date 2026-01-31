import api from '../api/axios';

export interface AnomalyAlert {
    id: number;
    patientId: number;
    patientName: string;
    type: string;
    severity: 'LOW' | 'MEDIUM' | 'HIGH';
    description: string;
    timestamp: string;
    status: 'OPEN' | 'RESOLVED' | 'DISMISSED';
}

export const AnomalyService = {
    getAlerts: async (): Promise<AnomalyAlert[]> => {
        const response = await api.get('/compliance/anomalies');
        return response.data;
    },

    resolveAlert: async (alertId: number, resolution: string): Promise<void> => {
        await api.patch(`/compliance/anomalies/${alertId}/resolve`, { resolution });
    },

    dismissAlert: async (alertId: number): Promise<void> => {
        await api.patch(`/compliance/anomalies/${alertId}/dismiss`);
    }
};
