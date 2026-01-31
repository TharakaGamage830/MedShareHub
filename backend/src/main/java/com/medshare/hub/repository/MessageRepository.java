package com.medshare.hub.repository;

import com.medshare.hub.entity.Message;
import com.medshare.hub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRecipientOrderByCreatedAtDesc(User recipient);

    List<Message> findBySenderOrderByCreatedAtDesc(User sender);
}
