import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Chip,
    CircularProgress,
    Alert
} from '@mui/material';
import { MedicalRecordService, type MedicalRecord } from '../../services/MedicalRecordService';

interface LabResultsViewerProps {
    patientId: number;
}

const LabResultsViewer: React.FC<LabResultsViewerProps> = ({ patientId }) => {
    const [records, setRecords] = useState<MedicalRecord[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchLabs = async () => {
            try {
                setLoading(true);
                // In a real app, we might have a specific endpoint or filter
                const response = await MedicalRecordService.getPatientRecords(patientId);
                // Filter for Lab Results if the type exists
                const labs = response.filter((r: MedicalRecord) =>
                    r.recordType === 'LAB_RESULT' || r.title.toLowerCase().includes('lab')
                );
                setRecords(labs);
            } catch (err) {
                console.error(err);
                setError('Failed to load lab results.');
            } finally {
                setLoading(false);
            }
        };
        fetchLabs();
    }, [patientId]);

    if (loading) return <CircularProgress />;
    if (error) return <Alert severity="error">{error}</Alert>;

    return (
        <Box>
            <Typography variant="h6" gutterBottom>Laboratory Results</Typography>
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Date</TableCell>
                            <TableCell>Test Name</TableCell>
                            <TableCell>Result</TableCell>
                            <TableCell>Status</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {records.map((record) => (
                            <TableRow key={record.recordId}>
                                <TableCell>{new Date(record.createdAt).toLocaleDateString()}</TableCell>
                                <TableCell>{record.title}</TableCell>
                                <TableCell>{record.description}</TableCell>
                                <TableCell>
                                    <Chip
                                        label="Final"
                                        size="small"
                                        color="success"
                                        variant="outlined"
                                    />
                                </TableCell>
                            </TableRow>
                        ))}
                        {records.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={4} align="center">No lab results found.</TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default LabResultsViewer;
