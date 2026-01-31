import React, { useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogContentText,
    DialogActions,
    Button,
    TextField,
    Alert,
    Typography
} from '@mui/material';
import WarningIcon from '@mui/icons-material/Warning';

interface BreakGlassDialogProps {
    open: boolean;
    onClose: () => void;
    onConfirm: (justification: string) => void;
    patientName: string;
}

const BreakGlassDialog: React.FC<BreakGlassDialogProps> = ({
    open,
    onClose,
    onConfirm,
    patientName
}) => {
    const [justification, setJustification] = useState('');
    const [error, setError] = useState('');

    const handleConfirm = () => {
        if (justification.trim().length < 10) {
            setError('Please provide a detailed justification (at least 10 characters).');
            return;
        }
        onConfirm(justification);
        setJustification('');
        setError('');
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1, color: 'warning.main' }}>
                <WarningIcon />
                Emergency Access (Break-Glass)
            </DialogTitle>
            <DialogContent>
                <DialogContentText paragraph>
                    You are about to access the medical records of <strong>{patientName}</strong> outside of standard authorization protocols.
                </DialogContentText>

                <Alert severity="warning" sx={{ mb: 2 }}>
                    This action will be audited and flagged for immediate review by the compliance officer.
                    Misuse of emergency access may result in disciplinary action.
                </Alert>

                <Typography variant="subtitle2" gutterBottom>
                    Reason for Emergency Access:
                </Typography>
                <TextField
                    autoFocus
                    margin="dense"
                    label="Justification"
                    fullWidth
                    multiline
                    rows={3}
                    value={justification}
                    onChange={(e) => setJustification(e.target.value)}
                    error={!!error}
                    helperText={error}
                    placeholder="e.g., Patient arrived in critical condition unconscious..."
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} color="inherit">
                    Cancel
                </Button>
                <Button onClick={handleConfirm} color="warning" variant="contained">
                    Confirm Emergency Access
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default BreakGlassDialog;
