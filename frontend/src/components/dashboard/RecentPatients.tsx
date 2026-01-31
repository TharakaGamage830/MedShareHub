import React, { useState, useEffect } from 'react';
import {
    Box,
    List,
    ListItem,
    ListItemText,
    ListItemAvatar,
    Avatar,
    Typography,
    Paper,
    Divider,
    IconButton,
    Tooltip
} from '@mui/material';
import PersonIcon from '@mui/icons-material/Person';
import HistoryIcon from '@mui/icons-material/History';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { useNavigate } from 'react-router-dom';
import { PatientService, type Patient } from '../../services/PatientService';

const RecentPatients = () => {
    const [patients, setPatients] = useState<Patient[]>([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchRecent = async () => {
            try {
                const data = await PatientService.getRecentPatients();
                setPatients(data);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        fetchRecent();
    }, []);

    return (
        <Paper sx={{ p: 2 }}>
            <Box display="flex" alignItems="center" gap={1} mb={2}>
                <HistoryIcon color="primary" />
                <Typography variant="h6">Recent Patients</Typography>
            </Box>
            <Divider />
            <List>
                {patients.map((patient, index) => (
                    <React.Fragment key={patient.patientId}>
                        <ListItem
                            secondaryAction={
                                <Tooltip title="View Records">
                                    <IconButton edge="end" onClick={() => navigate(`/records/${patient.patientId}`)}>
                                        <ArrowForwardIcon />
                                    </IconButton>
                                </Tooltip>
                            }
                        >
                            <ListItemAvatar>
                                <Avatar sx={{ bgcolor: 'primary.light' }}>
                                    <PersonIcon />
                                </Avatar>
                            </ListItemAvatar>
                            <ListItemText
                                primary={`${patient.firstName} ${patient.lastName}`}
                                secondary={`MRN: ${patient.mrn}`}
                            />
                        </ListItem>
                        {index < patients.length - 1 && <Divider variant="inset" component="li" />}
                    </React.Fragment>
                ))}
                {patients.length === 0 && !loading && (
                    <Typography variant="body2" color="textSecondary" sx={{ py: 2, textAlign: 'center' }}>
                        No recent patients found.
                    </Typography>
                )}
            </List>
        </Paper>
    );
};

export default RecentPatients;
