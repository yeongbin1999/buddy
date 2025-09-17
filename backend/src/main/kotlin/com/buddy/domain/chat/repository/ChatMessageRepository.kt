package com.buddy.domain.chat.repository

import com.buddy.domain.chat.entity.ChatMessage
import com.buddy.domain.chat.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    fun findByRoomAndCreatedAtAfter(room: ChatRoom, createdAt: LocalDateTime): List<ChatMessage>
    fun findByRoomOrderByCreatedAtAsc(room: ChatRoom): List<ChatMessage>
}
