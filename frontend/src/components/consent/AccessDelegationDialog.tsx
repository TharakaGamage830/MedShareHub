import React, { useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Autocomplete,
    Typography,
    Alert
} from '@mui/material';
import { AccessDelegationService } from '../../services/AccessDelegationService';

interface AccessDelegationDialogProps {
    open: boolean;
    onClose: () => void;
    patientId: number;
    onSuccess?: () => void;
}

const AccessDelegationDialog: React.FC<AccessDelegationDialogProps> = ({ open, onClose, patientId, onSuccess }) => {
    const [delegate, setDelegate] = useState<any>(null);
    const [expiryDate, setExpiryDate] = useState('');
    const [reason, setReason] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const handleDelegate = async () => {
        if (!delegate || !expiryDate || !reason) return;
        setLoading(true);
        setError(null);
        try {
            await AccessDelegationService.delegateAccess({
                patientId,
                delegateId: delegate.id,
                expiryDate,
                reason
            });
            onSuccess?.();
            onClose();
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to delegate access.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>Delegate Temporary Access</DialogTitle>
            <DialogContent dividers>
                <Typography variant="body2" color="textSecondary" paragraph>
                    Grant another healthcare provider temporary access to this patient's records.
                    This access will automatically expire on the selected date.
                </Typography>

                {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

                <Autocomplete
                    options={[]} // Mock: Should be populated from doctors list
                    getOptionLabel={(option: any) => option.name || ''}
                    renderInput={(params) => <TextField {...params} label="Select Provider" margin="normal" required />}
                    value={delegate}
                    onChange={(_, val) => setDelegate(val)}
                    noOptionsText="No providers found"
                />

                <TextField
                    fullWidth
                    label="Expiry Date"
                    type="date"
                    margin="normal"
                    InputLabelProps={{ shrink: true }}
                    value={expiryDate}
                    onChange={(e) => setExpiryDate(e.target.value)}
                    required
                />

                <TextField
                    fullWidth
                    label="Reason for Delegation"
                    margin="normal"
                    multiline
                    rows={3}
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    required
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} disabled={loading}>Cancel</Button>
                <Button
                    onClick={handleDelegate}
                    variant="contained"
                    color="primary"
                    disabled={loading || !delegate || !expiryDate || !reason}
                >
                    Delegate Access
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default AccessDelegationDialog;
