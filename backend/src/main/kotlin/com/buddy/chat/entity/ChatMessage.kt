package com.buddy.chat.entity

import com.buddy.common.types.ChatMessageType
import com.buddy.global.entity.BaseEntity
import com.buddy.user.entity.User
import jakarta.persistence.*

@Entity
@Table(
    name = "chat_message",
    indexes = [
        Index(name = "idx_chat_message_room_timeline", columnList = "room_id, id")
    ]
)
open class ChatMessage : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "room_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_message_room")
    )
    lateinit var room: ChatRoom

    /** SYSTEM 메시지는 sender = null */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", foreignKey = ForeignKey(name = "fk_message_sender"))
    var sender: User? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    var type: ChatMessageType = ChatMessageType.TEXT

    @Column(name = "text", length = 2000)
    var text: String? = null

    @Column(name = "image_url", length = 512)
    var imageUrl: String? = null

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false
}