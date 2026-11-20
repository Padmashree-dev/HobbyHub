package com.hobbyconnect.service;

import com.hobbyconnect.model.Message;
import com.hobbyconnect.model.User;
import com.hobbyconnect.repository.MessageRepository;
import com.hobbyconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public Message sendMessage(Long senderId, Long receiverId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty.");
        }
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("You cannot send a message to yourself.");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found with id: " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found with id: " + receiverId));

        Message message = new Message(content.trim(), sender, receiver);
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getConversation(Long user1Id, Long user2Id) {
        return messageRepository.findConversationBetween(user1Id, user2Id);
    }

    @Transactional(readOnly = true)
    public List<User> getChatPartners(Long userId) {
        return messageRepository.findChatPartners(userId);
    }

    public void markMessagesAsRead(Long currentUserId, Long partnerId) {
        List<Message> messages = messageRepository.findConversationBetween(currentUserId, partnerId);
        for (Message m : messages) {
            if (m.getReceiver().getId().equals(currentUserId) && !m.isRead()) {
                m.setRead(true);
                messageRepository.save(m);
            }
        }
    }
}
