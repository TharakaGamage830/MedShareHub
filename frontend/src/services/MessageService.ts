import api from '../api/axios';

export interface Message {
    id: number;
    senderId: number;
    senderName: string;
    recipientId: number;
    recipientName: string;
    subject: string;
    body: string;
    createdAt: string;
    isRead: boolean;
}

export const MessageService = {
    getInbox: async (): Promise<Message[]> => {
        const response = await api.get('/messages/inbox');
        return response.data;
    },

    getSentMessages: async (): Promise<Message[]> => {
        const response = await api.get('/messages/sent');
        return response.data;
    },

    sendMessage: async (recipientId: number, subject: string, body: string): Promise<Message> => {
        const response = await api.post('/messages', { recipientId, subject, body });
        return response.data;
    },

    getThread: async (messageId: number): Promise<Message[]> => {
        const response = await api.get(`/messages/thread/${messageId}`);
        return response.data;
    },

    markAsRead: async (messageId: number): Promise<void> => {
        await api.patch(`/messages/${messageId}/read`);
    }
};
