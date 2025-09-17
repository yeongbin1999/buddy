package com.buddy.domain.notification.service

import com.buddy.domain.notification.entity.Notification
import com.buddy.domain.notification.repository.NotificationRepository
import com.buddy.domain.user.repository.UserRepository
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
class NotificationService(
  private val userRepo: UserRepository,
  private val notifRepo: NotificationRepository,
  private val messaging: SimpMessagingTemplate
) {
  @Transactional
  fun createAndSend(
    userId: Long,
    type: String,
    title: String,
    message: String,
    dataJson: String? = null
  ) {
    val user = userRepo.findById(userId).orElseThrow { IllegalArgumentException("USER_NOT_FOUND") }
    val saved = notifRepo.save(
      Notification(user = user, type = type, title = title, message = message, dataJson = dataJson)
    )
    // 접속 중이면 실시간 수신, 아니면 무시되고 DB만 남음
    messaging.convertAndSendToUser(userId.toString(), "/queue/notifications", saved.toDto())
  }

  fun unreadCount(userId: Long): Long = notifRepo.countByUserIdAndReadFalse(userId)

  @Transactional fun markRead(userId: Long, id: Long) { notifRepo.markRead(id, userId) }
  @Transactional fun markAllRead(userId: Long) { notifRepo.markAllRead(userId) }

  fun fetchUnread(userId: Long): List<NotificationDto> = notifRepo.findTop50ByUserIdAndReadFalseOrderByIdDesc(userId).map { it.toDto() }
}

data class NotificationDto(
  val id: Long, val type: String, val title: String, val message: String,
  val dataJson: String?, val read: Boolean, val createdAt: String
)

private fun Notification.toDto() = NotificationDto(
  id = this.id!!, type = this.type, title = this.title, message = this.message,
  dataJson = this.dataJson, read = this.read, createdAt = this.createdAt!!.format(DateTimeFormatter.ISO_DATE_TIME)
)