package com.buddy.domain.user.dto

import com.buddy.enum.InterestType
import com.buddy.enum.UserStatus
import java.time.LocalDate

data class UserProfileResponse(
    val email: String,
    val name: String?,
    val profileImageUrl: String?,
    val birthdate: LocalDate?,
    val municipalityName: String?,
    val interests: Set<InterestType>,
    val status: UserStatus
)
