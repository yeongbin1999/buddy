package com.buddy.domain.post.service

import com.buddy.domain.group.repository.GroupMemberRepository
import com.buddy.domain.group.repository.GroupRepository
import com.buddy.domain.post.dto.PostCreateRequest
import com.buddy.domain.post.dto.PostResponse
import com.buddy.domain.post.dto.PostUpdateRequest
import com.buddy.domain.post.entity.Post
import com.buddy.domain.post.repository.PostRepository
import com.buddy.domain.user.repository.UserRepository
import com.buddy.enum.GroupMemberStatus
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val groupMemberRepository: GroupMemberRepository
) {

    @Transactional
    fun createPost(userId: Long, groupId: Long, request: PostCreateRequest): PostResponse {
        val author = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("Author not found with ID: $userId") }
        val group = groupRepository.findById(groupId)
            .orElseThrow { EntityNotFoundException("Group not found with ID: $groupId") }

        // 그룹 멤버만 게시글을 작성할 수 있도록 권한 확인
        val isMember = groupMemberRepository.findByUserAndGroupAndStatus(author, group, GroupMemberStatus.APPROVED).isPresent
        if (!isMember) {
            throw AccessDeniedException("User is not an approved member of this group.")
        }

        val post = Post(
            group = group,
            author = author,
            title = request.title,
            content = request.content,
            isDeleted = false
        )
        return postRepository.save(post).toResponse()
    }

    @Transactional(readOnly = true)
    fun getPosts(groupId: Long, pageable: Pageable): Page<PostResponse> {
        val group = groupRepository.findById(groupId)
            .orElseThrow { EntityNotFoundException("Group not found with ID: $groupId") }
        // TODO: 그룹 멤버만 게시글을 조회할 수 있도록 권한 확인 로직 추가 필요
        return postRepository.findByGroupAndIsDeletedFalseOrderByCreatedAtDesc(group, pageable)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getPostDetail(postId: Long): PostResponse {
        val post = postRepository.findByIdAndIsDeletedFalse(postId)
            ?: throw EntityNotFoundException("Post not found with ID: $postId")
        // TODO: 그룹 멤버만 게시글을 조회할 수 있도록 권한 확인 로직 추가 필요
        return post.toResponse()
    }

    @Transactional
    fun updatePost(userId: Long, postId: Long, request: PostUpdateRequest): PostResponse {
        val post = postRepository.findByIdAndIsDeletedFalse(postId)
            ?: throw EntityNotFoundException("Post not found with ID: $postId")

        if (post.author.id != userId) {
            throw AccessDeniedException("You are not the author of this post.")
        }

        post.apply {
            title = request.title
            content = request.content
        }
        return postRepository.save(post).toResponse()
    }

    @Transactional
    fun deletePost(userId: Long, postId: Long) {
        val post = postRepository.findByIdAndIsDeletedFalse(postId)
            ?: throw EntityNotFoundException("Post not found with ID: $postId")

        if (post.author.id != userId) {
            throw AccessDeniedException("You are not the author of this post.")
        }

        post.isDeleted = true
        postRepository.save(post)
    }

    private fun Post.toResponse() = PostResponse(
        id = this.id!!,
        groupId = this.group.id!!,
        authorId = this.author.id!!,
        authorName = this.author.name,
        title = this.title,
        content = this.content,
        createdAt = this.createdAt!!,
        updatedAt = this.updatedAt!!
    )
}
