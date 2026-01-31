import React from 'react';
import { Box, Typography } from '@mui/material';
import type { TypographyProps } from '@mui/material';

interface BrandTextProps extends TypographyProps {
    showHub?: boolean;
}

const BrandText: React.FC<BrandTextProps> = ({ showHub = true, sx, ...props }) => {
    return (
        <Typography
            {...props}
            sx={{
                fontWeight: 800,
                display: 'inline-flex',
                alignItems: 'center',
                letterSpacing: '-0.02em',
                ...sx
            }}
        >
            <Box component="span" sx={{ color: '#0B5ED7' }}>Med</Box>
            <Box component="span" sx={{ color: '#2FA84F' }}>Share</Box>
            {showHub && <Box component="span" sx={{ ml: 1, color: 'text.primary' }}>Hub</Box>}
        </Typography>
    );
};

export default BrandText;
