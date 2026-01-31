import React, { useState } from 'react';
import {
    TextField,
    InputAdornment,
    List,
    ListItem,
    ListItemText,
    ListItemAvatar,
    Avatar,
    Box,
    Paper,
    CircularProgress
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import PersonIcon from '@mui/icons-material/Person';
import { useNavigate } from 'react-router-dom';
import { PatientService, type Patient } from '../../services/PatientService';

const PatientSearch = () => {
    const [query, setQuery] = useState('');
    const [results, setResults] = useState<Patient[]>([]);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSearch = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const val = e.target.value;
        setQuery(val);

        if (val.length > 2) {
            setLoading(true);
            try {
                const data = await PatientService.searchPatients(val);
                setResults(data);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        } else {
            setResults([]);
        }
    };

    const handleSelect = (patientId: number) => {
        navigate(`/records/${patientId}`);
    };

    return (
        <Box sx={{ position: 'relative' }}>
            <TextField
                fullWidth
                variant="outlined"
                placeholder="Search patients by name or MRN..."
                value={query}
                onChange={handleSearch}
                InputProps={{
                    startAdornment: (
                        <InputAdornment position="start">
                            <SearchIcon />
                        </InputAdornment>
                    ),
                    endAdornment: loading && (
                        <InputAdornment position="end">
                            <CircularProgress size={20} />
                        </InputAdornment>
                    )
                }}
            />
            {results.length > 0 && (
                <Paper
                    elevation={3}
                    sx={{
                        position: 'absolute',
                        top: '100%',
                        left: 0,
                        right: 0,
                        zIndex: 1000,
                        mt: 1,
                        maxHeight: 300,
                        overflow: 'auto'
                    }}
                >
                    <List>
                        {results.map((patient) => (
                            <ListItem
                                key={patient.patientId}
                                component="div"
                                sx={{ cursor: 'pointer', '&:hover': { bgcolor: 'action.hover' } }}
                                onClick={() => handleSelect(patient.patientId)}
                            >
                                <ListItemAvatar>
                                    <Avatar>
                                        <PersonIcon />
                                    </Avatar>
                                </ListItemAvatar>
                                <ListItemText
                                    primary={`${patient.firstName} ${patient.lastName}`}
                                    secondary={`MRN: ${patient.mrn}`}
                                />
                            </ListItem>
                        ))}
                    </List>
                </Paper>
            )}
        </Box>
    );
};

export default PatientSearch;
