package com.buddy.domain.post.repository

import com.buddy.domain.post.entity.Comment
import com.buddy.domain.post.entity.Post
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByPostAndIsDeletedFalseOrderByCreatedAtAsc(post: Post): List<Comment>
    fun findByIdAndIsDeletedFalse(id: Long): Comment?
}
