package com.hobbyconnect.repository;

import com.hobbyconnect.model.Message;
import com.hobbyconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m JOIN FETCH m.sender JOIN FETCH m.receiver " +
           "WHERE (m.sender.id = :u1 AND m.receiver.id = :u2) " +
           "   OR (m.sender.id = :u2 AND m.receiver.id = :u1) " +
           "ORDER BY m.sentAt ASC")
    List<Message> findConversationBetween(@Param("u1") Long u1, @Param("u2") Long u2);

    @Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.receiver ELSE m.sender END " +
           "FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<User> findChatPartners(@Param("userId") Long userId);

    long countByReceiverIdAndIsReadFalse(Long receiverId);
}
