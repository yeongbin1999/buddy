package com.buddy.domain.photo.service

import com.buddy.domain.group.repository.GroupMemberRepository
import com.buddy.domain.group.repository.GroupRepository
import com.buddy.domain.photo.dto.PhotoCreateRequest
import com.buddy.domain.photo.dto.PhotoResponse
import com.buddy.domain.photo.entity.Photo
import com.buddy.domain.photo.repository.PhotoRepository
import com.buddy.domain.user.repository.UserRepository
import com.buddy.enum.GroupMemberStatus
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PhotoService(
    private val photoRepository: PhotoRepository,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val groupMemberRepository: GroupMemberRepository
) {

    @Transactional
    fun uploadPhoto(userId: Long, groupId: Long, request: PhotoCreateRequest): PhotoResponse {
        val uploader = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("Uploader not found with ID: $userId") }
        val group = groupRepository.findById(groupId)
            .orElseThrow { EntityNotFoundException("Group not found with ID: $groupId") }

        // 그룹 멤버만 사진을 업로드할 수 있도록 권한 확인
        val isMember = groupMemberRepository.findByUserAndGroupAndStatus(uploader, group, GroupMemberStatus.APPROVED).isPresent
        if (!isMember) {
            throw AccessDeniedException("User is not an approved member of this group.")
        }

        val photo = Photo(
            group = group,
            uploader = uploader,
            imageUrl = request.imageUrl,
            caption = request.caption
        )
        return photoRepository.save(photo).toResponse()
    }

    @Transactional(readOnly = true)
    fun getGroupPhotos(groupId: Long): List<PhotoResponse> {
        val group = groupRepository.findById(groupId)
            .orElseThrow { EntityNotFoundException("Group not found with ID: $groupId") }
        return photoRepository.findByGroupAndIsDeletedFalseOrderByCreatedAtDesc(group)
            .map { it.toResponse() }
    }

    @Transactional
    fun deletePhoto(userId: Long, photoId: Long) {
        val photo = photoRepository.findById(photoId)
            .orElseThrow { EntityNotFoundException("Photo not found with ID: $photoId") }

        if (photo.uploader.id != userId) {
            throw AccessDeniedException("You are not the uploader of this photo.")
        }

        photo.isDeleted = true
        photoRepository.save(photo)
    }

    private fun Photo.toResponse() = PhotoResponse(
        id = this.id!!,
        groupId = this.group.id!!,
        uploaderId = this.uploader.id!!,
        uploaderName = this.uploader.name,
        imageUrl = this.imageUrl,
        caption = this.caption,
        createdAt = this.createdAt!!
    )
}
