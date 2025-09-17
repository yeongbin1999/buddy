package com.buddy.global.config

import com.buddy.global.util.DummyDataGenerator
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DummyDataRunner(
    private val dummyDataGenerator: DummyDataGenerator
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        println("\n=====================================================")
        println("Starting dummy data generation runner (unconditional execution).")
        println("=====================================================\n")

        try {
            val userCount = 1000
            val groupCount = 500
            val memberCountPerGroup = 5 // Average members per group
            val postCount = 2000
            val commentCount = 5000
            val photoCount = 1000
            val chatMessageCount = 10000

            println("Generating $userCount dummy users...")
            val createdUsers = dummyDataGenerator.generateDummyUsers(userCount)
            println("Generated ${createdUsers.size} dummy users.")

            println("Generating $groupCount dummy groups...")
            val createdGroups = if (createdUsers.isNotEmpty()) {
                dummyDataGenerator.generateDummyGroups(groupCount, createdUsers)
            } else {
                emptyList()
            }
            println("Generated ${createdGroups.size} dummy groups.")

            if (createdUsers.isNotEmpty() && createdGroups.isNotEmpty()) {
                println("Adding dummy members to groups...")
                val memberResults = dummyDataGenerator.addDummyMembersToGroups(createdUsers, createdGroups)
                println("Added ${memberResults["membersAdded"]} members to groups and approved ${memberResults["joinRequestsApproved"]} join requests.")
            } else {
                println("Skipping adding members to groups: no users or groups available.")
            }

            println("Generating $postCount dummy posts...")
            val createdPosts = if (createdUsers.isNotEmpty() && createdGroups.isNotEmpty()) {
                dummyDataGenerator.generateDummyPosts(postCount, createdUsers, createdGroups)
            } else {
                emptyList()
            }
            println("Generated ${createdPosts.size} dummy posts.")

            println("Generating $commentCount dummy comments...")
            val createdComments = if (createdUsers.isNotEmpty() && createdPosts.isNotEmpty()) {
                dummyDataGenerator.generateDummyComments(commentCount, createdUsers, createdPosts)
            } else {
                emptyList()
            }
            println("Generated ${createdComments.size} dummy comments.")

            println("Generating $photoCount dummy photos...")
            val createdPhotos = if (createdUsers.isNotEmpty() && createdGroups.isNotEmpty()) {
                dummyDataGenerator.generateDummyPhotos(photoCount, createdUsers, createdGroups)
            } else {
                emptyList()
            }
            println("Generated ${createdPhotos.size} dummy photos.")

            if (createdUsers.isNotEmpty() && createdGroups.isNotEmpty()) {
                println("Generating $chatMessageCount dummy chat messages...")
                val chatResults = dummyDataGenerator.generateDummyChatMessages(chatMessageCount, createdUsers, createdGroups)
                println("Generated ${chatResults["messagesCreated"]} chat messages.")
            } else {
                println("Skipping chat message generation: no users or groups available.")
            }

            println("\n=====================================================")
            println("Dummy data generation completed successfully.")
            println("=====================================================\n")
        } catch (e: Exception) {
            System.err.println("\n=====================================================")
            System.err.println("ERROR during dummy data generation: ${e.message}")
            e.printStackTrace()
            System.err.println("=====================================================\n")
        }
    }
}