package com.buddy.domain.notification.entity

import com.buddy.domain.user.entity.User
import com.buddy.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
  name = "notification",
  indexes = [
    Index(name = "idx_notif_user_read", columnList = "user_id, `read`"),
    Index(name = "idx_notif_user_created", columnList = "user_id, created_at")
  ]
)
open class Notification(
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name="fk_notif_user"))
  var user: User,

  @Column(nullable = false, length = 80)
  var type: String, // e.g. "GROUP_JOIN_REQUESTED"

  @Column(nullable = false, length = 100)
  var title: String,

  @Column(nullable = false, length = 500)
  var message: String,

  @Column(name = "data_json", columnDefinition = "TEXT")
  var dataJson: String? = null,

  @Column(name = "`read`", nullable = false)
  var read: Boolean = false
) : BaseEntity() {
  @Column(name="read_at") var readAt: LocalDateTime? = null
}