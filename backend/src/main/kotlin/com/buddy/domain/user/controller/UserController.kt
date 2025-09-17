package com.buddy.domain.user.controller

import com.buddy.common.Rq
import com.buddy.domain.user.dto.ProfileUpdateRequest
import com.buddy.domain.user.dto.UserProfileResponse
import com.buddy.domain.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
class UserController(
    private val userService: UserService,
    private val rq: Rq
) {

    @Operation(summary = "내 프로필 조회", description = "현재 로그인된 사용자의 프로필 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "프로필 조회 성공")
    @GetMapping("/me/profile")
    fun getMyProfile(): ResponseEntity<UserProfileResponse> {
        val userProfile = userService.getProfile(rq.getUserId())
        return ResponseEntity.ok(userProfile)
    }

    @Operation(
        summary = "내 프로필 업데이트",
        description = "최초 가입 후 프로필을 작성하거나, 기존 프로필을 수정합니다. 성공 시 사용자의 상태(status)가 ACTIVE로 변경됩니다."
    )
    @ApiResponse(responseCode = "200", description = "프로필 업데이트 성공")
    @ApiResponse(responseCode = "400", description = "요청 DTO의 필드가 누락되거나 형식이 맞지 않음")
    @PutMapping("/me/profile")
    fun updateMyProfile(@RequestBody request: ProfileUpdateRequest): ResponseEntity<UserProfileResponse> {
        val updatedProfile = userService.updateProfile(rq.getUserId(), request)
        return ResponseEntity.ok(updatedProfile)
    }
}
