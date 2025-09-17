package com.buddy.domain.group.repository

import com.buddy.domain.group.entity.Group
import com.buddy.domain.region.entity.RegionMunicipality
import com.buddy.domain.user.entity.User
import com.buddy.enum.GroupMemberStatus
import com.buddy.enum.InterestType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GroupRepository : JpaRepository<Group, Long> {
    fun findByMembersUserAndMembersStatus(user: User, status: GroupMemberStatus): List<Group>

    fun findByInterest(interest: InterestType): List<Group>
    fun findByMunicipality(municipality: RegionMunicipality): List<Group>
    fun findByInterestAndMunicipality(interest: InterestType, municipality: RegionMunicipality): List<Group>

    @Query("""
        SELECT g FROM Group g
        WHERE g.id NOT IN (SELECT gm.group.id FROM GroupMember gm WHERE gm.user.id = :userId)
        AND (
            (g.interest IN :userInterests AND g.municipality = :userMunicipality)
            OR
            (g.interest IN :userInterests AND g.municipality != :userMunicipality)
            OR
            (g.interest NOT IN :userInterests AND g.municipality = :userMunicipality)
        )
        ORDER BY
            CASE
                WHEN (g.interest IN :userInterests AND g.municipality = :userMunicipality) THEN 1
                WHEN (g.interest IN :userInterests OR g.municipality = :userMunicipality) THEN 2
                ELSE 3
            END,
            g.createdAt DESC
    """)
    fun findRecommendedGroups(
        @Param("userId") userId: Long,
        @Param("userInterests") userInterests: Set<InterestType>,
        @Param("userMunicipality") userMunicipality: RegionMunicipality
    ): List<Group>
}
