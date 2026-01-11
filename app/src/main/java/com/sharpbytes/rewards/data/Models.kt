package com.sharpbytes.rewards.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * DRD COLLECTION: users/{userId}
 * Firestore Document Fields: name, email, wallet, streak, xp, level, coins, badges
 * Relations: One-to-Many with Participation, Idea
 */
data class User(
    val userId: String = "",           // Document ID (maps to Firebase UID)
    val name: String = "",             // User display name
    val email: String = "",            // User email
    val streak: Int = 0,               // Current streak count
    val wallet: Long = 0,              // Total points in wallet
    val coins: Long = 0,               // In-app currency (for redemption)
    val xp: Long = 0,                  // Experience points
    val level: Int = 1,                // User level (calculated from XP)
    val badges: List<String> = emptyList(), // Earned badge IDs
    val lastActiveDate: Long = 0,      // Last activity timestamp (for inactivity penalty)
    val photoUrl: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
)

/**
 * DRD COLLECTION: games/{gameId}
 * Firestore Document Fields: type, duration, reward
 * Relations: One-to-Many with Participation
 * 
 * FOMO FEATURES:
 * - Time-limited challenges (expiresAt)
 * - Limited winner slots (maxWinners, currentWinners)
 * - No replay restriction (allowReplay = false)
 * - Live countdown (expiresAt - now)
 * - Urgency notifications (urgencyLevel)
 */
data class Game(
    val gameId: String = "",           // Document ID
    val type: String = "",             // Type: "flash_hustle", "skill_streak", etc.
    val duration: Long = 0,            // Duration in seconds (0 for unlimited)
    val reward: Int = 10,              // Base points per correct answer/completion
    val title: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    
    // FOMO Features
    val isTimeLimited: Boolean = false, // If true, challenge expires at expiresAt
    val expiresAt: Long = 0,           // Unix timestamp when challenge expires
    val maxWinners: Int = 0,           // Limited winner slots (0 = unlimited)
    val currentWinners: Int = 0,       // Current number of winners
    val allowReplay: Boolean = true,   // If false, user can only play once
    val urgencyLevel: String = "none", // "none", "low", "medium", "high", "critical"
    val urgencyMessage: String = "",   // e.g., "Only 2 hours left!", "Last 5 spots!"
    val bonusMultiplier: Float = 1.0f, // Extra multiplier for limited challenges
    
    @ServerTimestamp
    val createdAt: Date? = null
)

/**
 * DRD COLLECTION: participations/{participationId}
 * Firestore Document Fields: userId, gameId, score, timestamp
 * Relations: Many-to-One with User, Many-to-One with Game
 */
data class Participation(
    val participationId: String = "",  // Document ID
    val userId: String = "",           // Reference to users/{userId}
    val gameId: String = "",           // Reference to games/{gameId}
    val score: Int = 0,                // Score achieved in this participation
    @ServerTimestamp
    val timestamp: Date? = null,       // When participation occurred
    val questionsAttempted: Int = 0,
    val correctAnswers: Int = 0,
    val pointsEarned: Int = 0,
    val coinsEarned: Int = 0,          // Coins earned from this participation
    val xpEarned: Int = 0,             // XP earned from this participation
    val timeTaken: Long = 0,           // Time taken in seconds
    val speedBonus: Int = 0,           // Bonus for fast completion
    val streakBonus: Int = 0,          // Bonus for active streak
    val rarityBonus: Int = 0,          // Bonus for rare participation
    val totalRewardMultiplier: Float = 1.0f // Combined multiplier applied
)

/**
 * ERD ENTITY: IDEA
 * DRD COLLECTION: ideas/{ideaId}
 * Firestore Document Fields: userId, title, description, votes
 * Relations: Many-to-One with User
 */
data class Idea(
    val ideaId: String = "",           // Document ID
    val userId: String = "",           // Reference to users/{userId}
    val description: String = "",      // Idea description
    val votes: Int = 0,                // Number of votes/likes
    val category: String = "",
    val status: String = "pending",    // pending, approved, rejected
    val pointsAwarded: Int = 0,
    @ServerTimestamp
    val submittedAt: Date? = null
)

/**
 * ERD ENTITY: REWARD
 * Primary Key: rewardId
 * DRD COLLECTION: leaderboard/{userId}
 * Firestore Document Fields: score (denormalized from user.wallet)
 * Note: This is a denormalized collection for performance optimization
 */
data class Leaderboard(
    val userId: String = "",           // Document ID (matches users/{userId})
    val score: Long = 0,               // Denormalized wallet balance
    val name: String = "",             // Denormalized user name
    @ServerTimestamp
    val updatedAt: Date? = null
)

/**
 * SUBCOLLECTION: users/{userId}/transactions/{transactionId}
 * Optional wallet transaction history (not in core DRD)
 */
data class WalletTransaction(
    val transactionId: String = "",    // Document ID
    val points: Long = 0,              // Points earned/deducted
    val type: String = "",             // Type: "earned", "redeemed", "bonus"
    val description: String = "",      // Description
    val sourceType: String = "",       // "game", "idea", "streak", "bonus"
    val sourceId: String = "",         // Reference to source
    @ServerTimestamp
    val createdAt: Date? = null
)

// Supporting entities (not in core DRD but needed for functionality)

/**
 * Badge System - Achievement tracking
 */
data class Badge(
    val badgeId: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val requirement: String = "",      // e.g., "complete_10_games", "reach_level_5"
    val rarity: String = "common",     // common, rare, epic, legendary
    val coinsReward: Int = 0,          // Coins awarded when earned
    val xpReward: Int = 0              // XP awarded when earned
)

/**
 * DRD COLLECTION: user_badges/{userId}_{badgeId}
 * Tracks earned badges per user
 */
data class UserBadge(
    val id: String = "",
    val badgeId: String = "",
    val userId: String = "",
    val badgeName: String = "",
    val badgeDescription: String = "",
    val badgeRarity: String = "",
    @ServerTimestamp
    val earnedAt: Date? = null
)

/**
 * DRD COLLECTION: redemptions/{redemptionId}
 * Firestore Document Fields: userId, rewardType, coinsSpent, status
 */
data class Redemption(
    val redemptionId: String = "",    // Document ID
    val userId: String = "",           // Reference to users/{userId}
    val rewardType: String = "",       // "cash", "coupon", "gift_card", "product"
    val rewardTitle: String = "",      // e.g., "$5 Amazon Gift Card"
    val coinsRequired: Long = 0,       // Coins spent for redemption
    val status: String = "pending",    // pending, approved, fulfilled, rejected
    val redemptionCode: String = "",  // Coupon/voucher code (if applicable)
    @ServerTimestamp
    val redeemedAt: Date? = null
)

data class QuizQuestion(
    val id: String = "",
    val gameId: String = "",           // Links to Game entity
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val points: Int = 10,
    val category: String = "general"
)

data class SkillTask(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val points: Int = 10,
    val difficulty: String = "easy",   // easy, medium, hard
    val category: String = ""
)

data class StreakRecord(
    val id: String = "",
    val userId: String = "",           // Reference to users/{userId}
    val date: Long = 0,
    val isCompleted: Boolean = false,
    val taskId: String = ""
)

// View models (for display purposes, not stored in DB)

data class LeaderboardEntry(
    val userId: String = "",
    val name: String = "",
    val score: Long = 0,               // Matches leaderboard.score field
    val rank: Int = 0,
    val photoUrl: String = "",
    val level: Int = 1,                // User level for display
    val badges: List<String> = emptyList() // User's top badges
)
