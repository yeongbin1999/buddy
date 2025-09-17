package com.buddy.domain.chat.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class WebRTCSignalingHandler(
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {

    // Map to store sessions by chatRoomId and userId
    private val chatRoomSessions: ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSession>> = ConcurrentHashMap()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val chatRoomId = session.attributes["chatRoomId"] as? String
        val userId = session.attributes["userId"] as? String

        if (chatRoomId != null && userId != null) {
            chatRoomSessions.computeIfAbsent(chatRoomId) { ConcurrentHashMap() }[userId] = session
            println("WebRTC Session established: chatRoomId=$chatRoomId, userId=$userId, sessionId=${session.id}")
            // Notify others in the room about new participant
            // This part will be implemented later
        } else {
            println("WebRTC Session established without chatRoomId or userId. Closing session: ${session.id}")
            session.close(CloseStatus.BAD_DATA)
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val chatRoomId = session.attributes["chatRoomId"] as? String
        val senderUserId = session.attributes["userId"] as? String

        if (chatRoomId == null || senderUserId == null) {
            println("Received message from session without chatRoomId or userId. Closing session: ${session.id}")
            session.close(CloseStatus.BAD_DATA)
            return
        }

        val payload = objectMapper.readTree(message.payload)
        val type = payload.get("type")?.asText()
        val targetUserId = payload.get("targetUserId")?.asText()

        println("WebRTC Message received: chatRoomId=$chatRoomId, sender=$senderUserId, type=$type, target=$targetUserId")

        when (type) {
            "offer", "answer", "candidate" -> {
                if (targetUserId != null) {
                    val targetSession = chatRoomSessions[chatRoomId]?.get(targetUserId)
                    if (targetSession != null && targetSession.isOpen) {
                        targetSession.sendMessage(message)
                        println("Forwarded $type from $senderUserId to $targetUserId in room $chatRoomId")
                    } else {
                        println("Target user $targetUserId not found or not open in room $chatRoomId")
                        // Optionally send error back to sender
                    }
                } else {
                    println("Signaling message $type received without targetUserId.")
                }
            }
            "join" -> {
                // A user explicitly joins a video call in a room
                // Notify existing participants about the new joiner
                chatRoomSessions[chatRoomId]?.forEach { (userId, s) ->
                    if (userId != senderUserId && s.isOpen) {
                        s.sendMessage(TextMessage(objectMapper.writeValueAsString(mapOf("type" to "new-participant", "userId" to senderUserId))))
                    }
                }
                println("User $senderUserId joined video call in room $chatRoomId")
            }
            "leave" -> {
                // A user explicitly leaves a video call in a room
                // Notify existing participants about the leaver
                chatRoomSessions[chatRoomId]?.forEach { (userId, s) ->
                    if (userId != senderUserId && s.isOpen) {
                        s.sendMessage(TextMessage(objectMapper.writeValueAsString(mapOf("type" to "participant-left", "userId" to senderUserId))))
                    }
                }
                println("User $senderUserId left video call in room $chatRoomId")
            }
            else -> {
                println("Unknown WebRTC message type: $type from $senderUserId in room $chatRoomId")
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        var removed = false
        chatRoomSessions.forEach { (chatRoomId, usersInRoom) ->
            usersInRoom.entries.removeIf { (userId, s) ->
                if (s.id == session.id) {
                    println("WebRTC Session closed: chatRoomId=$chatRoomId, userId=$userId, sessionId=${session.id}, status=$status")
                    // Notify others in the room about participant leaving
                    usersInRoom.forEach { (otherUserId, otherSession) ->
                        if (otherUserId != userId && otherSession.isOpen) {
                            otherSession.sendMessage(TextMessage(objectMapper.writeValueAsString(mapOf("type" to "participant-left", "userId" to userId))))
                        }
                    }
                    removed = true
                    true // Remove this entry
                } else {
                    false
                }
            }
            if (usersInRoom.isEmpty()) {
                chatRoomSessions.remove(chatRoomId)
            }
        }
        if (!removed) {
            println("WebRTC Session closed (not found in map): sessionId=${session.id}, status=$status")
        }
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        println("WebRTC Transport error for session ${session.id}: ${exception.message}")
        exception.printStackTrace()
    }
}
