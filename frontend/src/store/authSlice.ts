import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

interface AuthState {
    user: {
        userId: number;
        email: string;
        role: string;
        department?: string;
    } | null;
    accessToken: string | null;
    isAuthenticated: boolean;
    mfaRequired: boolean;
    mfaToken: string | null;
    isLoading: boolean;
    error: string | null;
}

// Initialize state from local storage
const storedToken = localStorage.getItem('accessToken');
const storedUser = localStorage.getItem('user');

const initialState: AuthState = {
    user: storedUser ? JSON.parse(storedUser) : null,
    accessToken: storedToken,
    isAuthenticated: !!storedToken,
    mfaRequired: false,
    mfaToken: null,
    isLoading: false,
    error: null,
};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        loginStart: (state) => {
            state.isLoading = true;
            state.error = null;
        },
        loginSuccess: (state, action: PayloadAction<{ user: any; accessToken: string; refreshToken: string }>) => {
            state.isLoading = false;
            state.isAuthenticated = true;
            state.mfaRequired = false;
            state.mfaToken = null;
            state.user = action.payload.user;
            state.accessToken = action.payload.accessToken;
            state.error = null;

            // Persist to local storage
            localStorage.setItem('accessToken', action.payload.accessToken);
            localStorage.setItem('refreshToken', action.payload.refreshToken);
            localStorage.setItem('user', JSON.stringify(action.payload.user));
        },
        setMfaRequired: (state, action: PayloadAction<{ user: any; mfaToken: string }>) => {
            state.isLoading = false;
            state.mfaRequired = true;
            state.mfaToken = action.payload.mfaToken;
            state.user = action.payload.user;
            state.error = null;
        },
        loginFailure: (state, action: PayloadAction<string>) => {
            state.isLoading = false;
            state.isAuthenticated = false;
            state.mfaRequired = false;
            state.mfaToken = null;
            state.user = null;
            state.accessToken = null;
            state.error = action.payload;
        },
        logout: (state) => {
            state.user = null;
            state.accessToken = null;
            state.isAuthenticated = false;
            state.mfaRequired = false;
            state.mfaToken = null;
            state.error = null;

            // Clear persistence
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('user');
        },
        updateToken: (state, action: PayloadAction<string>) => {
            state.accessToken = action.payload;
            localStorage.setItem('accessToken', action.payload);
        },
    },
});

export const { loginStart, loginSuccess, loginFailure, logout, updateToken, setMfaRequired } = authSlice.actions;
export default authSlice.reducer;
