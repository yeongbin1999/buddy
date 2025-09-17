package com.buddy.domain.notification.controller

import com.buddy.domain.notification.service.NotificationDto
import com.buddy.domain.notification.service.NotificationService
import com.buddy.common.RsData
import com.buddy.global.extension.toSuccessResponse
import com.buddy.global.extension.toSuccessResponseWithoutData
import com.buddy.common.Rq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Notification", description = "알림 관련 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/notifications")
class NotificationController(
  private val service: NotificationService,
  private val rq: Rq
) {
  private fun currentUserId(): Long = rq.getUserId()

  @Operation(
      summary = "미읽음 알림 조회",
      description = "앱 진입/재접속 시 미수신 알림을 동기화하고 조회합니다."
  )
  @ApiResponse(responseCode = "200", description = "미읽음 알림 조회 성공")
  @ApiResponse(responseCode = "401", description = "인증 실패")
  /** 앱 진입/재접속 시 미수신분 동기화 */
  @GetMapping("/unread")
  fun unread(): ResponseEntity<RsData<List<NotificationDto>>> =
    service.fetchUnread(currentUserId()).toSuccessResponse("미읽음 알림")

  @Operation(
      summary = "미읽음 알림 개수 조회",
      description = "현재 사용자의 미읽음 알림 개수를 조회하여 배지 카운트에 사용합니다."
  )
  @ApiResponse(responseCode = "200", description = "미읽음 알림 개수 조회 성공")
  @ApiResponse(responseCode = "401", description = "인증 실패")
  /** 배지 카운트 */
  @GetMapping("/badge-count")
  fun badgeCount(): ResponseEntity<RsData<Map<String, Long>>> =
    mapOf("count" to service.unreadCount(currentUserId())).toSuccessResponse("배지 카운트")

  @Operation(
      summary = "단건 알림 읽음 처리",
      description = "특정 알림을 읽음 상태로 변경합니다."
  )
  @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공")
  @ApiResponse(responseCode = "401", description = "인증 실패")
  @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음")
  /** 단건 읽음 */
  @PostMapping("/{id}/read")
  fun read(@Parameter(description = "알림 ID") @PathVariable id: Long): ResponseEntity<RsData<Nothing>> {
    service.markRead(currentUserId(), id)
    return toSuccessResponseWithoutData("읽음 처리")
  }

  @Operation(
      summary = "모든 알림 읽음 처리",
      description = "현재 사용자의 모든 미읽음 알림을 읽음 상태로 변경합니다."
  )
  @ApiResponse(responseCode = "200", description = "모든 알림 읽음 처리 성공")
  @ApiResponse(responseCode = "401", description = "인증 실패")
  /** 모두 읽음 */
  @PostMapping("/read-all")
  fun readAll(): ResponseEntity<RsData<Nothing>> {
    service.markAllRead(currentUserId())
    return toSuccessResponseWithoutData("전체 읽음 처리")
  }
}