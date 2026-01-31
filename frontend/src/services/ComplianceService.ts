import api from '../api/axios';

export type ReportType = 'ACCESS_SUMMARY' | 'ANOMALY_REPORT' | 'CONSENT_AUDIT';

export const ComplianceService = {
    generateReport: async (type: ReportType, patientId?: number) => {
        const response = await api.post('/compliance/reports/generate', { type, patientId });
        return response.data;
    },

    downloadReport: async (reportId: string) => {
        const response = await api.get(`/compliance/reports/download/${reportId}`, { responseType: 'blob' });
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `report_${reportId}.pdf`);
        document.body.appendChild(link);
        link.click();
        link.remove();
    }
};
