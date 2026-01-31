import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import {
    Box,
    Container,
    Typography,
    Paper,
    Tabs,
    Tab,
    CircularProgress,
    Alert,
    Avatar,
    Grid,
    Chip,
    Button
} from '@mui/material';
import PersonIcon from '@mui/icons-material/Person';
import WarningIcon from '@mui/icons-material/Warning';
import api from '../../api/axios';
import { MedicalRecordService, type MedicalRecord } from '../../services/MedicalRecordService';
import BreakGlassDialog from '../../components/emergency/BreakGlassDialog';
import AuditLogViewer from '../../components/audit/AuditLogViewer';
import LabResultsViewer from '../../components/records/LabResultsViewer';
import MedicationList from '../../components/records/MedicationList';

interface Patient {
    id: number;
    firstName: string;
    lastName: string;
    dateOfBirth: string;
    gender: string;
    mrn: string;
}

const MedicalRecordView = () => {
    const { patientId } = useParams<{ patientId: string }>();
    const [activeTab, setActiveTab] = useState(0);
    const [patient, setPatient] = useState<Patient | null>(null);
    const [records, setRecords] = useState<MedicalRecord[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [isAccessDenied, setIsAccessDenied] = useState(false);
    const [showBreakGlass, setShowBreakGlass] = useState(false);
    const [emergencyJustification, setEmergencyJustification] = useState<string | null>(null);

    const fetchRecords = async (isEmergency: boolean = false, justification?: string) => {
        if (!patientId) return;
        try {
            const data = await MedicalRecordService.getPatientRecords(Number(patientId), isEmergency, justification);
            setRecords(data);
            setIsAccessDenied(false); // Clear denied state on success
            setError(null);
        } catch (err: any) {
            console.error(err);
            if (err.response && err.response.status === 403) {
                setIsAccessDenied(true);
                setError('Access Denied. You do not have permission to view these records.');
            } else {
                setError('Failed to load medical records.');
            }
        }
    };

    useEffect(() => {
        const fetchPatientAndRecords = async () => {
            if (!patientId) return;
            try {
                setLoading(true);
                // Fetch patient details (Assuming basic access allowed or handled separately)
                const patientResponse = await api.get(`/patients/${patientId}`);
                setPatient(patientResponse.data);

                // Fetch records
                await fetchRecords();

            } catch (err: any) {
                console.error(err);
                setError('Failed to load patient data.');
            } finally {
                setLoading(false);
            }
        };

        fetchPatientAndRecords();
    }, [patientId]);

    const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
        setActiveTab(newValue);
    };

    const handleBreakGlassConfirm = async (justification: string) => {
        setEmergencyJustification(justification);
        setShowBreakGlass(false);
        // Retry fetching with emergency parameters
        await fetchRecords(true, justification);
    };

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" mt={4}>
                <CircularProgress />
            </Box>
        );
    }

    if (error && !patient) {
        return (
            <Container maxWidth="lg" sx={{ mt: 4 }}>
                <Alert severity="error">{error}</Alert>
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4 }}>
            {/* Patient Header */}
            <Paper sx={{ p: 3, mb: 3 }}>
                <Grid container spacing={2} alignItems="center">
                    <Grid>
                        <Avatar sx={{ width: 64, height: 64, bgcolor: 'primary.main' }}>
                            <PersonIcon fontSize="large" />
                        </Avatar>
                    </Grid>
                    <Grid size="grow">
                        <Typography variant="h4">
                            {patient?.firstName} {patient?.lastName}
                        </Typography>
                        <Box display="flex" gap={2} mt={1}>
                            <Typography variant="body1" color="textSecondary">
                                MRN: {patient?.mrn}
                            </Typography>
                            <Typography variant="body1" color="textSecondary">
                                DOB: {patient?.dateOfBirth}
                            </Typography>
                            <Typography variant="body1" color="textSecondary">
                                Gender: {patient?.gender}
                            </Typography>
                        </Box>
                    </Grid>
                    <Grid>
                        {/* Emergency Access Indicator */}
                        {emergencyJustification && (
                            <Chip
                                icon={<WarningIcon />}
                                label="Emergency Access Active"
                                color="warning"
                                variant="outlined"
                            />
                        )}
                    </Grid>
                </Grid>
            </Paper>

            {/* Access Denied / Break Glass Section */}
            {isAccessDenied ? (
                <Box>
                    <Alert
                        severity="warning"
                        action={
                            <Button color="inherit" size="small" onClick={() => setShowBreakGlass(true)}>
                                Emergency Override
                            </Button>
                        }
                    >
                        Access Denied: You do not have a treatment relationship with this patient.
                        in a medical emergency, you may override this restriction.
                    </Alert>
                </Box>
            ) : (
                <>
                    {/* Navigation Tabs */}
                    <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
                        <Tabs value={activeTab} onChange={handleTabChange}>
                            <Tab label="Clinical Notes" />
                            <Tab label="Labs & Imaging" />
                            <Tab label="Medications" />
                            <Tab label="Access Logs" />
                        </Tabs>
                    </Box>

                    {/* Content Area */}
                    <Box>
                        {activeTab === 0 && (
                            <Box>
                                <Typography variant="h6" gutterBottom>Medical History</Typography>
                                {records.length === 0 ? (
                                    <Typography color="textSecondary">No records found.</Typography>
                                ) : (
                                    records.map((record) => (
                                        <Paper key={record.recordId} sx={{ p: 2, mb: 2 }}>
                                            <Box display="flex" justifyContent="space-between" mb={1}>
                                                <Typography variant="subtitle1" fontWeight="bold">
                                                    {record.title}
                                                </Typography>
                                                <Chip
                                                    label={record.sensitivityLevel}
                                                    color={record.sensitivityLevel === 'PUBLIC' ? 'success' : 'warning'}
                                                    size="small"
                                                />
                                            </Box>
                                            <Typography variant="body2" color="textSecondary" gutterBottom>
                                                Date: {new Date(record.createdAt).toLocaleDateString()}
                                            </Typography>
                                            <Typography variant="body1" mt={1}>
                                                {record.description}
                                            </Typography>
                                        </Paper>
                                    ))
                                )}
                            </Box>
                        )}
                        {activeTab === 1 && (
                            <Box mt={2}>
                                <LabResultsViewer patientId={Number(patientId)} />
                            </Box>
                        )}
                        {activeTab === 2 && (
                            <Box mt={2}>
                                <MedicationList patientId={Number(patientId)} />
                            </Box>
                        )}
                        {activeTab === 3 && (
                            <Box mt={2}>
                                <AuditLogViewer patientId={Number(patientId)} />
                            </Box>
                        )}    </Box>
                </>
            )}

            <BreakGlassDialog
                open={showBreakGlass}
                onClose={() => setShowBreakGlass(false)}
                onConfirm={handleBreakGlassConfirm}
                patientName={`${patient?.firstName} ${patient?.lastName}`}
            />
        </Container>
    );
};

export default MedicalRecordView;
