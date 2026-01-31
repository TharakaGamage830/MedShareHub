import React, { useState, useEffect } from 'react';
import {
    Box,
    Paper,
    Typography,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Button,
    Chip,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Alert,
    CircularProgress
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import BlockIcon from '@mui/icons-material/Block';
import { ConsentService, type Consent } from '../../services/ConsentService';
import AccessDelegationDialog from './AccessDelegationDialog';
import SupervisorAccountIcon from '@mui/icons-material/SupervisorAccount';

interface ConsentManagerProps {
    patientId: number;
}

const ConsentManager: React.FC<ConsentManagerProps> = ({ patientId }) => {
    const [consents, setConsents] = useState<Consent[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [delegationOpen, setDelegationOpen] = useState(false);
    const [newConsent, setNewConsent] = useState({
        dataType: '',
        purpose: '',
        grantedToUserId: '',
        expiresAt: ''
    });

    const fetchConsents = async () => {
        try {
            setLoading(true);
            const data = await ConsentService.getPatientConsents(patientId);
            setConsents(data);
            setError(null);
        } catch (err) {
            setError('Failed to load consents.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (patientId) {
            fetchConsents();
        }
    }, [patientId]);

    const handleCreateConsent = async () => {
        try {
            await ConsentService.createConsent({
                patientId,
                grantedToUserId: newConsent.grantedToUserId ? Number(newConsent.grantedToUserId) : undefined,
                dataType: newConsent.dataType,
                purpose: newConsent.purpose,
                expiresAt: new Date(newConsent.expiresAt).toISOString()
            });
            setDialogOpen(false);
            fetchConsents();
        } catch (err) {
            console.error('Failed to create consent', err);
            // Ideally show error toast
        }
    };

    const handleRevoke = async (consentId: number) => {
        try {
            await ConsentService.revokeConsent(consentId, patientId);
            fetchConsents();
        } catch (err) {
            console.error('Failed to revoke consent', err);
        }
    };

    if (loading) return <CircularProgress />;

    return (
        <Box>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h6">Consent Management</Typography>
                <Box display="flex" gap={2}>
                    <Button
                        variant="outlined"
                        startIcon={<SupervisorAccountIcon />}
                        onClick={() => setDelegationOpen(true)}
                    >
                        Delegate Access
                    </Button>
                    <Button
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={() => setDialogOpen(true)}
                    >
                        Grant New Consent
                    </Button>
                </Box>
            </Box>

            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Data Type</TableCell>
                            <TableCell>Purpose</TableCell>
                            <TableCell>Recipient ID</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Expires At</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {consents.map((consent) => (
                            <TableRow key={consent.consentId}>
                                <TableCell>{consent.dataType}</TableCell>
                                <TableCell>{consent.purpose}</TableCell>
                                <TableCell>{consent.providerId || consent.grantedToOrganization || 'N/A'}</TableCell>
                                <TableCell>
                                    <Chip
                                        label={consent.revoked ? 'REVOKED' : 'ACTIVE'}
                                        color={!consent.revoked ? 'success' : 'default'}
                                        size="small"
                                    />
                                </TableCell>
                                <TableCell>{consent.expiresAt ? new Date(consent.expiresAt).toLocaleDateString() : 'Never'}</TableCell>
                                <TableCell>
                                    {!consent.revoked && (
                                        <Button
                                            size="small"
                                            color="error"
                                            startIcon={<BlockIcon />}
                                            onClick={() => handleRevoke(consent.consentId)}
                                        >
                                            Revoke
                                        </Button>
                                    )}
                                </TableCell>
                            </TableRow>
                        ))}
                        {consents.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={6} align="center">No active consents found</TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* New Consent Dialog */}
            <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)}>
                <DialogTitle>Grant New Consent</DialogTitle>
                <DialogContent>
                    <Box display="flex" flexDirection="column" gap={2} mt={1} minWidth={400}>
                        <FormControl fullWidth>
                            <InputLabel>Data to Share</InputLabel>
                            <Select
                                value={newConsent.dataType}
                                label="Data to Share"
                                onChange={(e) => setNewConsent({ ...newConsent, dataType: e.target.value })}
                            >
                                <MenuItem value="ALL">All Records</MenuItem>
                                <MenuItem value="LAB_RESULTS">Lab Results Only</MenuItem>
                                <MenuItem value="PRESCRIPTIONS">Medications Only</MenuItem>
                                <MenuItem value="VISIT_NOTES">Visit Notes</MenuItem>
                            </Select>
                        </FormControl>
                        <FormControl fullWidth>
                            <InputLabel>Purpose</InputLabel>
                            <Select
                                value={newConsent.purpose}
                                label="Purpose"
                                onChange={(e) => setNewConsent({ ...newConsent, purpose: e.target.value })}
                            >
                                <MenuItem value="TREATMENT">Treatment</MenuItem>
                                <MenuItem value="INSURANCE">Insurance Claim</MenuItem>
                                <MenuItem value="FAMILY_ACCESS">Family Access</MenuItem>
                                <MenuItem value="RESEARCH">Research</MenuItem>
                            </Select>
                        </FormControl>
                        <TextField
                            label="Provider ID (Recipient)"
                            type="number"
                            value={newConsent.grantedToUserId}
                            onChange={(e) => setNewConsent({ ...newConsent, grantedToUserId: e.target.value })}
                        />
                        <TextField
                            label="Expiration Date"
                            type="date"
                            InputLabelProps={{ shrink: true }}
                            value={newConsent.expiresAt}
                            onChange={(e) => setNewConsent({ ...newConsent, expiresAt: e.target.value })}
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
                    <Button onClick={handleCreateConsent} variant="contained">Grant Consent</Button>
                </DialogActions>
            </Dialog>
            <AccessDelegationDialog
                open={delegationOpen}
                onClose={() => setDelegationOpen(false)}
                patientId={patientId}
                onSuccess={fetchConsents}
            />
        </Box>
    );
};

export default ConsentManager;
