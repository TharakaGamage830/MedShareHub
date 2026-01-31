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
import AnomalyAlerts from '../components/audit/AnomalyAlerts';
import MessageInbox from '../components/messages/MessageInbox';
import NewMessageDialog from '../components/messages/NewMessageDialog';
import SendIcon from '@mui/icons-material/Send';
import { DownloadService } from '../services/DownloadService';
import { MedicalRecordService } from '../services/MedicalRecordService';

const PatientPortal = () => {
    const { user } = useSelector((state: RootState) => state.auth);
    const [activeTab, setActiveTab] = useState(0);
    const [messageDialogOpen, setMessageDialogOpen] = useState(false);

    const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
        setActiveTab(newValue);
    };

    // If user is not strictly a patient, we might need logic to handle that or redirect.
    // For now, assume the user.userId maps to patientId for portal context.
    const patientId = user?.userId || 0;

    return (
        <Container maxWidth="lg" sx={{ mt: 4 }} component="main">
            <Typography variant="h4" gutterBottom fontWeight="bold" component="h1">
                Patient Portal
            </Typography>
            <Typography variant="subtitle1" color="textSecondary" paragraph component="p">
                Welcome back, {user?.email}
            </Typography>

            <Paper sx={{ mb: 3 }} component="nav" aria-label="Portal Sections">
                <Tabs
                    value={activeTab}
                    onChange={handleTabChange}
                    centered
                    aria-label="Patient portal content tabs"
                >
                    <Tab label="Dashboard" id="portal-tab-0" aria-controls="portal-panel-0" />
                    <Tab label="My Records" id="portal-tab-1" aria-controls="portal-panel-1" />
                    <Tab label="Consent Management" id="portal-tab-2" aria-controls="portal-panel-2" />
                    <Tab label="Message Center" id="portal-tab-3" aria-controls="portal-panel-3" />
                    <Tab label="Access Log" id="portal-tab-4" aria-controls="portal-panel-4" />
                </Tabs>
            </Paper>

            {/* Dashboard Tab */}
            {activeTab === 0 && (
                <Box
                    mt={3}
                    role="tabpanel"
                    id="portal-panel-0"
                    aria-labelledby="portal-tab-0"
                >
                    <Grid container spacing={3}>
                        <Grid size={{ xs: 12, md: 8 }}>
                            <Paper sx={{ p: 3, mb: 3 }} component="section" aria-labelledby="summary-title">
                                <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                                    <Typography id="summary-title" variant="h6">Health Summary</Typography>
                                    <Button
                                        variant="outlined"
                                        startIcon={<DownloadIcon aria-hidden="true" />}
                                        aria-label="Export my health records as JSON"
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
                            <Box mb={3} component="section" aria-label="Security Alerts">
                                <AnomalyAlerts />
                            </Box>
                            <Card component="section" aria-labelledby="appointments-title">
                                <CardContent>
                                    <Typography id="appointments-title" variant="h6" gutterBottom>Upcoming Appointments</Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        No upcoming appointments.
                                    </Typography>
                                    <Button
                                        sx={{ mt: 2 }}
                                        variant="outlined"
                                        size="small"
                                        fullWidth
                                        aria-label="Schedule a new appointment"
                                    >
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
                <Box
                    role="tabpanel"
                    id="portal-panel-1"
                    aria-labelledby="portal-tab-1"
                >
                    <Typography variant="h6" component="h2">My Medical Records</Typography>
                    <Typography color="textSecondary">
                        Redirecting to your full medical record view... (Placeholder)
                    </Typography>
                </Box>
            )}

            {/* Consent Management Tab */}
            {activeTab === 2 && (
                <Box
                    role="tabpanel"
                    id="portal-panel-2"
                    aria-labelledby="portal-tab-2"
                >
                    <ConsentManager patientId={patientId} />
                </Box>
            )}

            {/* Message Center Tab */}
            {activeTab === 3 && (
                <Box
                    mt={2}
                    role="tabpanel"
                    id="portal-panel-3"
                    aria-labelledby="portal-tab-3"
                >
                    <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                        <Typography variant="h6" component="h2">Your Inbox</Typography>
                        <Button
                            variant="contained"
                            startIcon={<SendIcon aria-hidden="true" />}
                            onClick={() => setMessageDialogOpen(true)}
                            aria-label="Compose a new clinical message"
                        >
                            New Message
                        </Button>
                    </Box>
                    <MessageInbox />
                    <NewMessageDialog
                        open={messageDialogOpen}
                        onClose={() => setMessageDialogOpen(false)}
                    />
                </Box>
            )}

            {/* Access Log Tab */}
            {activeTab === 4 && (
                <Box
                    mt={2}
                    role="tabpanel"
                    id="portal-panel-4"
                    aria-labelledby="portal-tab-4"
                >
                    <AuditLogViewer patientId={patientId} />
                </Box>
            )}

            <Box mt={8} pt={4} borderTop="1px solid" borderColor="divider">
                <Typography variant="caption" color="textSecondary" align="center" display="block">
                    PRIVACY NOTICE: Your data is protected by industry-standard encryption and ABAC access controls.
                    You have the right to view your access logs and manage your data consents at any time.
                </Typography>
            </Box>
        </Container>
    );
};

export default PatientPortal;
