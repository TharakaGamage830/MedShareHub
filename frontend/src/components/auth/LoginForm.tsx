import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import {
    Box,
    Button,
    TextField,
    Typography,
    Paper,
    Container,
    Alert,
    CircularProgress
} from '@mui/material';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import api from '../../api/axios';
import { loginStart, loginSuccess, loginFailure, setMfaRequired } from '../../store/authSlice';
import type { RootState } from '../../store';

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
        <Container component="main" maxWidth="xs">
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Paper elevation={3} sx={{ p: 4, display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
                    <Box sx={{ m: 1, bgcolor: 'primary.main', borderRadius: '50%', p: 1 }}>
                        <LockOutlinedIcon sx={{ color: 'white' }} />
                    </Box>
                    <Typography component="h1" variant="h5" sx={{ mb: 3 }}>
                        Sign in to MedShare Hub
                    </Typography>

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
    );
};

export default LoginForm;
