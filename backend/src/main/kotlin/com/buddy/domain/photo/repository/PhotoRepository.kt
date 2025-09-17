package com.buddy.domain.photo.repository

import com.buddy.domain.group.entity.Group
import com.buddy.domain.photo.entity.Photo
import org.springframework.data.jpa.repository.JpaRepository

interface PhotoRepository : JpaRepository<Photo, Long> {
    fun findByGroupAndIsDeletedFalseOrderByCreatedAtDesc(group: Group): List<Photo>
}
