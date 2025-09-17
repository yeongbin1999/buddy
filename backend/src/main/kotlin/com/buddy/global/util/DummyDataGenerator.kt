package com.buddy.global.util

import com.buddy.domain.auth.service.AuthService
import com.buddy.domain.chat.service.ChatMessageService
import com.buddy.domain.group.dto.GroupCreateRequest
import com.buddy.domain.group.entity.Group
import com.buddy.domain.group.repository.GroupMemberRepository
import com.buddy.domain.group.repository.GroupRepository
import com.buddy.domain.group.service.GroupService
import com.buddy.domain.photo.dto.PhotoCreateRequest
import com.buddy.domain.photo.entity.Photo
import com.buddy.domain.photo.repository.PhotoRepository
import com.buddy.domain.photo.service.PhotoService
import com.buddy.domain.post.dto.CommentCreateRequest
import com.buddy.domain.post.dto.PostCreateRequest
import com.buddy.domain.post.entity.Comment
import com.buddy.domain.post.entity.Post
import com.buddy.domain.post.repository.CommentRepository
import com.buddy.domain.post.repository.PostRepository
import com.buddy.domain.post.service.CommentService
import com.buddy.domain.post.service.PostService
import com.buddy.domain.region.repository.RegionMunicipalityRepository
import com.buddy.domain.user.dto.ProfileUpdateRequest
import com.buddy.domain.user.entity.User
import com.buddy.domain.user.repository.UserRepository
import com.buddy.domain.user.service.UserService
import com.buddy.enum.ChatMessageType
import com.buddy.enum.GroupMemberStatus
import com.buddy.enum.InterestType
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*

