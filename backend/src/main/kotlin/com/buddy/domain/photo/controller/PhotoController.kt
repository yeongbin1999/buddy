package com.buddy.domain.photo.controller

import com.buddy.common.Rq
import com.buddy.domain.group.repository.GroupMemberRepository
import com.buddy.domain.group.repository.GroupRepository
import com.buddy.domain.photo.dto.PhotoCreateRequest
import com.buddy.domain.photo.dto.PhotoResponse
import com.buddy.domain.photo.service.PhotoService
import com.buddy.domain.user.repository.UserRepository
import com.buddy.enum.GroupMemberStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*
import java.net.URI

@Tag(name = "Photo", description = "그룹 사진 관련 API")
@RestController
@RequestMapping("/api/v1/groups/{groupId}/photos")
@SecurityRequirement(name = "bearerAuth")
class PhotoController(
    private val photoService: PhotoService,
    private val rq: Rq,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val groupMemberRepository: GroupMemberRepository
) {

    @Operation(summary = "그룹 사진 업로드", description = "그룹에 사진을 업로드합니다. 그룹 멤버만 가능합니다.")
    @ApiResponse(responseCode = "201", description = "사진 업로드 성공")
    @ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
    @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    @PostMapping
    fun uploadPhoto(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @RequestBody request: PhotoCreateRequest
    ): ResponseEntity<PhotoResponse> {
        val photoResponse = photoService.uploadPhoto(rq.getUserId(), groupId, request)
        return ResponseEntity.created(URI.create("/api/v1/groups/$groupId/photos/${photoResponse.id}")).body(photoResponse)
    }

    @Operation(summary = "그룹 사진 목록 조회", description = "특정 그룹의 사진 목록을 조회합니다. 그룹 멤버만 가능합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
    @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    @GetMapping
    fun getGroupPhotos(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long
    ): ResponseEntity<List<PhotoResponse>> {
        val userId = rq.getUserId()

        val group = groupRepository.findById(groupId)
            .orElseThrow { EntityNotFoundException("Group not found with ID: $groupId") }
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }

        val isMember = groupMemberRepository.findByUserAndGroupAndStatus(user, group, GroupMemberStatus.APPROVED).isPresent

        if (!isMember) {
            throw AccessDeniedException("User is not an approved member of this group.")
        }

        val photos = photoService.getGroupPhotos(groupId)
        return ResponseEntity.ok(photos)
    }

    @Operation(summary = "그룹 사진 삭제", description = "업로더가 그룹 사진을 삭제합니다. 업로더만 가능합니다.")
    @ApiResponse(responseCode = "204", description = "사진 삭제 성공")
    @ApiResponse(responseCode = "403", description = "업로더가 아님")
    @ApiResponse(responseCode = "404", description = "사진을 찾을 수 없음")
    @DeleteMapping("/{photoId}")
    fun deletePhoto(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @Parameter(description = "사진 ID") @PathVariable photoId: Long
    ): ResponseEntity<Void> {
        photoService.deletePhoto(rq.getUserId(), photoId)
        return ResponseEntity.noContent().build()
    }
}
