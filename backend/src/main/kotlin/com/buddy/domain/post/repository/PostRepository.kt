package com.buddy.domain.post.repository

import com.buddy.domain.group.entity.Group
import com.buddy.domain.post.entity.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long> {
    fun findByGroupAndIsDeletedFalseOrderByCreatedAtDesc(group: Group, pageable: Pageable): Page<Post>
    fun findByIdAndIsDeletedFalse(id: Long): Post?
}