@Component
class DummyDataGenerator(
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val regionMunicipalityRepository: RegionMunicipalityRepository,
    private val groupService: GroupService,
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val postService: PostService,
    private val postRepository: PostRepository,
    private val commentService: CommentService,
    private val commentRepository: CommentRepository,
    private val chatMessageService: ChatMessageService,
    private val photoService: PhotoService,
    private val photoRepository: PhotoRepository
) {
    private val random = Random(42L)

        fun generateDummyUsers(count: Int): List<User> {
        val createdUsers = mutableListOf<User>()
        val allMunicipalities = regionMunicipalityRepository.findAll()
        val allInterestTypes = InterestType.values().toList()

        if (allMunicipalities.isEmpty()) {
            println("No municipalities found. Cannot complete user profiles.")
            return emptyList()
        }

        for (i in 1..count) {
            val providerId = "dummy_provider_id_$i"
            val email = "dummyuser$i@example.com"
            val name = "Dummy User $i"
            val profileImageUrl: String? = "https://picsum.photos/200/300?random=$i" // Placeholder image
            val provider = "dummy"

            val customUserDetails = authService.findOrCreateOAuthUser(
                providerId = providerId,
                email = email,
                name = name,
                profileImageUrl = profileImageUrl,
                provider = provider
            )
            val user = userRepository.findById(customUserDetails.id).orElse(null)

            if (user != null) {
                // Generate random birthdate (e.g., between 1980-01-01 and 2005-12-31)
                val minDate = LocalDate.of(1980, 1, 1)
                val maxDate = LocalDate.of(2005, 12, 31)
                val randomDays = random.nextInt(minDate.until(maxDate).days)
                val birthdate = minDate.plusDays(randomDays.toLong())

                // Pick a random municipality
                val randomMunicipality = allMunicipalities[random.nextInt(allMunicipalities.size)]

                // Pick 1 to 3 random interests
                val randomInterests = mutableSetOf<InterestType>()
                val numberOfInterests = random.nextInt(3) + 1 // 1 to 3 interests
                repeat(numberOfInterests) {
                    randomInterests.add(allInterestTypes[random.nextInt(allInterestTypes.size)])
                }

                val profileUpdateRequest = ProfileUpdateRequest(
                    name = name,
                    birthdate = birthdate,
                    municipalityId = randomMunicipality.id!!, // Assuming ID is not null
                    interests = randomInterests.toList()
                )
                userService.updateProfile(user.id!!, profileUpdateRequest) // Assuming user ID is not null
                createdUsers.add(user)
            } else {
                println("Failed to retrieve user after creation for providerId $providerId. Skipping profile update.")
            }
        }
        return createdUsers
    }

        fun generateDummyGroups(count: Int, dummyUsers: List<User>): List<Group> {
        val createdGroups = mutableListOf<Group>()
        val allMunicipalities = regionMunicipalityRepository.findAll()
        val allInterestTypes = InterestType.values().toList()

        if (dummyUsers.isEmpty()) {
            println("No dummy users available to create groups.")
            return emptyList()
        }
        if (allMunicipalities.isEmpty()) {
            println("No municipalities found. Cannot create groups.")
            return emptyList()
        }

        for (i in 1..count) {
            val owner = dummyUsers[random.nextInt(dummyUsers.size)]
            val title = "Dummy Group $i - ${allInterestTypes[random.nextInt(allInterestTypes.size)].description}"
            val description = "This is a dummy group for testing purposes. Group number $i."
            val imageUrl = "https://picsum.photos/400/300?random=${random.nextInt(1000)}"
            val interest = allInterestTypes[random.nextInt(allInterestTypes.size)].name
            val region = allMunicipalities[random.nextInt(allMunicipalities.size)]
            val minMemberCount = random.nextInt(3) + 2 // 2 to 4
            val maxMemberCount = random.nextInt(5) + minMemberCount + 1 // minMemberCount + 1 to minMemberCount + 5

            try {
                val groupCreateRequest = GroupCreateRequest(
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    interest = interest,
                    regionId = region.id!!,
                    minMemberCount = minMemberCount,
                    maxMemberCount = maxMemberCount
                )
                val createdGroupResponse = groupService.createGroup(owner.id!!, groupCreateRequest)
                val createdGroup = groupRepository.findById(createdGroupResponse.id).orElse(null)
                if (createdGroup != null) {
                    createdGroups.add(createdGroup)
                }
            } catch (e: Exception) {
                println("Failed to create group $title: ${e.message}")
            }
        }
        return createdGroups
    }

        fun addDummyMembersToGroups(dummyUsers: List<User>, dummyGroups: List<Group>): Map<String, Int> {
        val results = mutableMapOf<String, Int>()
        var membersAdded = 0
        var joinRequestsApproved = 0

        if (dummyUsers.isEmpty() || dummyGroups.isEmpty()) {
            println("No dummy users or groups available to add members.")
            return results
        }

        dummyGroups.forEach { group -> // Changed 'it' to 'group' for clarity
            val currentMembers = groupMemberRepository.findAllByGroupAndStatus(group, GroupMemberStatus.APPROVED).map { it.user }
            val potentialMembers = dummyUsers.filter { user ->
                user.id != group.owner.id && !currentMembers.any { it.id == user.id }
            }

            // Add 0 to (maxMemberCount - currentMembers.size) random members
            val membersToAddCount = random.nextInt(group.maxMemberCount - currentMembers.size + 1)
            val membersToAdd = potentialMembers.shuffled(random).take(membersToAddCount)

            membersToAdd.forEach { memberUser ->
                try {
                    // Request to join
                    groupService.requestToJoinGroup(memberUser.id!!, group.id!!)
                    membersAdded++

                    // Approve join request (by owner)
                    val groupMember = groupMemberRepository.findByUserAndGroup(memberUser, group)
                        ?: throw IllegalStateException("GroupMember not found after request to join.")
                    groupService.approveJoinRequest(group.owner.id!!, groupMember.id!!)
                    joinRequestsApproved++
                } catch (e: Exception) {
                    println("Failed to add user ${memberUser.email} to group ${group.title}: ${e.message}")
                }
            }
        }
        results["membersAdded"] = membersAdded
        results["joinRequestsApproved"] = joinRequestsApproved
        return results
    }

        fun generateDummyPosts(count: Int, dummyUsers: List<User>, dummyGroups: List<Group>): List<Post> {
        val createdPosts = mutableListOf<Post>()
        if (dummyUsers.isEmpty() || dummyGroups.isEmpty()) {
            println("No dummy users or groups available to create posts.")
            return emptyList()
        }

        for (i in 1..count) {
            val group = dummyGroups[random.nextInt(dummyGroups.size)]
            val approvedMembers = groupMemberRepository.findAllByGroupAndStatus(group, GroupMemberStatus.APPROVED).map { it.user }

            if (approvedMembers.isEmpty()) {
                println("No approved members in group ${group.title}. Skipping post creation.")
                continue
            }
            val author = approvedMembers[random.nextInt(approvedMembers.size)]

            val title = "Post $i in ${group.title} by ${author.name}"
            val content = "This is the content for post $i. It's a dummy post generated for testing purposes."

            try {
                val postCreateRequest = PostCreateRequest(
                    title = title,
                    content = content
                )
                val createdPostResponse = postService.createPost(author.id!!, group.id!!, postCreateRequest)
                val createdPost = postRepository.findById(createdPostResponse.id).orElse(null)
                if (createdPost != null) {
                    createdPosts.add(createdPost)
                }
            } catch (e: Exception) {
                println("Failed to create post $title: ${e.message}")
            }
        }
        return createdPosts
    }

        fun generateDummyComments(count: Int, dummyUsers: List<User>, dummyPosts: List<Post>): List<Comment> {
        val createdComments = mutableListOf<Comment>()
        if (dummyUsers.isEmpty() || dummyPosts.isEmpty()) {
            println("No dummy users or posts available to create comments.")
            return emptyList()
        }

        for (i in 1..count) {
            val post = dummyPosts[random.nextInt(dummyPosts.size)]
            val approvedMembers = groupMemberRepository.findAllByGroupAndStatus(post.group, GroupMemberStatus.APPROVED).map { it.user }

            if (approvedMembers.isEmpty()) {
                println("No approved members in group ${post.group.title} for post ${post.title}. Skipping comment creation.")
                continue
            }
            val author = approvedMembers[random.nextInt(approvedMembers.size)]

            val content = "Comment $i on post \"${post.title}\" by ${author.name}."

            var parentCommentId: Long? = null
            // Randomly decide if it's a reply to an existing comment
            if (random.nextBoolean()) {
                val existingComments = commentRepository.findByPostAndIsDeletedFalseOrderByCreatedAtAsc(post)
                if (existingComments.isNotEmpty()) {
                    parentCommentId = existingComments[random.nextInt(existingComments.size)].id
                }
            }

            try {
                val commentCreateRequest = CommentCreateRequest(
                    content = content,
                    parentId = parentCommentId
                )
                val createdCommentResponse = commentService.createComment(author.id!!, post.id!!, commentCreateRequest)
                val createdComment = commentRepository.findById(createdCommentResponse.id).orElse(null)
                if (createdComment != null) {
                    createdComments.add(createdComment)
                }
            } catch (e: Exception) {
                println("Failed to create comment on post ${post.title}: ${e.message}")
            }
        }
        return createdComments
    }

        fun generateDummyChatMessages(count: Int, dummyUsers: List<User>, dummyGroups: List<Group>): Map<String, Int> {
        val results = mutableMapOf<String, Int>()
        var messagesCreated = 0

        if (dummyUsers.isEmpty() || dummyGroups.isEmpty()) {
            println("No dummy users or groups available to create chat messages.")
            return results
        }

        dummyGroups.forEach { group -> // Changed 'it' to 'group' for clarity
            val approvedMembers = groupMemberRepository.findAllByGroupAndStatus(group, GroupMemberStatus.APPROVED).map { it.user }
            val chatRoom = group.chatRoom

            if (approvedMembers.isEmpty() || chatRoom == null) {
                println("Group ${group.title} has no approved members or chat room. Skipping chat message creation.")
                return@forEach
            }

            // Generate a random number of messages for this group (e.g., 5 to 20 messages)
            val messagesPerGroup = random.nextInt(16) + 5

            repeat(messagesPerGroup) {
                val sender = approvedMembers[random.nextInt(approvedMembers.size)]
                val content = "Hello from ${sender.name} in ${group.title}! This is chat message ${it + 1}."
                try {
                    chatMessageService.createAndSaveMessage(chatRoom, sender, ChatMessageType.TEXT, content)
                    messagesCreated++
                } catch (e: Exception) {
                    println("Failed to create chat message in group ${group.title}: ${e.message}")
                }
            }
        }
        results["messagesCreated"] = messagesCreated
        return results
    }

        fun generateDummyPhotos(count: Int, dummyUsers: List<User>, dummyGroups: List<Group>): List<Photo> {
        val createdPhotos = mutableListOf<Photo>()
        if (dummyUsers.isEmpty() || dummyGroups.isEmpty()) {
            println("No dummy users or groups available to create photos.")
            return emptyList()
        }

        for (i in 1..count) {
            val group = dummyGroups[random.nextInt(dummyGroups.size)]
            val approvedMembers = groupMemberRepository.findAllByGroupAndStatus(group, GroupMemberStatus.APPROVED).map { it.user }

            if (approvedMembers.isEmpty()) {
                println("No approved members in group ${group.title}. Skipping photo creation.")
                continue
            }
            val uploader = approvedMembers[random.nextInt(approvedMembers.size)]

            val imageUrl = "https://picsum.photos/600/400?random=${random.nextInt(1000)}"
            val caption = if (random.nextBoolean()) "A lovely moment in ${group.title} by ${uploader.name}" else null

            try {
                val photoCreateRequest = PhotoCreateRequest(
                    imageUrl = imageUrl,
                    caption = caption
                )
                val createdPhotoResponse = photoService.uploadPhoto(uploader.id!!, group.id!!, photoCreateRequest)
                val createdPhoto = photoRepository.findById(createdPhotoResponse.id).orElse(null)
                if (createdPhoto != null) {
                    createdPhotos.add(createdPhoto)
                }
            } catch (e: Exception) {
                println("Failed to create photo in group ${group.title}: ${e.message}")
            }
        }
        return createdPhotos
    }

    // Helper function to get a random element from a list
    private fun <T> List<T>.randomElement(random: Random): T? {
        if (isEmpty()) return null
        return get(random.nextInt(size))
    }

    // Helper function to get a random set of elements from a list
    private fun <T> List<T>.randomElements(random: Random, count: Int): Set<T> {
        if (isEmpty() || count <= 0) return emptySet()
        val result = mutableSetOf<T>()
        while (result.size < count && result.size < size) {
            result.add(get(random.nextInt(size)))
        }
        return result
    }
}