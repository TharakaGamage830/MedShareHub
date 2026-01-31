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
import WarningIcon from '@mui/icons-material/Warning';
import { AuditService, type AccessLog } from '../../services/AuditService';

interface AuditLogViewerProps {
    patientId: number;
}

const AuditLogViewer: React.FC<AuditLogViewerProps> = ({ patientId }) => {
    const [logs, setLogs] = useState<AccessLog[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchLogs = async () => {
            try {
                setLoading(true);
                const data = await AuditService.getPatientAccessLogs(patientId);
                setLogs(data);
                setError(null);
            } catch (err) {
                console.error(err);
                setError('Failed to load audit logs.');
            } finally {
                setLoading(false);
            }
        };

        if (patientId) {
            fetchLogs();
        }
    }, [patientId]);

    if (loading) return <CircularProgress />;
    if (error) return <Alert severity="error">{error}</Alert>;

    return (
        <Box>
            <Typography variant="h6" gutterBottom>Access History</Typography>
            <TableContainer component={Paper}>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>Time</TableCell>
                            <TableCell>User</TableCell>
                            <TableCell>Action</TableCell>
                            <TableCell>Resource</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Context</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {logs.map((log) => (
                            <TableRow key={log.logId} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                <TableCell>{new Date(log.createdAt).toLocaleString()}</TableCell>
                                <TableCell>
                                    {log.user ? log.user.email : 'System'}
                                </TableCell>
                                <TableCell>{log.action}</TableCell>
                                <TableCell>{log.resourceType}</TableCell>
                                <TableCell>
                                    <Chip
                                        label={log.decision}
                                        color={log.decision === 'PERMIT' ? 'success' : 'error'}
                                        size="small"
                                    />
                                </TableCell>
                                <TableCell>
                                    {log.isEmergency && (
                                        <Chip
                                            icon={<WarningIcon />}
                                            label="Emergency"
                                            color="warning"
                                            size="small"
                                            variant="outlined"
                                            title={log.justification}
                                        />
                                    )}
                                </TableCell>
                            </TableRow>
                        ))}
                        {logs.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={6} align="center">No access logs found.</TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default AuditLogViewer;
