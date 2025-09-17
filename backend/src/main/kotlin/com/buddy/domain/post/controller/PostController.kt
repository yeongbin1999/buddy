package com.buddy.domain.post.controller

import com.buddy.common.Rq
import com.buddy.domain.group.repository.GroupMemberRepository
import com.buddy.domain.group.repository.GroupRepository
import com.buddy.domain.post.dto.PostCreateRequest
import com.buddy.domain.post.dto.PostResponse
import com.buddy.domain.post.dto.PostUpdateRequest
import com.buddy.domain.post.service.PostService
import com.buddy.domain.user.repository.UserRepository
import com.buddy.enum.GroupMemberStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*
import java.net.URI

@Tag(name = "Post", description = "게시글 관련 API")
@RestController
@RequestMapping("/api/v1/groups/{groupId}/posts")
@SecurityRequirement(name = "bearerAuth")
class PostController(
    private val postService: PostService,
    private val rq: Rq,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val groupMemberRepository: GroupMemberRepository
) {

    @Operation(summary = "게시글 생성", description = "그룹에 새 게시글을 생성합니다. 그룹 멤버만 가능합니다.")
    @ApiResponse(responseCode = "201", description = "게시글 생성 성공")
    @ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
    @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    @PostMapping
    fun createPost(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @Valid @RequestBody request: PostCreateRequest
    ): ResponseEntity<PostResponse> {
        val postResponse = postService.createPost(rq.getUserId(), groupId, request)
        return ResponseEntity.created(URI.create("/api/v1/groups/$groupId/posts/${postResponse.id}")).body(postResponse)
    }

    @Operation(summary = "그룹 게시글 목록 조회", description = "특정 그룹의 게시글 목록을 조회합니다. 그룹 멤버만 가능합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
    @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
    @GetMapping
    fun getPosts(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = org.springframework.data.domain.Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<PostResponse>> {
        val userId = rq.getUserId()

        val group = groupRepository.findById(groupId)
            .orElseThrow { EntityNotFoundException("Group not found with ID: $groupId") }
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }

        val isMember = groupMemberRepository.findByUserAndGroupAndStatus(user, group, GroupMemberStatus.APPROVED).isPresent

        if (!isMember) {
            throw AccessDeniedException("User is not an approved member of this group.")
        }

        val posts = postService.getPosts(groupId, pageable)
        return ResponseEntity.ok(posts)
    }

    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다. 그룹 멤버만 가능합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    @GetMapping("/{postId}")
    fun getPostDetail(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @Parameter(description = "게시글 ID") @PathVariable postId: Long
    ): ResponseEntity<PostResponse> {
        val userId = rq.getUserId()

        val group = groupRepository.findById(groupId)
            .orElseThrow { EntityNotFoundException("Group not found with ID: $groupId") }
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }

        val isMember = groupMemberRepository.findByUserAndGroupAndStatus(user, group, GroupMemberStatus.APPROVED).isPresent

        if (!isMember) {
            throw AccessDeniedException("User is not an approved member of this group.")
        }

        val post = postService.getPostDetail(postId)
        return ResponseEntity.ok(post)
    }

    @Operation(summary = "게시글 수정", description = "작성자가 게시글을 수정합니다. 작성자만 가능합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "403", description = "작성자가 아님")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    @PutMapping("/{postId}")
    fun updatePost(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @Parameter(description = "게시글 ID") @PathVariable postId: Long,
        @Valid @RequestBody request: PostUpdateRequest
    ): ResponseEntity<PostResponse> {
        val postResponse = postService.updatePost(rq.getUserId(), postId, request)
        return ResponseEntity.ok(postResponse)
    }

    @Operation(summary = "게시글 삭제", description = "작성자가 게시글을 삭제합니다. 작성자만 가능합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "403", description = "작성자가 아님")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    @DeleteMapping("/{postId}")
    fun deletePost(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @Parameter(description = "게시글 ID") @PathVariable postId: Long
    ): ResponseEntity<Void> {
        postService.deletePost(rq.getUserId(), postId)
        return ResponseEntity.noContent().build()
    }
}
