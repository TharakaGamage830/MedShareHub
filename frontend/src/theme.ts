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
            main: '#0B5ED7', // Tech Blue
            light: '#42a5f5',
            dark: '#084297',
            contrastText: '#ffffff',
        },
        secondary: {
            main: '#2FA84F', // Healthcare Green
            light: '#5eba70',
            dark: '#1e6b32',
            contrastText: '#ffffff',
        },
        error: {
            main: '#d32f2f',
        },
        background: {
            default: '#f8f9fa',
            paper: '#ffffff',
        },
        text: {
            primary: '#2d3436',
            secondary: '#636e72',
        },
    },
    typography: {
        fontFamily: '"Inter", "Segoe UI", "Roboto", "Helvetica", "Arial", sans-serif',
        h1: {
            fontWeight: 800,
            fontSize: '2.75rem',
            letterSpacing: '-0.02em',
        },
        h2: {
            fontWeight: 700,
            fontSize: '2.25rem',
            letterSpacing: '-0.01em',
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
