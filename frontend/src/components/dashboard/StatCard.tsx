import React from 'react';
import { Card, CardContent, Typography, Box, Button, CardActions } from '@mui/material';

interface StatCardProps {
    title: string;
    value: string | number;
    icon?: React.ReactNode;
    actionLabel?: string;
    onAction?: () => void;
    color?: string;
}

const StatCard: React.FC<StatCardProps> = ({ title, value, icon, actionLabel, onAction, color }) => {
    return (
        <Card sx={{ height: '100%' }}>
            <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <Box>
                        <Typography color="textSecondary" variant="overline" fontWeight="bold">
                            {title}
                        </Typography>
                        <Typography variant="h3" sx={{ mt: 1, color: color }}>
                            {value}
                        </Typography>
                    </Box>
                    {icon && <Box sx={{ color: color || 'primary.main' }}>{icon}</Box>}
                </Box>
            </CardContent>
            {actionLabel && (
                <CardActions>
                    <Button size="small" onClick={onAction} color={color === 'error' ? 'error' : 'primary'}>
                        {actionLabel}
                    </Button>
                </CardActions>
            )}
        </Card>
    );
};

export default StatCard;
