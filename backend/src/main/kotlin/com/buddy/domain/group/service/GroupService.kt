package com.buddy.domain.group.service

import com.buddy.domain.chat.service.ChatMessageService
import com.buddy.domain.chat.service.ChatRoomService
import com.buddy.domain.group.dto.*
import com.buddy.domain.group.entity.Group
import com.buddy.domain.group.entity.GroupMember
import com.buddy.domain.group.repository.GroupMemberRepository
import com.buddy.domain.group.repository.GroupRepository
import com.buddy.domain.notification.service.NotificationService
import com.buddy.domain.region.repository.RegionMunicipalityRepository
import com.buddy.domain.user.entity.User
import com.buddy.domain.user.repository.UserRepository
import com.buddy.enum.*
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val userRepository: UserRepository,
    private val regionMunicipalityRepository: RegionMunicipalityRepository,
    private val notificationService: NotificationService,
    private val chatRoomService: ChatRoomService,
    private val chatMessageService: ChatMessageService
) {

    @Transactional
    fun createGroup(userId: Long, request: GroupCreateRequest): GroupResponse {
        val owner = getUserIfActive(userId)
        val region = regionMunicipalityRepository.findById(request.regionId)
            .orElseThrow { EntityNotFoundException("지역을 찾을 수 없습니다.") }

        // 최소/최대 인원수 유효성 검사
        if (request.minMemberCount > request.maxMemberCount) {
            throw IllegalArgumentException("최소 인원수는 최대 인원수보다 클 수 없습니다.")
        }

        val group = Group(
            title = request.title,
            description = request.description,
            imageUrl = request.imageUrl,
            interest = InterestType.valueOf(request.interest),
            municipality = region,
            owner = owner,
            minMemberCount = request.minMemberCount,
            maxMemberCount = request.maxMemberCount
        )

        val chatRoom = chatRoomService.createChatRoom(group)
        group.chatRoom = chatRoom

        val groupMember = GroupMember(
            group = group,
            user = owner,
            role = GroupRole.OWNER,
            status = GroupMemberStatus.APPROVED
        )
        group.members.add(groupMember)

        // 그룹 생성 시 그룹장에게 시스템 메시지 전송
        chatMessageService.createAndSaveMessage(
            room = chatRoom,
            sender = null, // 시스템 메시지이므로 sender는 null
            type = ChatMessageType.SYSTEM,
            text = "${owner.name ?: "그룹장"}님이 그룹을 생성하고 채팅방에 입장했습니다."
        )

        return groupRepository.save(group).toResponse()
    }

    @Transactional(readOnly = true)
    fun getGroupDetails(groupId: Long): GroupDetailResponse {
        val group = groupRepository.findById(groupId).orElseThrow { EntityNotFoundException("그룹을 찾을 수 없습니다.") }
        return group.toDetailResponse()
    }

    @Transactional
    fun updateGroup(userId: Long, groupId: Long, request: GroupUpdateRequest): GroupResponse {
        val owner = getUserIfActive(userId)
        val group = groupRepository.findById(groupId).orElseThrow { EntityNotFoundException("그룹을 찾을 수 없습니다.") }
        if (group.owner.id != owner.id) {
            throw AccessDeniedException("You are not the owner of this group.")
        }
        val region = regionMunicipalityRepository.findById(request.regionId)
            .orElseThrow { EntityNotFoundException("지역을 찾을 수 없습니다.") }

        group.apply {
            title = request.title
            description = request.description
            imageUrl = request.imageUrl
            interest = InterestType.valueOf(request.interest)
            municipality = region
        }
        return groupRepository.save(group).toResponse()
    }

    @Transactional
    fun deleteGroup(userId: Long, groupId: Long) {
        val owner = getUserIfActive(userId)
        val group = groupRepository.findById(groupId).orElseThrow { EntityNotFoundException("그룹을 찾을 수 없습니다.") }
        if (group.owner.id != owner.id) {
            throw AccessDeniedException("You are not the owner of this group.")
        }
        groupRepository.delete(group)
    }

    @Transactional
    fun leaveGroup(userId: Long, groupId: Long) {
        val user = getUserIfActive(userId)
        val group = groupRepository.findById(groupId).orElseThrow { EntityNotFoundException("그룹을 찾을 수 없습니다.") }
        val groupMember = groupMemberRepository.findByUserAndGroup(user, group)
            ?: throw EntityNotFoundException("GroupMember not found")

        if (groupMember.role == GroupRole.OWNER) {
            throw IllegalStateException("Owner cannot leave the group. Please delete the group instead.")
        }
        groupMemberRepository.delete(groupMember)
    }

    @Transactional
    fun kickMember(ownerId: Long, groupId: Long, memberUserId: Long) {
        val owner = getUserIfActive(ownerId)
        val group = groupRepository.findById(groupId).orElseThrow { EntityNotFoundException("그룹을 찾을 수 없습니다.") }
        if (group.owner.id != owner.id) {
            throw AccessDeniedException("You are not the owner of this group.")
        }
        if (owner.id == memberUserId) {
            throw IllegalStateException("Owner cannot kick themselves.")
        }
        val memberToKick = userRepository.findById(memberUserId).orElseThrow { EntityNotFoundException("User to kick not found") }
        val groupMember = groupMemberRepository.findByUserAndGroup(memberToKick, group)
            ?: throw EntityNotFoundException("GroupMember to kick not found")

        groupMemberRepository.delete(groupMember)
    }


    @Transactional(readOnly = true)
    fun getMyGroups(userId: Long): List<GroupResponse> {
        val user = getUserIfActive(userId)
        return groupRepository.findByMembersUserAndMembersStatus(user, GroupMemberStatus.APPROVED)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getRecommendedGroups(userId: Long): List<GroupResponse> {
        val user = getUserIfActive(userId)
        val userInterests = user.interests
        val userMunicipality = user.municipality ?: throw IllegalStateException("User municipality must be set for recommendations")

        return groupRepository.findRecommendedGroups(userId, userInterests, userMunicipality)
            .map { it.toResponse() }
    }

    @Transactional
    fun requestToJoinGroup(userId: Long, groupId: Long) {
        val user = getUserIfActive(userId)
        val group = groupRepository.findById(groupId).orElseThrow { EntityNotFoundException("그룹을 찾을 수 없습니다.") }

        if (groupMemberRepository.existsByUserAndGroup(user, group)) {
            throw IllegalStateException("User has already joined or applied to this group.")
        }

        val groupMember = GroupMember(group = group, user = user)
        groupMemberRepository.save(groupMember)

        notificationService.sendNotification(
            receiver = group.owner,
            notificationType = NotificationType.GROUP_JOIN_REQUEST,
            title = "새로운 그룹 가입 신청",
            content = "${user.name ?: "사용자"}님이 '${group.title}' 그룹에 가입을 신청했습니다.",
            relatedId = groupMember.id
        )
    }

    @Transactional
    fun approveJoinRequest(ownerId: Long, groupMemberId: Long) {
        val owner = getUserIfActive(ownerId)
        val groupMember = groupMemberRepository.findById(groupMemberId)
            .orElseThrow { EntityNotFoundException("GroupMember not found") }

        if (groupMember.group.owner.id != owner.id) {
            throw AccessDeniedException("You are not the owner of this group.")
        }

        groupMember.status = GroupMemberStatus.APPROVED
        groupMemberRepository.save(groupMember) // 상태 변경 저장

        // 시스템 메시지 전송: 멤버가 그룹에 가입했음을 알림
        groupMember.group.chatRoom?.let { chatRoom ->
            chatMessageService.createAndSaveMessage(
                room = chatRoom,
                sender = null, // 시스템 메시지이므로 sender는 null
                type = ChatMessageType.SYSTEM,
                text = "${groupMember.user.name ?: "새로운 멤버"}님이 채팅방에 입장했습니다."
            )
        }

        notificationService.sendNotification(
            receiver = groupMember.user,
            notificationType = NotificationType.GROUP_JOIN_APPROVED,
            title = "그룹 가입이 승인되었습니다.",
            content = "'${groupMember.group.title}' 그룹에 오신 것을 환영합니다!",
            relatedId = groupMember.group.id
        )
    }

    @Transactional
    fun rejectJoinRequest(ownerId: Long, groupMemberId: Long) {
        val owner = getUserIfActive(ownerId)
        val groupMember = groupMemberRepository.findById(groupMemberId)
            .orElseThrow { EntityNotFoundException("GroupMember not found") }

        if (groupMember.group.owner.id != owner.id) {
            throw AccessDeniedException("You are not the owner of this group.")
        }

        groupMember.status = GroupMemberStatus.REJECTED

        notificationService.sendNotification(
            receiver = groupMember.user,
            notificationType = NotificationType.GROUP_JOIN_REJECTED,
            title = "그룹 가입이 거절되었습니다.",
            content = "'${groupMember.group.title}' 그룹 가입 신청이 거절되었습니다.",
            relatedId = groupMember.group.id
        )
    }

    private fun getUserIfActive(userId: Long): User {
        val user = userRepository.findById(userId).orElseThrow { EntityNotFoundException("User not found") }
        if (user.status != UserStatus.ACTIVE) {
            throw IllegalStateException("User profile is incomplete. Please complete your profile first.")
        }
        return user
    }

    private fun Group.toResponse() = GroupResponse(
        id = this.id!!,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl,
        interest = this.interest.description,
        region = this.municipality.name,
        memberCount = this.getApprovedMemberCount(),
        minMemberCount = this.minMemberCount,
        maxMemberCount = this.maxMemberCount,
        createdAt = this.createdAt!!
    )

    private fun Group.toDetailResponse() = GroupDetailResponse(
        id = this.id!!,
        title = this.title,
        description = this.description,
        imageUrl = this.imageUrl,
        interest = this.interest.description,
        region = this.municipality.name,
        minMemberCount = this.minMemberCount,
        maxMemberCount = this.maxMemberCount,
        members = this.members.filter { it.status == GroupMemberStatus.APPROVED }.map { it.toMemberResponse() },
        createdAt = this.createdAt!!
    )

    @Transactional(readOnly = true)
    fun getGroups(interest: String?, regionId: Long?): List<GroupResponse> {
        val groups = when {
            interest != null && regionId != null -> {
                val interestType = InterestType.valueOf(interest.uppercase())
                val region = regionMunicipalityRepository.findById(regionId)
                    .orElseThrow { EntityNotFoundException("지역을 찾을 수 없습니다.") }
                groupRepository.findByInterestAndMunicipality(interestType, region)
            }
            interest != null -> {
                val interestType = InterestType.valueOf(interest.uppercase())
                groupRepository.findByInterest(interestType)
            }
            regionId != null -> {
                val region = regionMunicipalityRepository.findById(regionId)
                    .orElseThrow { EntityNotFoundException("지역을 찾을 수 없습니다.") }
                groupRepository.findByMunicipality(region)
            }
            else -> {
                groupRepository.findAll()
            }
        }
        return groups.map { it.toResponse() }
    }

    private fun GroupMember.toMemberResponse() = GroupMemberResponse(
        userId = this.user.id!!,
        name = this.user.name,
        profileUrl = this.user.profileImageUrl,
        role = this.role
    )
}
