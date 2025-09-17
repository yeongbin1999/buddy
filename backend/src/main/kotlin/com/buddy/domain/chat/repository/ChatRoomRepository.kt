package com.buddy.domain.chat.repository

import com.buddy.domain.chat.entity.ChatRoom
import com.buddy.domain.group.entity.Group
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {
    fun findByGroup(group: Group): ChatRoom?
}
