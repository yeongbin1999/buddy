package com.buddy.domain.chat.entity

import com.buddy.global.entity.BaseEntity
import com.buddy.domain.group.entity.Group
import jakarta.persistence.*

@Entity
@Table(
    name = "chat_room",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_chat_room_group",
            columnNames = ["group_id"]
        )
    ]
)
open class ChatRoom(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "group_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_chat_room_group")
    )
    var group: Group
) : BaseEntity() {
}
