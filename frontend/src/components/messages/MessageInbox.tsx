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
    CircularProgress,
    Alert
} from '@mui/material';
import MailIcon from '@mui/icons-material/Mail';
import DraftsIcon from '@mui/icons-material/Drafts';
import DeleteIcon from '@mui/icons-material/Delete';
import { MessageService, type Message } from '../../services/MessageService';

const MessageInbox = () => {
    const [messages, setMessages] = useState<Message[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchMessages = async () => {
            try {
                setLoading(true);
                const data = await MessageService.getInbox();
                setMessages(data);
            } catch (err) {
                console.error(err);
                setError('Failed to load messages.');
                setMessages([]);
            } finally {
                setLoading(false);
            }
        };
        fetchMessages();
    }, []);

    if (loading) return <Box display="flex" justifyContent="center" p={4}><CircularProgress /></Box>;
    if (error) return <Alert severity="error">{error}</Alert>;

    return (
        <Paper>
            <List>
                {messages.map((msg, index) => (
                    <React.Fragment key={msg.id}>
                        <ListItem
                            secondaryAction={
                                <IconButton edge="end" aria-label="delete">
                                    <DeleteIcon />
                                </IconButton>
                            }
                            sx={{ bgcolor: msg.isRead ? 'transparent' : 'action.hover' }}
                        >
                            <ListItemAvatar>
                                <Avatar sx={{ bgcolor: msg.isRead ? 'grey.300' : 'primary.main' }}>
                                    {msg.isRead ? <DraftsIcon /> : <MailIcon />}
                                </Avatar>
                            </ListItemAvatar>
                            <ListItemText
                                primary={
                                    <Box display="flex" justifyContent="space-between">
                                        <Typography variant="subtitle1" fontWeight={msg.isRead ? 'normal' : 'bold'}>
                                            {msg.subject}
                                        </Typography>
                                        <Typography variant="caption" color="textSecondary">
                                            {new Date(msg.createdAt).toLocaleDateString()}
                                        </Typography>
                                    </Box>
                                }
                                secondary={
                                    <>
                                        <Typography component="span" variant="body2" color="textPrimary">
                                            From: {msg.senderName}
                                        </Typography>
                                        {" — "}{msg.body.substring(0, 100)}{msg.body.length > 100 ? '...' : ''}
                                    </>
                                }
                            />
                        </ListItem>
                        {index < messages.length - 1 && <Divider component="li" />}
                    </React.Fragment>
                ))}
                {messages.length === 0 && (
                    <ListItem>
                        <ListItemText
                            primary="No messages in your inbox."
                            sx={{ textAlign: 'center', py: 3, color: 'text.secondary' }}
                        />
                    </ListItem>
                )}
            </List>
        </Paper>
    );
};

export default MessageInbox;
