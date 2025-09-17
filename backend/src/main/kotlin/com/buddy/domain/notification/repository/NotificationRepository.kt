package com.buddy.domain.notification.repository

import com.buddy.domain.notification.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface NotificationRepository : JpaRepository<Notification, Long> {
  fun findTop50ByUserIdAndReadFalseOrderByIdDesc(userId: Long): List<Notification>
  fun countByUserIdAndReadFalse(userId: Long): Long

  @Modifying @Query("update Notification n set n.read=true, n.readAt = CURRENT_TIMESTAMP where n.id = :id and n.user.id = :userId and n.read=false")
  fun markRead(id: Long, userId: Long): Int

  @Modifying @Query("update Notification n set n.read=true, n.readAt = CURRENT_TIMESTAMP where n.user.id = :userId and n.read=false")
  fun markAllRead(userId: Long): Int
}