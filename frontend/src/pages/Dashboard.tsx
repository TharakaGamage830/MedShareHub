import { useDispatch, useSelector } from 'react-redux';
import {
    Box,
    AppBar,
    Toolbar,
    Typography,
    Button,
    Container,
    Grid,
    Card,
    CardContent,
    CardActions
} from '@mui/material';
import LogoutIcon from '@mui/icons-material/Logout';
import PersonIcon from '@mui/icons-material/Person';
import type { RootState } from '../store';
import { logout } from '../store/authSlice';

const Dashboard = () => {
    const dispatch = useDispatch();
    const { user } = useSelector((state: RootState) => state.auth);

    const handleLogout = () => {
        dispatch(logout());
    };

    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        MedShare Hub
                    </Typography>
                    <Box display="flex" alignItems="center" gap={2}>
                        <Box display="flex" alignItems="center" gap={1}>
                            <PersonIcon />
                            <Typography variant="subtitle1">
                                Drag. {user?.email} ({user?.role})
                            </Typography>
                        </Box>
                        <Button color="inherit" onClick={handleLogout} startIcon={<LogoutIcon />}>
                            Logout
                        </Button>
                    </Box>
                </Toolbar>
            </AppBar>

            <Container maxWidth="lg" sx={{ mt: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Provider Dashboard
                </Typography>

                <Grid container spacing={3}>
                    {/* Quick Stats */}
                    <Grid size={{ xs: 12, md: 4 }}>
                        <Card>
                            <CardContent>
                                <Typography color="textSecondary" gutterBottom>
                                    My Patients
                                </Typography>
                                <Typography variant="h3">
                                    12
                                </Typography>
                            </CardContent>
                            <CardActions>
                                <Button size="small">View All</Button>
                            </CardActions>
                        </Card>
                    </Grid>

                    <Grid size={{ xs: 12, md: 4 }}>
                        <Card>
                            <CardContent>
                                <Typography color="textSecondary" gutterBottom>
                                    Pending Consents
                                </Typography>
                                <Typography variant="h3">
                                    3
                                </Typography>
                            </CardContent>
                            <CardActions>
                                <Button size="small">Review</Button>
                            </CardActions>
                        </Card>
                    </Grid>

                    <Grid size={{ xs: 12, md: 4 }}>
                        <Card>
                            <CardContent>
                                <Typography color="textSecondary" gutterBottom>
                                    Emergency Access
                                </Typography>
                                <Typography variant="h3" color="error">
                                    0
                                </Typography>
                            </CardContent>
                            <CardActions>
                                <Button size="small" color="error">Break Glass</Button>
                            </CardActions>
                        </Card>
                    </Grid>
                </Grid>
            </Container>
        </Box>
    );
};

export default Dashboard;
