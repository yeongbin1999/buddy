package com.buddy.domain.user.service

import com.buddy.domain.region.repository.RegionMunicipalityRepository
import com.buddy.domain.user.dto.ProfileUpdateRequest
import com.buddy.domain.user.dto.UserProfileResponse
import com.buddy.domain.user.entity.User
import com.buddy.domain.user.repository.UserRepository
import com.buddy.enum.UserStatus
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val regionMunicipalityRepository: RegionMunicipalityRepository
) {
    fun findById(id: Long): User = userRepository.findById(id).orElseThrow { EntityNotFoundException("User not found") }

    @Transactional(readOnly = true)
    fun getProfile(userId: Long): UserProfileResponse {
        val user = findById(userId)
        return user.toProfileResponse()
    }

    @Transactional
    fun updateProfile(userId: Long, request: ProfileUpdateRequest): UserProfileResponse {
        val user = findById(userId)
        val region = regionMunicipalityRepository.findById(request.municipalityId)
            .orElseThrow { EntityNotFoundException("Region not found") }

        user.apply {
            name = request.name
            birthdate = request.birthdate
            municipality = region
            interests.clear()
            interests.addAll(request.interests)
            status = UserStatus.ACTIVE // 프로필 작성이 완료되었으므로 상태를 ACTIVE로 변경
        }

        return userRepository.save(user).toProfileResponse()
    }

    private fun User.toProfileResponse() = UserProfileResponse(
        email = this.email,
        name = this.name,
        profileImageUrl = this.profileImageUrl,
        birthdate = this.birthdate,
        municipalityName = this.municipality?.name,
        interests = this.interests,
        status = this.status
    )
}
