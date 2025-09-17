package com.buddy.domain.post.service

import com.buddy.domain.group.repository.GroupMemberRepository
import com.buddy.domain.post.dto.CommentCreateRequest
import com.buddy.domain.post.dto.CommentResponse
import com.buddy.domain.post.dto.CommentUpdateRequest
import com.buddy.domain.post.entity.Comment
import com.buddy.domain.post.repository.CommentRepository
import com.buddy.domain.post.repository.PostRepository
import com.buddy.domain.user.repository.UserRepository
import com.buddy.enum.GroupMemberStatus
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val groupMemberRepository: GroupMemberRepository
) {

    @Transactional
    fun createComment(userId: Long, postId: Long, request: CommentCreateRequest): CommentResponse {
        val author = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("Author not found with ID: $userId") }
        val post = postRepository.findByIdAndIsDeletedFalse(postId)
            ?: throw EntityNotFoundException("Post not found with ID: $postId")

        // 그룹 멤버만 댓글을 작성할 수 있도록 권한 확인
        val isMember = groupMemberRepository.findByUserAndGroupAndStatus(author, post.group, GroupMemberStatus.APPROVED).isPresent
        if (!isMember) {
            throw AccessDeniedException("User is not an approved member of this group.")
        }

        val parentComment = request.parentId?.let {
            commentRepository.findByIdAndIsDeletedFalse(it)
                ?: throw EntityNotFoundException("Parent comment not found with ID: $it")
        }

        val comment = Comment(
            post = post,
            author = author,
            content = request.content,
            parent = parentComment,
            isDeleted = false
        )
        return commentRepository.save(comment).toResponse()
    }

    @Transactional(readOnly = true)
    fun getCommentsByPostId(postId: Long): List<CommentResponse> {
        val post = postRepository.findByIdAndIsDeletedFalse(postId)
            ?: throw EntityNotFoundException("Post not found with ID: $postId")
        // TODO: 그룹 멤버만 댓글을 조회할 수 있도록 권한 확인 로직 추가 필요
        val comments = commentRepository.findByPostAndIsDeletedFalseOrderByCreatedAtAsc(post)
        return comments.filter { it.parent == null }.map { it.toResponseWithChildren() }
    }

    @Transactional
    fun updateComment(userId: Long, commentId: Long, request: CommentUpdateRequest): CommentResponse {
        val comment = commentRepository.findByIdAndIsDeletedFalse(commentId)
            ?: throw EntityNotFoundException("Comment not found with ID: $commentId")

        if (comment.author.id != userId) {
            throw AccessDeniedException("You are not the author of this comment.")
        }

        comment.apply {
            content = request.content
        }
        return commentRepository.save(comment).toResponse()
    }

    @Transactional
    fun deleteComment(userId: Long, commentId: Long) {
        val comment = commentRepository.findByIdAndIsDeletedFalse(commentId)
            ?: throw EntityNotFoundException("Comment not found with ID: $commentId")

        if (comment.author.id != userId) {
            throw AccessDeniedException("You are not the author of this comment.")
        }

        comment.isDeleted = true
        commentRepository.save(comment)
    }

    private fun Comment.toResponse(): CommentResponse {
        return CommentResponse(
            id = this.id!!,
            postId = this.post.id!!,
            authorId = this.author.id!!,
            authorName = this.author.name,
            parentId = this.parent?.id,
            content = this.content,
            createdAt = this.createdAt!!,
            updatedAt = this.updatedAt!!
        )
    }

    private fun Comment.toResponseWithChildren(): CommentResponse {
        val childrenComments = commentRepository.findByPostAndIsDeletedFalseOrderByCreatedAtAsc(this.post)
            .filter { it.parent?.id == this.id }
            .map { it.toResponseWithChildren() }
        return this.toResponse().copy(children = childrenComments)
    }
}
