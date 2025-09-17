package com.buddy.domain.chat.service

import com.buddy.domain.chat.entity.ChatRoom
import com.buddy.domain.chat.repository.ChatRoomRepository
import com.buddy.domain.group.entity.Group
import com.buddy.domain.group.repository.GroupRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val groupRepository: GroupRepository
) {

    @Transactional
    fun createChatRoom(group: Group): ChatRoom {
        val chatRoom = ChatRoom(group = group)
        return chatRoomRepository.save(chatRoom)
    }

    @Transactional(readOnly = true)
    fun getChatRoomByGroupId(groupId: Long): ChatRoom {
        val group = groupRepository.getReferenceById(groupId)
        return chatRoomRepository.findByGroup(group)
            ?: throw EntityNotFoundException("ChatRoom not found for group ID: $groupId")
    }

    @Transactional(readOnly = true)
    fun getChatRoomById(chatRoomId: Long): ChatRoom {
        return chatRoomRepository.findById(chatRoomId)
            .orElseThrow { EntityNotFoundException("ChatRoom not found with ID: $chatRoomId") }
    }
}
