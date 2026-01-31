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
import SendIcon from '@mui/icons-material/Send';
import EmailIcon from '@mui/icons-material/Email';
import type { RootState } from '../store';
import { logout } from '../store/authSlice';
import PatientSearch from '../components/dashboard/PatientSearch';
import StatCard from '../components/dashboard/StatCard';
import RecentPatients from '../components/dashboard/RecentPatients';
import MessageInbox from '../components/messages/MessageInbox';
import AnomalyAlerts from '../components/audit/AnomalyAlerts';

const Dashboard = () => {
    const dispatch = useDispatch();
    const { user } = useSelector((state: RootState) => state.auth);

    const handleLogout = () => {
        dispatch(logout());
    };

    return (
        <Box sx={{ flexGrow: 1, bgcolor: 'grey.50', minHeight: '100vh' }} component="div" role="presentation">
            <AppBar
                position="static"
                elevation={0}
                sx={{ borderBottom: '1px solid', borderColor: 'divider' }}
                component="nav"
                aria-label="Main Navigation"
            >
                <Toolbar>
                    <Typography
                        variant="h6"
                        component="h2"
                        sx={{ flexGrow: 1, fontWeight: 'bold' }}
                    >
                        MedShare Hub
                    </Typography>
                    <Box display="flex" alignItems="center" gap={2}>
                        <Box display="flex" alignItems="center" gap={1} aria-label={`Logged in as ${user?.email}`}>
                            <PersonIcon fontSize="small" aria-hidden="true" />
                            <Typography variant="subtitle2">
                                {user?.email} ({user?.role})
                            </Typography>
                        </Box>
                        <Button
                            variant="outlined"
                            color="inherit"
                            size="small"
                            onClick={handleLogout}
                            startIcon={<LogoutIcon aria-hidden="true" />}
                            aria-label="Logout from system"
                        >
                            Logout
                        </Button>
                    </Box>
                </Toolbar>
            </AppBar>

            <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }} component="main">
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
                        <Box mb={4} component="section" aria-labelledby="search-title">
                            <Typography id="search-title" variant="h6" gutterBottom>Find Patient</Typography>
                            <PatientSearch />
                        </Box>

                        <Box component="section" aria-labelledby="overview-title">
                            <Typography id="overview-title" variant="h6" gutterBottom>Quick Overview</Typography>
                            <Grid container spacing={3}>
                                <Grid size={{ xs: 12, sm: 4 }}>
                                    <StatCard
                                        title="Active Patients"
                                        value="12"
                                        icon={<PeopleIcon aria-hidden="true" />}
                                        actionLabel="View All"
                                    />
                                </Grid>
                                <Grid size={{ xs: 12, sm: 4 }}>
                                    <StatCard
                                        title="Pending Consents"
                                        value="3"
                                        icon={<AssignmentIcon aria-hidden="true" />}
                                        actionLabel="Review"
                                        color="info.main"
                                    />
                                </Grid>
                                <Grid size={{ xs: 12, sm: 4 }}>
                                    <StatCard
                                        title="Emergency Access"
                                        value="0"
                                        icon={<WarningIcon aria-hidden="true" />}
                                        actionLabel="Log Audit"
                                        color="error.main"
                                    />
                                </Grid>
                            </Grid>
                        </Box>

                        <Box mt={4} component="section" aria-labelledby="announcements-title">
                            <Paper sx={{ p: 3 }}>
                                <Typography id="announcements-title" variant="h6" gutterBottom>System Announcements</Typography>
                                <Divider sx={{ mb: 2 }} aria-hidden="true" />
                                <Typography variant="body2" paragraph>
                                    - New ABAC policies for Psychiatric records are now active.
                                </Typography>
                                <Typography variant="body2">
                                    - Emergency Access (Break-Glass) logs are now requiring mandatory 24-hour review.
                                </Typography>
                            </Paper>
                        </Box>

                        <Box mt={4} component="section" aria-labelledby="recent-patients-title">
                            <Typography id="recent-patients-title" variant="h6" sx={{ display: 'none' }}>Recent Patients</Typography>
                            <RecentPatients />
                        </Box>
                    </Grid>

                    {/* Sidebar */}
                    <Grid size={{ xs: 12, md: 4 }} component="aside">
                        <Box mb={3} component="section" aria-labelledby="alerts-title">
                            <Typography id="alerts-title" variant="h6" sx={{ display: 'none' }}>Security Alerts</Typography>
                            <AnomalyAlerts />
                        </Box>

                        <Paper sx={{ p: 2 }} component="section" aria-labelledby="inbox-title">
                            <Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
                                <Box display="flex" alignItems="center" gap={1}>
                                    <EmailIcon color="primary" aria-hidden="true" />
                                    <Typography id="inbox-title" variant="h6">Inbox</Typography>
                                </Box>
                                <Button
                                    size="small"
                                    variant="outlined"
                                    startIcon={<SendIcon aria-hidden="true" />}
                                    aria-label="Compose new message"
                                >
                                    New
                                </Button>
                            </Box>
                            <MessageInbox />
                        </Paper>
                    </Grid>
                </Grid>

                <Box mt={8} pt={4} borderTop="1px solid" borderColor="divider">
                    <Typography variant="caption" color="textSecondary" align="center" display="block">
                        CONFIDENTIALITY NOTICE: This system contains protected health information (PHI) governed by HIPAA.
                        Unauthorized access is strictly prohibited and subject to legal action.
                        All access is monitored and logged for security and compliance auditing.
                    </Typography>
                </Box>
            </Container>
        </Box>
    );
};

export default Dashboard;
