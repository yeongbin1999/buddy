package com.buddy.domain.chat.controller

import com.buddy.common.Rq
import com.buddy.domain.chat.entity.ChatMessage
import com.buddy.domain.chat.service.ChatMessageService
import com.buddy.domain.chat.service.ChatRoomService
import com.buddy.domain.group.repository.GroupMemberRepository
import com.buddy.domain.group.repository.GroupRepository
import com.buddy.domain.user.repository.UserRepository
import com.buddy.enum.GroupMemberStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "ChatRoom", description = "채팅방 관련 API")
@RestController
@RequestMapping("/api/v1/chat-rooms")
@SecurityRequirement(name = "bearerAuth")
class ChatRoomController(
    private val chatRoomService: ChatRoomService,
    private val rq: Rq,
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val userRepository: UserRepository,
    private val chatMessageService: ChatMessageService
) {

    @Operation(summary = "그룹 ID로 채팅방 조회", description = "특정 그룹의 채팅방 정보를 조회합니다. 그룹 멤버만 접근 가능합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
    @ApiResponse(responseCode = "404", description = "해당 그룹 또는 채팅방을 찾을 수 없음")
    @GetMapping("/group/{groupId}")
    fun getChatRoomByGroupId(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long
    ): ResponseEntity<Long> { // ChatRoom ID를 반환
        val userId = rq.getUserId()

        val group = groupRepository.findById(groupId)
            .orElseThrow { EntityNotFoundException("Group not found with ID: $groupId") }
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }

        val isMember = groupMemberRepository.findByUserAndGroupAndStatus(user, group, GroupMemberStatus.APPROVED).isPresent

        if (!isMember) {
            throw AccessDeniedException("User is not an approved member of this group.")
        }

        val chatRoom = chatRoomService.getChatRoomByGroupId(groupId)
        return ResponseEntity.ok(chatRoom.id)
    }

    @Operation(summary = "채팅방 메시지 조회", description = "특정 채팅방의 메시지를 조회합니다. 그룹 가입 시점부터의 메시지만 조회됩니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
    @ApiResponse(responseCode = "404", description = "해당 채팅방을 찾을 수 없음")
    @GetMapping("/{chatRoomId}/messages")
    fun getChatMessages(
        @Parameter(description = "채팅방 ID") @PathVariable chatRoomId: Long
    ): ResponseEntity<List<ChatMessage>> {
        val userId = rq.getUserId()

        // Access control: Ensure user is an approved member of the group associated with this chat room
        val chatRoom = chatRoomService.getChatRoomById(chatRoomId) // Need to add getChatRoomById to ChatRoomService
        val group = chatRoom.group
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }

        val isMember = groupMemberRepository.findByUserAndGroupAndStatus(user, group, GroupMemberStatus.APPROVED).isPresent

        if (!isMember) {
            throw AccessDeniedException("User is not an approved member of this group.")
        }

        val messages = chatMessageService.getMessagesForUserInChatRoom(chatRoomId, userId)
        return ResponseEntity.ok(messages)
    }
}
