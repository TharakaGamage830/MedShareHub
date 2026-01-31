import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Paper,
    List,
    ListItem,
    ListItemText,
    ListItemIcon,
    Divider,
    Chip,
    CircularProgress,
    Alert
} from '@mui/material';
import MedicationIcon from '@mui/icons-material/Medication';
import { MedicalRecordService, type MedicalRecord } from '../../services/MedicalRecordService';

interface MedicationListProps {
    patientId: number;
}

const MedicationList: React.FC<MedicationListProps> = ({ patientId }) => {
    const [medications, setMedications] = useState<MedicalRecord[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchMeds = async () => {
            try {
                setLoading(true);
                const response = await MedicalRecordService.getPatientRecords(patientId);
                const meds = response.filter((r: MedicalRecord) =>
                    r.recordType === 'MEDICATION' || r.title.toLowerCase().includes('medication') || r.title.toLowerCase().includes('prescription')
                );
                setMedications(meds);
            } catch (err) {
                console.error(err);
                setError('Failed to load medications.');
            } finally {
                setLoading(false);
            }
        };
        fetchMeds();
    }, [patientId]);

    if (loading) return <CircularProgress />;
    if (error) return <Alert severity="error">{error}</Alert>;

    return (
        <Box>
            <Typography variant="h6" gutterBottom>Current Medications</Typography>
            <Paper>
                <List>
                    {medications.map((med, index) => (
                        <React.Fragment key={med.recordId}>
                            <ListItem>
                                <ListItemIcon>
                                    <MedicationIcon color="primary" />
                                </ListItemIcon>
                                <ListItemText
                                    primary={med.title}
                                    secondary={`Prescribed on: ${new Date(med.createdAt).toLocaleDateString()}`}
                                />
                                <Chip label="Active" color="success" size="small" />
                            </ListItem>
                            {index < medications.length - 1 && <Divider />}
                        </React.Fragment>
                    ))}
                    {medications.length === 0 && (
                        <ListItem>
                            <ListItemText primary="No active medications found." sx={{ textAlign: 'center', color: 'text.secondary' }} />
                        </ListItem>
                    )}
                </List>
            </Paper>
        </Box>
    );
};

export default MedicationList;
