import React, { useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Autocomplete,
    CircularProgress
} from '@mui/material';
import { MessageService } from '../../services/MessageService';

interface NewMessageDialogProps {
    open: boolean;
    onClose: () => void;
    onSent?: () => void;
}

const NewMessageDialog: React.FC<NewMessageDialogProps> = ({ open, onClose, onSent }) => {
    const [recipient, setRecipient] = useState<any>(null);
    const [subject, setSubject] = useState('');
    const [body, setBody] = useState('');
    const [sending, setSending] = useState(false);

    const handleSend = async () => {
        if (!recipient || !subject || !body) return;
        setSending(true);
        try {
            await MessageService.sendMessage(recipient.id, subject, body);
            onSent?.();
            onClose();
            // Reset
            setRecipient(null);
            setSubject('');
            setBody('');
        } catch (err) {
            console.error(err);
        } finally {
            setSending(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>New Message</DialogTitle>
            <DialogContent dividers>
                <Autocomplete
                    options={[]} // Mock: Should be populated from patient/provider list
                    getOptionLabel={(option: any) => option.name || ''}
                    renderInput={(params) => <TextField {...params} label="Recipient" margin="normal" />}
                    value={recipient}
                    onChange={(_, val) => setRecipient(val)}
                    noOptionsText="No contacts found"
                />
                <TextField
                    fullWidth
                    label="Subject"
                    margin="normal"
                    value={subject}
                    onChange={(e) => setSubject(e.target.value)}
                />
                <TextField
                    fullWidth
                    label="Message"
                    margin="normal"
                    multiline
                    rows={4}
                    value={body}
                    onChange={(e) => setBody(e.target.value)}
                />
            </DialogContent>
            <DialogActions sx={{ px: 3, py: 2 }}>
                <Button onClick={onClose} disabled={sending}>Cancel</Button>
                <Button
                    onClick={handleSend}
                    variant="contained"
                    disabled={sending || !recipient || !subject || !body}
                    sx={{ minWidth: 120 }}
                >
                    {sending ? <CircularProgress size={24} color="inherit" /> : 'Send Message'}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default NewMessageDialog;
