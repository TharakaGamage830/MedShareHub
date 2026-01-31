package com.medshare.hub.controller;

import com.medshare.hub.entity.Message;
import com.medshare.hub.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // In production, restrict this
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/inbox")
    public ResponseEntity<List<Message>> getInbox(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(messageService.getInbox(userId));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<Message>> getSentMessages(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(messageService.getSentMessages(userId));
    }

    @PostMapping
    public ResponseEntity<Message> sendMessage(
            @RequestAttribute("userId") Long senderId,
            @RequestBody Map<String, Object> payload) {

        Long recipientId = ((Number) payload.get("recipientId")).longValue();
        String subject = (String) payload.get("subject");
        String body = (String) payload.get("body");

        return ResponseEntity.ok(messageService.sendMessage(senderId, recipientId, subject, body));
    }

    @PatchMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.noContent().build();
    }
}
