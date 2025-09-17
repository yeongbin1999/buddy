package com.buddy.domain.notification.controller

import com.buddy.domain.notification.service.NotificationDto
import com.buddy.domain.notification.service.NotificationService
import com.buddy.global.dto.RsData
import com.buddy.global.extension.toSuccessResponse
import com.buddy.global.extension.toSuccessResponseWithoutData
import com.buddy.security.AuthSupport
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
  private val service: NotificationService
) {
  private fun currentUserId(): Long = AuthSupport.currentUserId()

  /** 앱 진입/재접속 시 미수신분 동기화 */
  @GetMapping("/unread")
  fun unread(): ResponseEntity<RsData<List<NotificationDto>>> =
    service.fetchUnread(currentUserId()).toSuccessResponse("미읽음 알림")

  /** 배지 카운트 */
  @GetMapping("/badge-count")
  fun badgeCount(): ResponseEntity<RsData<Map<String, Long>>> =
    mapOf("count" to service.unreadCount(currentUserId())).toSuccessResponse("배지 카운트")

  /** 단건 읽음 */
  @PostMapping("/{id}/read")
  fun read(@PathVariable id: Long): ResponseEntity<RsData<Nothing>> {
    service.markRead(currentUserId(), id)
    return toSuccessResponseWithoutData("읽음 처리")
  }

  /** 모두 읽음 */
  @PostMapping("/read-all")
  fun readAll(): ResponseEntity<RsData<Nothing>> {
    service.markAllRead(currentUserId())
    return toSuccessResponseWithoutData("전체 읽음 처리")
  }
}