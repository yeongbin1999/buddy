package com.buddy.domain.chat.service

import com.buddy.domain.chat.entity.ChatMessage
import com.buddy.domain.chat.entity.ChatRoom
import com.buddy.domain.chat.repository.ChatMessageRepository
import com.buddy.domain.chat.repository.ChatRoomRepository
import com.buddy.domain.group.repository.GroupMemberRepository
import com.buddy.domain.user.entity.User
import com.buddy.domain.user.repository.UserRepository
import com.buddy.enum.ChatMessageType
import com.buddy.enum.GroupMemberStatus
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ChatMessageService(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createAndSaveMessage(
        room: ChatRoom,
        sender: User?,
        type: ChatMessageType,
        text: String?
    ): ChatMessage {
        val chatMessage = ChatMessage().apply {
            this.room = room
            this.sender = sender
            this.type = type
            this.text = text
        }
        return chatMessageRepository.save(chatMessage)
    }

    @Transactional(readOnly = true)
    fun getMessagesForUserInChatRoom(chatRoomId: Long, userId: Long): List<ChatMessage> {
        val chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow { EntityNotFoundException("ChatRoom not found with ID: $chatRoomId") }
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }

        val groupMember = groupMemberRepository.findByUserAndGroupAndStatus(
            user,
            chatRoom.group,
            GroupMemberStatus.APPROVED
        ).orElseThrow { EntityNotFoundException("User is not an approved member of this group.") }

        // 그룹 가입 승인 시간 (BaseEntity의 updatedAt 사용)
        val joinedAt = groupMember.updatedAt ?: LocalDateTime.MIN

        return chatMessageRepository.findByRoomAndCreatedAtAfter(chatRoom, joinedAt)
    }
}
