import { createTheme } from '@mui/material/styles';

/**
 * MedShare Hub Application Theme
 * 
 * Primary: #2C5F8D (Deep Blue - Trust, Professionalism)
 * Secondary: #34A853 (Green - Growth, Health)
 * Error: #D32F2F (Red - Alert)
 * 
 * Typography: Inter (default), Roboto
 */
const theme = createTheme({
    palette: {
        primary: {
            main: '#2C5F8D',
            light: '#5E8BB9',
            dark: '#003663',
            contrastText: '#ffffff',
        },
        secondary: {
            main: '#34A853',
            light: '#6CCF82',
            dark: '#007926',
            contrastText: '#ffffff',
        },
        error: {
            main: '#D32F2F',
        },
        background: {
            default: '#F5F7FA',
            paper: '#ffffff',
        },
        text: {
            primary: '#1A2027',
            secondary: '#5E6B77',
        },
    },
    typography: {
        fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
        h1: {
            fontWeight: 700,
            fontSize: '2.5rem',
        },
        h2: {
            fontWeight: 600,
            fontSize: '2rem',
        },
        h3: {
            fontWeight: 600,
            fontSize: '1.75rem',
        },
        h6: {
            fontWeight: 600,
        },
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    textTransform: 'none',
                    borderRadius: 8,
                    fontWeight: 600,
                    padding: '8px 16px',
                },
                contained: {
                    boxShadow: 'none',
                    '&:hover': {
                        boxShadow: '0px 2px 4px rgba(0,0,0,0.2)',
                    },
                },
            },
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    borderRadius: 12,
                    boxShadow: '0px 4px 12px rgba(0,0,0,0.05)',
                },
            },
        },
        MuiTextField: {
            defaultProps: {
                variant: 'outlined',
                size: 'small',
            },
        },
    },
});

export default theme;
