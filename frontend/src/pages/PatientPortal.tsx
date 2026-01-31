import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import {
    Box,
    Container,
    Typography,
    Paper,
    Tabs,
    Tab,
    Grid,
    Card,
    CardContent,
    Button
} from '@mui/material';
import DownloadIcon from '@mui/icons-material/Download';
import type { RootState } from '../store';
import ConsentManager from '../components/consent/ConsentManager';
import AuditLogViewer from '../components/audit/AuditLogViewer';
import { DownloadService } from '../services/DownloadService';
import { MedicalRecordService } from '../services/MedicalRecordService';

const PatientPortal = () => {
    const { user } = useSelector((state: RootState) => state.auth);
    const [activeTab, setActiveTab] = useState(0);

    const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
        setActiveTab(newValue);
    };

    // If user is not strictly a patient, we might need logic to handle that or redirect.
    // For now, assume the user.userId maps to patientId for portal context.
    const patientId = user?.userId || 0;

    return (
        <Container maxWidth="lg" sx={{ mt: 4 }}>
            <Typography variant="h4" gutterBottom fontWeight="bold">
                Patient Portal
            </Typography>
            <Typography variant="subtitle1" color="textSecondary" paragraph>
                Welcome back, {user?.email}
            </Typography>

            <Paper sx={{ mb: 3 }}>
                <Tabs value={activeTab} onChange={handleTabChange} centered>
                    <Tab label="Dashboard" />
                    <Tab label="My Records" />
                    <Tab label="Consent Management" />
                    <Tab label="Access Log" />
                </Tabs>
            </Paper>

            {/* Dashboard Tab */}
            {activeTab === 0 && (
                <Box mt={3}>
                    <Grid container spacing={3}>
                        <Grid size={{ xs: 12, md: 8 }}>
                            <Paper sx={{ p: 3, mb: 3 }}>
                                <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                                    <Typography variant="h6">Health Summary</Typography>
                                    <Button
                                        variant="outlined"
                                        startIcon={<DownloadIcon />}
                                        onClick={async () => {
                                            if (user?.userId) {
                                                try {
                                                    const records = await MedicalRecordService.getPatientRecords(user.userId);
                                                    DownloadService.downloadAsJSON(`medshare_records_${user.userId}`, records);
                                                } catch (err) {
                                                    console.error('Failed to export records', err);
                                                }
                                            }
                                        }}
                                    >
                                        Export Records
                                    </Button>
                                </Box>
                                <Typography variant="body1">
                                    Your health records are up to date.
                                    You have 3 new lab results and 1 upcoming appointment.
                                </Typography>
                            </Paper>
                        </Grid>
                        <Grid size={{ xs: 12, md: 4 }}>
                            <Card>
                                <CardContent>
                                    <Typography variant="h6" gutterBottom>Upcoming Appointments</Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        No upcoming appointments.
                                    </Typography>
                                    <Button sx={{ mt: 2 }} variant="outlined" size="small" fullWidth>
                                        Schedule Appointment
                                    </Button>
                                </CardContent>
                            </Card>
                        </Grid>
                    </Grid>
                </Box>
            )}

            {/* Records Tab */}
            {activeTab === 1 && (
                <Box>
                    <Typography variant="h6">My Medical Records</Typography>
                    <Typography color="textSecondary">
                        Redirecting to your full medical record view... (Placeholder)
                    </Typography>
                </Box>
            )}

            {/* Consent Management Tab */}
            {activeTab === 2 && (
                <ConsentManager patientId={patientId} />
            )}

            {/* Access Log Tab */}
            {activeTab === 3 && (
                <Box mt={2}>
                    <AuditLogViewer patientId={patientId} />
                </Box>
            )}
        </Container>
    );
};

export default PatientPortal;
