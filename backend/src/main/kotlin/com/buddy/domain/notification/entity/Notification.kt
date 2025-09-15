package com.buddy.domain.notification.entity

import com.buddy.common.NotificationType
import com.buddy.global.entity.BaseEntity
import com.buddy.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "notification",
    indexes = [
        Index(name = "idx_notification_receiver_read", columnList = "receiver_id, is_read")
    ]
)
open class Notification : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "receiver_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_notification_receiver")
    )
    lateinit var receiver: User

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 40)
    var type: NotificationType = NotificationType.GROUP_JOIN_REQUEST

    @Column(name = "ref_group_id", nullable = false)
    var refGroupId: Long = 0L

    @Column(name = "ref_user_id")
    var refUserId: Long? = null

    @Lob
    @Column(name = "payload", nullable = false, columnDefinition = "json")
    var payload: String = "{}"

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false

    @Column(name = "read_at")
    var readAt: LocalDateTime? = null
}
