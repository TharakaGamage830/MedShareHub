import { useDispatch, useSelector } from 'react-redux';
import {
    Box,
    AppBar,
    Toolbar,
    Typography,
    Button,
    Container,
    Grid,
    Paper,
    Divider
} from '@mui/material';
import LogoutIcon from '@mui/icons-material/Logout';
import PersonIcon from '@mui/icons-material/Person';
import PeopleIcon from '@mui/icons-material/People';
import AssignmentIcon from '@mui/icons-material/Assignment';
import WarningIcon from '@mui/icons-material/Warning';
import type { RootState } from '../store';
import { logout } from '../store/authSlice';
import PatientSearch from '../components/dashboard/PatientSearch';
import StatCard from '../components/dashboard/StatCard';
import RecentPatients from '../components/dashboard/RecentPatients';

const Dashboard = () => {
    const dispatch = useDispatch();
    const { user } = useSelector((state: RootState) => state.auth);

    const handleLogout = () => {
        dispatch(logout());
    };

    return (
        <Box sx={{ flexGrow: 1, bgcolor: 'grey.50', minHeight: '100vh' }}>
            <AppBar position="static" elevation={0} sx={{ borderBottom: '1px solid', borderColor: 'divider' }}>
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1, fontWeight: 'bold' }}>
                        MedShare Hub
                    </Typography>
                    <Box display="flex" alignItems="center" gap={2}>
                        <Box display="flex" alignItems="center" gap={1}>
                            <PersonIcon fontSize="small" />
                            <Typography variant="subtitle2">
                                {user?.email} ({user?.role})
                            </Typography>
                        </Box>
                        <Button variant="outlined" color="inherit" size="small" onClick={handleLogout} startIcon={<LogoutIcon />}>
                            Logout
                        </Button>
                    </Box>
                </Toolbar>
            </AppBar>

            <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
                <Box mb={4}>
                    <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
                        Welcome, Dr. {user?.email.split('@')[0]}
                    </Typography>
                    <Typography color="textSecondary">
                        Manage your patients and access logs from your secure dashboard.
                    </Typography>
                </Box>

                <Grid container spacing={4}>
                    {/* Main Content: Search and Stats */}
                    <Grid size={{ xs: 12, md: 8 }}>
                        <Box mb={4}>
                            <Typography variant="h6" gutterBottom>Find Patient</Typography>
                            <PatientSearch />
                        </Box>

                        <Typography variant="h6" gutterBottom>Quick Overview</Typography>
                        <Grid container spacing={3}>
                            <Grid size={{ xs: 12, sm: 4 }}>
                                <StatCard
                                    title="Active Patients"
                                    value="12"
                                    icon={<PeopleIcon />}
                                    actionLabel="View All"
                                />
                            </Grid>
                            <Grid size={{ xs: 12, sm: 4 }}>
                                <StatCard
                                    title="Pending Consents"
                                    value="3"
                                    icon={<AssignmentIcon />}
                                    actionLabel="Review"
                                    color="info.main"
                                />
                            </Grid>
                            <Grid size={{ xs: 12, sm: 4 }}>
                                <StatCard
                                    title="Emergency Access"
                                    value="0"
                                    icon={<WarningIcon />}
                                    actionLabel="Log Audit"
                                    color="error.main"
                                />
                            </Grid>
                        </Grid>

                        <Box mt={4}>
                            <Paper sx={{ p: 3 }}>
                                <Typography variant="h6" gutterBottom>System Announcements</Typography>
                                <Divider sx={{ mb: 2 }} />
                                <Typography variant="body2" paragraph>
                                    - New ABAC policies for Psychiatric records are now active.
                                </Typography>
                                <Typography variant="body2">
                                    - Emergency Access (Break-Glass) logs are now requiring mandatory 24-hour review.
                                </Typography>
                            </Paper>
                        </Box>
                    </Grid>

                    {/* Sidebar: Recent Patients */}
                    <Grid size={{ xs: 12, md: 4 }}>
                        <RecentPatients />
                    </Grid>
                </Grid>
            </Container>
        </Box>
    );
};

export default Dashboard;
