package com.buddy.domain.post.controller

import com.buddy.common.Rq
import com.buddy.domain.group.repository.GroupMemberRepository
import com.buddy.domain.group.repository.GroupRepository
import com.buddy.domain.post.dto.CommentCreateRequest
import com.buddy.domain.post.dto.CommentResponse
import com.buddy.domain.post.dto.CommentUpdateRequest
import com.buddy.domain.post.service.CommentService
import com.buddy.domain.post.repository.PostRepository
import com.buddy.domain.user.repository.UserRepository
import com.buddy.enum.GroupMemberStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*
import java.net.URI

@Tag(name = "Comment", description = "댓글 관련 API")
@RestController
@RequestMapping("/api/v1/groups/{groupId}/posts/{postId}/comments")
@SecurityRequirement(name = "bearerAuth")
class CommentController(
    private val commentService: CommentService,
    private val rq: Rq,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val postRepository: PostRepository
) {

    @Operation(summary = "댓글 생성", description = "게시글에 새 댓글을 생성합니다. 그룹 멤버만 가능합니다.")
    @ApiResponse(responseCode = "201", description = "댓글 생성 성공")
    @ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    @PostMapping
    fun createComment(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @Parameter(description = "게시글 ID") @PathVariable postId: Long,
        @Valid @RequestBody request: CommentCreateRequest
    ): ResponseEntity<CommentResponse> {
        val commentResponse = commentService.createComment(rq.getUserId(), postId, request)
        return ResponseEntity.created(URI.create("/api/v1/groups/$groupId/posts/$postId/comments/${commentResponse.id}")).body(commentResponse)
    }

    @Operation(summary = "게시글 댓글 목록 조회", description = "특정 게시글의 댓글 목록을 조회합니다. 그룹 멤버만 가능합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "403", description = "그룹 멤버가 아님")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    @GetMapping
    fun getComments(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @Parameter(description = "게시글 ID") @PathVariable postId: Long
    ): ResponseEntity<List<CommentResponse>> {
        val userId = rq.getUserId()

        val group = groupRepository.findById(groupId)
            .orElseThrow { EntityNotFoundException("Group not found with ID: $groupId") }
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }

        val isMember = groupMemberRepository.findByUserAndGroupAndStatus(user, group, GroupMemberStatus.APPROVED).isPresent

        if (!isMember) {
            throw AccessDeniedException("User is not an approved member of this group.")
        }

        val comments = commentService.getCommentsByPostId(postId)
        return ResponseEntity.ok(comments)
    }

    @Operation(summary = "댓글 수정", description = "작성자가 댓글을 수정합니다. 작성자만 가능합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "403", description = "작성자가 아님")
    @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    @PutMapping("/{commentId}")
    fun updateComment(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @Parameter(description = "게시글 ID") @PathVariable postId: Long,
        @Parameter(description = "댓글 ID") @PathVariable commentId: Long,
        @Valid @RequestBody request: CommentUpdateRequest
    ): ResponseEntity<CommentResponse> {
        val commentResponse = commentService.updateComment(rq.getUserId(), commentId, request)
        return ResponseEntity.ok(commentResponse)
    }

    @Operation(summary = "댓글 삭제", description = "작성자가 댓글을 삭제합니다. 작성자만 가능합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "403", description = "작성자가 아님")
    @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @Parameter(description = "그룹 ID") @PathVariable groupId: Long,
        @Parameter(description = "게시글 ID") @PathVariable postId: Long,
        @Parameter(description = "댓글 ID") @PathVariable commentId: Long
    ): ResponseEntity<Void> {
        commentService.deleteComment(rq.getUserId(), commentId)
        return ResponseEntity.noContent().build()
    }
}
