import { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Paper,
    List,
    ListItem,
    ListItemText,
    ListItemIcon,
    Chip,
    IconButton,
    Tooltip,
    Alert,
    CircularProgress,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField
} from '@mui/material';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import VisibilityIcon from '@mui/icons-material/Visibility';
import { AnomalyService, type AnomalyAlert } from '../../services/AnomalyService';

const AnomalyAlerts = () => {
    const [alerts, setAlerts] = useState<AnomalyAlert[]>([]);
    const [loading, setLoading] = useState(true);
    const [selectedAlert, setSelectedAlert] = useState<AnomalyAlert | null>(null);
    const [resolution, setResolution] = useState('');

    const fetchAlerts = async () => {
        try {
            setLoading(true);
            const data = await AnomalyService.getAlerts();
            setAlerts(data);
        } catch (err) {
            console.error('Failed to fetch alerts', err);
            // Mock empty if not ready
            setAlerts([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAlerts();
    }, []);

    const handleResolve = async () => {
        if (!selectedAlert || !resolution) return;
        try {
            await AnomalyService.resolveAlert(selectedAlert.id, resolution);
            fetchAlerts();
            setSelectedAlert(null);
            setResolution('');
        } catch (err) {
            console.error('Failed to resolve alert', err);
        }
    };

    if (loading) return <CircularProgress />;

    return (
        <Box>
            <Typography variant="h6" gutterBottom color="error" display="flex" alignItems="center" gap={1}>
                <WarningAmberIcon /> Security Anomaly Alerts
            </Typography>
            <Paper>
                <List>
                    {alerts.map((alert) => (
                        <ListItem
                            key={alert.id}
                            secondaryAction={
                                <Box display="flex" gap={1}>
                                    <Tooltip title="View Details">
                                        <IconButton size="small" onClick={() => setSelectedAlert(alert)}>
                                            <VisibilityIcon />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Quick Resolve">
                                        <IconButton size="small" color="success" onClick={() => setSelectedAlert(alert)}>
                                            <CheckCircleOutlineIcon />
                                        </IconButton>
                                    </Tooltip>
                                </Box>
                            }
                        >
                            <ListItemIcon>
                                <WarningAmberIcon color={alert.severity === 'HIGH' ? 'error' : 'warning'} />
                            </ListItemIcon>
                            <ListItemText
                                primary={
                                    <Box display="flex" alignItems="center" gap={1}>
                                        <Typography variant="subtitle2">{alert.type}</Typography>
                                        <Chip
                                            label={alert.severity}
                                            size="small"
                                            color={alert.severity === 'HIGH' ? 'error' : alert.severity === 'MEDIUM' ? 'warning' : 'default'}
                                        />
                                    </Box>
                                }
                                secondary={
                                    <>
                                        <Typography variant="body2" color="textPrimary">Patient: {alert.patientName}</Typography>
                                        <Typography variant="caption" color="textSecondary">
                                            {alert.description} • {new Date(alert.timestamp).toLocaleString()}
                                        </Typography>
                                    </>
                                }
                            />
                        </ListItem>
                    ))}
                    {alerts.length === 0 && (
                        <ListItem>
                            <ListItemText primary="No active security anomalies detected." sx={{ textAlign: 'center', py: 2, color: 'text.secondary' }} />
                        </ListItem>
                    )}
                </List>
            </Paper>

            <Dialog open={!!selectedAlert} onClose={() => setSelectedAlert(null)} fullWidth maxWidth="sm">
                <DialogTitle>Resolve Security Anomaly</DialogTitle>
                <DialogContent dividers>
                    {selectedAlert && (
                        <Box mb={2}>
                            <Typography variant="subtitle1" fontWeight="bold">{selectedAlert.type}</Typography>
                            <Typography variant="body2" paragraph>{selectedAlert.description}</Typography>
                            <Alert severity="info">
                                Detected on: {new Date(selectedAlert.timestamp).toLocaleString()}
                            </Alert>
                        </Box>
                    )}
                    <TextField
                        fullWidth
                        label="Resolution/Reasoning"
                        multiline
                        rows={3}
                        value={resolution}
                        onChange={(e) => setResolution(e.target.value)}
                        placeholder="Explain the cause of this activity or actions taken..."
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setSelectedAlert(null)}>Cancel</Button>
                    <Button onClick={handleResolve} variant="contained" color="success" disabled={!resolution}>
                        Resolve Alert
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default AnomalyAlerts;
