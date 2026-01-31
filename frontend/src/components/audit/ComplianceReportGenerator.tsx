import React, { useState } from 'react';
import {
    Box,
    Paper,
    Typography,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Alert,
    CircularProgress,
    Card,
    CardContent,
    Grid
} from '@mui/material';
import DescriptionIcon from '@mui/icons-material/Description';
import AssessmentIcon from '@mui/icons-material/Assessment';
import GavelIcon from '@mui/icons-material/Gavel';
import { ComplianceService, type ReportType } from '../../services/ComplianceService';

interface ComplianceReportGeneratorProps {
    patientId?: number;
}

const ComplianceReportGenerator: React.FC<ComplianceReportGeneratorProps> = ({ patientId }) => {
    const [reportType, setReportType] = useState<ReportType>('ACCESS_SUMMARY');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const handleGenerate = async () => {
        setLoading(true);
        setError(null);
        setSuccess(null);
        try {
            const result = await ComplianceService.generateReport(reportType, patientId);
            setSuccess(`Report generated successfully! Report ID: ${result.reportId}`);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to generate report.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box>
            <Typography variant="h6" gutterBottom>Compliance & Audit Reports</Typography>
            <Grid container spacing={3}>
                <Grid size={{ xs: 12, md: 7 }}>
                    <Paper sx={{ p: 3 }}>
                        <Typography variant="subtitle1" gutterBottom>Generate New Report</Typography>
                        <Typography variant="body2" color="textSecondary" paragraph>
                            Select a report type to generate a comprehensive compliance document.
                            Reports are generated in PDF format and include all relevant audit data.
                        </Typography>

                        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
                        {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

                        <FormControl fullWidth sx={{ mb: 3 }}>
                            <InputLabel>Report Type</InputLabel>
                            <Select
                                value={reportType}
                                label="Report Type"
                                onChange={(e) => setReportType(e.target.value as ReportType)}
                            >
                                <MenuItem value="ACCESS_SUMMARY">Complete Access Summary</MenuItem>
                                <MenuItem value="ANOMALY_REPORT">Security Anomaly Report</MenuItem>
                                <MenuItem value="CONSENT_AUDIT">Consent Management Audit</MenuItem>
                            </Select>
                        </FormControl>

                        <Button
                            variant="contained"
                            startIcon={loading ? <CircularProgress size={20} /> : <DescriptionIcon />}
                            onClick={handleGenerate}
                            disabled={loading}
                            fullWidth
                        >
                            {loading ? 'Generating...' : 'Generate PDF Report'}
                        </Button>
                    </Paper>
                </Grid>
                <Grid size={{ xs: 12, md: 5 }}>
                    <Card variant="outlined">
                        <CardContent>
                            <Typography variant="subtitle2" color="primary" gutterBottom>Available Report Formats</Typography>
                            <Box display="flex" alignItems="center" gap={1} mb={1}>
                                <AssessmentIcon fontSize="small" color="action" />
                                <Typography variant="body2">Executive Summary (PDF)</Typography>
                            </Box>
                            <Box display="flex" alignItems="center" gap={1} mb={1}>
                                <GavelIcon fontSize="small" color="action" />
                                <Typography variant="body2">Legal Compliance Audit (PDF)</Typography>
                            </Box>
                            <Typography variant="caption" color="textSecondary" sx={{ mt: 2, display: 'block' }}>
                                Reports are cryptographically signed to ensure data integrity.
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};

export default ComplianceReportGenerator;
