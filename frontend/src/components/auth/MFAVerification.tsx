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
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import api from '../../api/axios';
import { loginSuccess, loginFailure } from '../../store/authSlice';
import type { RootState } from '../../store';

const MFAVerification = () => {
    const [code, setCode] = useState('');
    const [verifying, setVerifying] = useState(false);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { mfaToken, user, error } = useSelector((state: RootState) => state.auth);

    const handleVerify = async (e: React.FormEvent) => {
        e.preventDefault();
        setVerifying(true);

        try {
            // Mock API call for MFA verification
            const response = await api.post('/auth/mfa/verify', {
                mfaToken,
                code
            });

            dispatch(loginSuccess({
                user: user,
                accessToken: response.data.accessToken,
                refreshToken: response.data.refreshToken
            }));

            navigate('/');
        } catch (err: any) {
            const errorMessage = err.response?.data?.message || 'Invalid verification code.';
            dispatch(loginFailure(errorMessage));
        } finally {
            setVerifying(false);
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
                    <Box sx={{ m: 1, bgcolor: 'secondary.main', borderRadius: '50%', p: 1 }}>
                        <VerifiedUserIcon sx={{ color: 'white' }} />
                    </Box>
                    <Typography component="h1" variant="h5">
                        Two-Factor Authentication
                    </Typography>
                    <Typography variant="body2" color="textSecondary" sx={{ mt: 1, textAlign: 'center' }}>
                        Please enter the 6-digit code sent to your registered device.
                    </Typography>

                    {error && (
                        <Alert
                            severity="error"
                            sx={{ width: '100%', mt: 2 }}
                            role="alert"
                            aria-live="assertive"
                        >
                            {error}
                        </Alert>
                    )}

                    <Box
                        component="form"
                        onSubmit={handleVerify}
                        noValidate
                        sx={{ mt: 3, width: '100%' }}
                        aria-label="Two-Factor Authentication Form"
                    >
                        <TextField
                            required
                            fullWidth
                            id="code"
                            label="Verification Code"
                            name="code"
                            autoFocus
                            value={code}
                            onChange={(e) => setCode(e.target.value)}
                            inputProps={{
                                maxLength: 6,
                                style: { textAlign: 'center', letterSpacing: '0.5em', fontSize: '1.5rem' },
                                'aria-label': '6-digit verification code',
                                'aria-required': true
                            }}
                            disabled={verifying}
                        />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2, height: 48 }}
                            disabled={verifying || code.length < 6}
                            aria-busy={verifying}
                            aria-label={verifying ? "Verifying code, please wait" : "Verify Code"}
                        >
                            {verifying ? <CircularProgress size={24} aria-hidden="true" /> : 'Verify'}
                        </Button>
                        <Button
                            fullWidth
                            variant="text"
                            size="small"
                            onClick={() => navigate('/login')}
                            aria-label="Back to login screen"
                        >
                            Back to Login
                        </Button>
                    </Box>
                </Paper>
            </Box>
        </Container>
    );
};

export default MFAVerification;
