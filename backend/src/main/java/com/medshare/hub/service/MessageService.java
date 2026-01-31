package com.medshare.hub.service;

import com.medshare.hub.entity.Message;
import com.medshare.hub.entity.User;
import com.medshare.hub.repository.MessageRepository;
import com.medshare.hub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public List<Message> getInbox(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return messageRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public List<Message> getSentMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return messageRepository.findBySenderOrderByCreatedAtDesc(user);
    }

    @Transactional
    public Message sendMessage(Long senderId, Long recipientId, String subject, String body) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .subject(subject)
                .body(body)
                .build();

        return messageRepository.save(message);
    }

    @Transactional
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        messageRepository.save(message);
    }
}
