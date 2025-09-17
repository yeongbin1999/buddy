package com.buddy.domain.group.controller

import com.buddy.common.Rq
import com.buddy.domain.group.dto.GroupCreateRequest
import com.buddy.domain.group.dto.GroupDetailResponse
import com.buddy.domain.group.dto.GroupResponse
import com.buddy.domain.group.dto.GroupUpdateRequest
import com.buddy.domain.group.service.GroupService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@Tag(name = "Group", description = "그룹 관련 API")
@RestController
@RequestMapping("/api/v1/groups")
@SecurityRequirement(name = "bearerAuth")
class GroupController(
    private val groupService: GroupService,
    private val rq: Rq
) {

    @Operation(summary = "그룹 생성", description = "새로운 그룹을 생성합니다. 프로필을 작성한 ACTIVE 상태의 유저만 가능합니다.")
    @ApiResponse(responseCode = "201", description = "그룹 생성 성공")
    @ApiResponse(responseCode = "400", description = "요청 DTO의 필드가 누락되거나 형식이 맞지 않음")
    @ApiResponse(responseCode = "403", description = "프로필을 작성하지 않은 경우")
    @PostMapping
    fun createGroup(@RequestBody request: GroupCreateRequest): ResponseEntity<GroupResponse> {
        val groupResponse = groupService.createGroup(rq.getUserId(), request)
        return ResponseEntity.created(URI.create("/api/v1/groups/${groupResponse.id}")).body(groupResponse)
    }

    @Operation(summary = "그룹 목록 조회", description = "그룹 목록을 조회합니다. 관심사 또는 지역으로 필터링할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    fun getGroups(
        @Parameter(description = "관심사 (예: SPORTS, STUDY)") @RequestParam(required = false) interest: String?,
        @Parameter(description = "지역 ID") @RequestParam(required = false) regionId: Long?
    ): ResponseEntity<List<GroupResponse>> {
        val groups = groupService.getGroups(interest, regionId)
        return ResponseEntity.ok(groups)
    }

    @Operation(summary = "그룹 상세 조회", description = "특정 그룹의 상세 정보(멤버 목록 포함)를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 그룹을 찾을 수 없음")
    @GetMapping("/{groupId}")
    fun getGroupDetails(@Parameter(description = "그룹 ID") @PathVariable groupId: Long): ResponseEntity<GroupDetailResponse> {
        val groupDetails = groupService.getGroupDetails(groupId)
        return ResponseEntity.ok(groupDetails)
    }

    @Operation(summary = "그룹 정보 수정", description = "그룹장이 그룹 정보를 수정합니다. 그룹장만 가능합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "403", description = "그룹장이 아닌 경우")
    @ApiResponse(responseCode = "404", description = "해당 그룹을 찾을 수 없음")
    @PutMapping("/{groupId}")
    fun updateGroup(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @RequestBody request: GroupUpdateRequest
    ): ResponseEntity<GroupResponse> {
        val groupResponse = groupService.updateGroup(rq.getUserId(), groupId, request)
        return ResponseEntity.ok(groupResponse)
    }

    @Operation(summary = "그룹 삭제", description = "그룹장이 그룹을 삭제합니다. 그룹장만 가능합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "403", description = "그룹장이 아닌 경우")
    @ApiResponse(responseCode = "404", description = "해당 그룹을 찾을 수 없음")
    @DeleteMapping("/{groupId}")
    fun deleteGroup(@Parameter(description = "그룹 ID") @PathVariable groupId: Long): ResponseEntity<Void> {
        groupService.deleteGroup(rq.getUserId(), groupId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "그룹 탈퇴", description = "멤버가 그룹을 탈퇴합니다. 그룹장은 탈퇴할 수 없습니다.")
    @ApiResponse(responseCode = "200", description = "탈퇴 성공")
    @ApiResponse(responseCode = "403", description = "프로필을 작성하지 않았거나 그룹장이 탈퇴 시도")
    @ApiResponse(responseCode = "404", description = "그룹 또는 멤버 정보를 찾을 수 없음")
    @PostMapping("/{groupId}/leave")
    fun leaveGroup(@Parameter(description = "그룹 ID") @PathVariable groupId: Long): ResponseEntity<Void> {
        groupService.leaveGroup(rq.getUserId(), groupId)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "그룹 멤버 강퇴", description = "그룹장이 멤버를 강퇴시킵니다. 그룹장만 가능합니다.")
    @ApiResponse(responseCode = "204", description = "강퇴 성공")
    @ApiResponse(responseCode = "403", description = "그룹장이 아닌 경우")
    @ApiResponse(responseCode = "404", description = "그룹 또는 멤버 정보를 찾을 수 없음")
    @DeleteMapping("/{groupId}/members/{memberUserId}")
    fun kickMember(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @Parameter(description = "강퇴할 멤버의 User ID") @PathVariable memberUserId: Long
    ): ResponseEntity<Void> {
        groupService.kickMember(rq.getUserId(), groupId, memberUserId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "내 그룹 목록 조회", description = "자신이 가입한 그룹 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/my")
    fun getMyGroups(): ResponseEntity<List<GroupResponse>> {
        val groups = groupService.getMyGroups(rq.getUserId())
        return ResponseEntity.ok(groups)
    }

    @Operation(summary = "추천 그룹 목록 조회", description = "자신의 관심사 및 지역 기반 추천 그룹 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/recommend")
    fun getRecommendedGroups(): ResponseEntity<List<GroupResponse>> {
        val groups = groupService.getRecommendedGroups(rq.getUserId())
        return ResponseEntity.ok(groups)
    }

    @Operation(summary = "그룹 가입 신청", description = "그룹에 가입을 신청합니다. 이미 가입 신청했거나 가입된 상태면 실패합니다.")
    @ApiResponse(responseCode = "200", description = "가입 신청 성공")
    @ApiResponse(responseCode = "403", description = "프로필을 작성하지 않은 경우")
    @PostMapping("/{groupId}/join")
    fun requestToJoinGroup(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long
    ): ResponseEntity<Void> {
        groupService.requestToJoinGroup(rq.getUserId(), groupId)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "그룹 가입 승인", description = "그룹장이 가입 신청을 승인합니다. 그룹장만 가능합니다.")
    @ApiResponse(responseCode = "200", description = "승인 성공")
    @ApiResponse(responseCode = "403", description = "그룹장이 아닌 경우")
    @PostMapping("/members/{memberId}/approve")
    fun approveJoinRequest(
        @Parameter(description = "GroupMember ID") @PathVariable memberId: Long
    ): ResponseEntity<Void> {
        groupService.approveJoinRequest(rq.getUserId(), memberId)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "그룹 가입 거절", description = "그룹장이 가입 신청을 거절합니다. 그룹장만 가능합니다.")
    @ApiResponse(responseCode = "200", description = "거절 성공")
    @ApiResponse(responseCode = "403", description = "그룹장이 아닌 경우")
    @PostMapping("/members/{memberId}/reject")
    fun rejectJoinRequest(
        @Parameter(description = "GroupMember ID") @PathVariable memberId: Long
    ): ResponseEntity<Void> {
        groupService.rejectJoinRequest(rq.getUserId(), memberId)
        return ResponseEntity.ok().build()
    }
}
