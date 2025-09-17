package com.buddy.domain.user.dto

import com.buddy.enum.InterestType
import java.time.LocalDate

data class ProfileUpdateRequest(
    val name: String,
    val birthdate: LocalDate,
    val municipalityId: Long,
    val interests: List<InterestType>
)
