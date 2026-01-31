import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import {
    Box,
    Button,
    TextField,
    Paper,
    Container,
    Alert,
    CircularProgress
} from '@mui/material';
import api from '../../api/axios';
import { loginStart, loginSuccess, loginFailure, setMfaRequired } from '../../store/authSlice';
import type { RootState } from '../../store';
import BrandText from '../BrandText';

const LoginForm = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { isLoading, error } = useSelector((state: RootState) => state.auth);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        dispatch(loginStart());

        try {
            const response = await api.post('/auth/login', { email, password });

            // Simulation of MFA requirement
            if (response.data.mfaRequired || email.includes('mfa')) {
                dispatch(setMfaRequired({
                    user: {
                        userId: Number(response.data.userId || 1),
                        email,
                        role: response.data.role || 'DOCTOR'
                    },
                    mfaToken: response.data.mfaToken || 'mock-mfa-token'
                }));
                navigate('/mfa-verify');
                return;
            }

            dispatch(loginSuccess({
                user: {
                    userId: Number(response.data.userId),
                    email,
                    role: response.data.role
                },
                accessToken: response.data.accessToken,
                refreshToken: response.data.refreshToken
            }));

            navigate('/');
        } catch (err: any) {
            const errorMessage = err.response?.data?.message || 'Login failed. Please check your credentials.';
            dispatch(loginFailure(errorMessage));
        }
    };

    return (
        <Box
            sx={{
                minHeight: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                bgcolor: 'background.default'
            }}
        >
            <Container component="main" maxWidth="xs">
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Paper
                        elevation={6}
                        sx={{
                            p: 4,
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                            width: '100%',
                            borderRadius: 3,
                            borderTop: '5px solid',
                            borderColor: 'primary.main'
                        }}
                    >
                        <Box sx={{ mb: 2, display: 'flex', justifyContent: 'center' }}>
                            <Box
                                component="img"
                                src="/MedShare Hub.png"
                                sx={{ height: 64, borderRadius: 2 }}
                                alt="MedShare Hub Logo"
                            />
                        </Box>
                        <BrandText variant="h4" sx={{ mb: 4 }} />

                        {error && (
                            <Alert
                                severity="error"
                                sx={{ width: '100%', mb: 2 }}
                                role="alert"
                                aria-live="assertive"
                            >
                                {error}
                            </Alert>
                        )}

                        <Box
                            component="form"
                            onSubmit={handleSubmit}
                            noValidate
                            sx={{ mt: 1, width: '100%' }}
                            aria-label="MedShare Hub Login Form"
                        >
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                id="email"
                                label="Email Address"
                                name="email"
                                autoComplete="email"
                                autoFocus
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                disabled={isLoading}
                                inputProps={{
                                    'aria-required': true,
                                }}
                            />
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                name="password"
                                label="Password"
                                type="password"
                                id="password"
                                autoComplete="current-password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                disabled={isLoading}
                                inputProps={{
                                    'aria-required': true,
                                }}
                            />
                            <Button
                                type="submit"
                                fullWidth
                                variant="contained"
                                sx={{ mt: 3, mb: 2, height: 48 }}
                                disabled={isLoading}
                                aria-busy={isLoading}
                                aria-label={isLoading ? "Logging in, please wait" : "Sign In"}
                            >
                                {isLoading ? <CircularProgress size={24} aria-hidden="true" /> : 'Sign In'}
                            </Button>
                        </Box>
                    </Paper>
                </Box>
            </Container>
        </Box>
    );
};

export default LoginForm;
