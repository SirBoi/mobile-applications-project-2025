package com.project2025.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project2025.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatIdOrderByDatetimeAsc(Long chatId);

    List<Message> findBySenderIdOrderByDatetimeAsc(Long senderId);
}
