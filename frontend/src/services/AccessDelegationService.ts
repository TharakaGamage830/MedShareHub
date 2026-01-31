import api from '../api/axios';

export interface DelegationRequest {
    patientId: number;
    delegateId: number;
    expiryDate: string;
    reason: string;
}

export const AccessDelegationService = {
    delegateAccess: async (request: DelegationRequest) => {
        const response = await api.post('/consents/delegate', request);
        return response.data;
    },

    getActiveDelegations: async (patientId: number) => {
        const response = await api.get(`/consents/delegations?patientId=${patientId}`);
        return response.data;
    },

    revokeDelegation: async (delegationId: number) => {
        await api.delete(`/consents/delegations/${delegationId}`);
    }
};
