-- Create Messages Table
-- Secure internal messaging between patients and providers

CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    recipient_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Indices for efficient inbox/outbox lookups
CREATE INDEX idx_messages_recipient ON messages(recipient_id, created_at DESC);
CREATE INDEX idx_messages_sender ON messages(sender_id, created_at DESC);
CREATE INDEX idx_messages_unread ON messages(recipient_id, is_read);

-- Add comments for documentation
COMMENT ON TABLE messages IS 'Secure internal messaging for healthcare communication';
