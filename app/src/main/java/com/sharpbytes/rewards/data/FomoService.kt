package com.sharpbytes.rewards.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

/**
 * FOMO (Fear Of Missing Out) Service
 * 
 * Implements psychological triggers to drive engagement:
 * 1. Time-limited challenges with countdowns
 * 2. Limited winner slots (scarcity)
 * 3. No replay system (one-shot opportunity)
 * 4. Push notifications with urgency
 * 5. "Miss now, regret later" psychology
 */
class FomoService {
    private val firestore = FirebaseFirestore.getInstance()
    private val messaging = FirebaseMessaging.getInstance()

    companion object {
        // Urgency thresholds
        const val CRITICAL_TIME_HOURS = 1      // < 1 hour left
        const val HIGH_TIME_HOURS = 3          // < 3 hours left
        const val MEDIUM_TIME_HOURS = 12       // < 12 hours left
        const val LOW_TIME_HOURS = 24          // < 24 hours left
        
        // Winner slot thresholds
        const val CRITICAL_SLOTS_REMAINING = 5  // < 5 spots left
        const val HIGH_SLOTS_REMAINING = 20     // < 20 spots left
        const val MEDIUM_SLOTS_REMAINING = 50   // < 50 spots left
        
        // Notification intervals (minutes)
        const val NOTIFY_AT_1_HOUR = 60
        const val NOTIFY_AT_3_HOURS = 180
        const val NOTIFY_AT_12_HOURS = 720
        const val NOTIFY_AT_24_HOURS = 1440
    }
    
    /**
     * Create time-limited challenge
     */
    suspend fun createTimeLimitedChallenge(
        gameId: String,
        title: String,
        durationHours: Int,
        maxWinners: Int = 0,
        bonusMultiplier: Float = 1.5f
    ): Result<String> {
        return try {
            val expiresAt = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(durationHours.toLong())
            
            val game = Game(
                gameId = gameId,
                type = "limited_challenge",
                title = title,
                description = "⏰ Time-Limited Challenge! $durationHours hours only!",
                isActive = true,
                isTimeLimited = true,
                expiresAt = expiresAt,
                maxWinners = maxWinners,
                currentWinners = 0,
                allowReplay = false, // ONE SHOT ONLY
                urgencyLevel = calculateUrgencyLevel(expiresAt, maxWinners, 0),
                urgencyMessage = generateUrgencyMessage(expiresAt, maxWinners, 0),
                bonusMultiplier = bonusMultiplier
            )
            
            firestore.collection("games").document(gameId).set(game).await()
            
            // Schedule urgency notifications
            scheduleUrgencyNotifications(gameId, expiresAt)
            
            Result.success(gameId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check if user can participate in challenge
     */
    suspend fun canUserParticipate(userId: String, gameId: String): FomoCheckResult {
        return try {
            val game = firestore.collection("games").document(gameId).get().await()
                .toObject(Game::class.java) ?: return FomoCheckResult(
                    canParticipate = false,
                    reason = "Challenge not found"
                )
            
            // Check if challenge is expired
            if (game.isTimeLimited && System.currentTimeMillis() > game.expiresAt) {
                return FomoCheckResult(
                    canParticipate = false,
                    reason = "⏰ Challenge expired! You missed it.",
                    urgencyLevel = "expired"
                )
            }
            
            // Check if winner slots are full
            if (game.maxWinners > 0 && game.currentWinners >= game.maxWinners) {
                return FomoCheckResult(
                    canParticipate = false,
                    reason = "🔒 All winner slots filled! Better luck next time.",
                    urgencyLevel = "full"
                )
            }
            
            // Check if user already participated (no replay)
            if (!game.allowReplay) {
                val existingParticipation = firestore.collection("participations")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("gameId", gameId)
                    .limit(1)
                    .get()
                    .await()
                
                if (!existingParticipation.isEmpty) {
                    return FomoCheckResult(
                        canParticipate = false,
                        reason = "⚠️ Already participated! No replays allowed.",
                        urgencyLevel = "already_played"
                    )
                }
            }
            
            // Calculate urgency
            val timeRemaining = game.expiresAt - System.currentTimeMillis()
            val slotsRemaining = if (game.maxWinners > 0) game.maxWinners - game.currentWinners else Int.MAX_VALUE
            
            FomoCheckResult(
                canParticipate = true,
                urgencyLevel = game.urgencyLevel,
                urgencyMessage = game.urgencyMessage,
                timeRemaining = timeRemaining,
                slotsRemaining = slotsRemaining,
                bonusMultiplier = game.bonusMultiplier
            )
        } catch (e: Exception) {
            FomoCheckResult(
                canParticipate = false,
                reason = "Error checking eligibility: ${e.message}"
            )
        }
    }
    
    /**
     * Record participation and update winner count
     */
    suspend fun recordParticipation(userId: String, gameId: String, isWinner: Boolean): Result<Unit> {
        return try {
            if (isWinner) {
                // Increment winner count (atomic operation)
                firestore.collection("games").document(gameId)
                    .update("currentWinners", FieldValue.increment(1))
                    .await()
                
                // Update urgency level
                updateChallengeUrgency(gameId)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Calculate urgency level based on time and slots
     */
    fun calculateUrgencyLevel(expiresAt: Long, maxWinners: Int, currentWinners: Int): String {
        val hoursRemaining = (expiresAt - System.currentTimeMillis()) / (1000 * 60 * 60)
        val slotsRemaining = if (maxWinners > 0) maxWinners - currentWinners else Int.MAX_VALUE
        
        // Time-based urgency
        val timeUrgency = when {
            hoursRemaining < CRITICAL_TIME_HOURS -> "critical"
            hoursRemaining < HIGH_TIME_HOURS -> "high"
            hoursRemaining < MEDIUM_TIME_HOURS -> "medium"
            hoursRemaining < LOW_TIME_HOURS -> "low"
            else -> "none"
        }
        
        // Slot-based urgency
        val slotUrgency = when {
            slotsRemaining <= CRITICAL_SLOTS_REMAINING -> "critical"
            slotsRemaining <= HIGH_SLOTS_REMAINING -> "high"
            slotsRemaining <= MEDIUM_SLOTS_REMAINING -> "medium"
            else -> "low"
        }
        
        // Return highest urgency
        return listOf(timeUrgency, slotUrgency).maxByOrNull { urgencyPriority(it) } ?: "none"
    }
    
    private fun urgencyPriority(level: String): Int = when (level) {
        "critical" -> 4
        "high" -> 3
        "medium" -> 2
        "low" -> 1
        else -> 0
    }
    
    /**
     * Generate urgency message
     */
    fun generateUrgencyMessage(expiresAt: Long, maxWinners: Int, currentWinners: Int): String {
        val hoursRemaining = (expiresAt - System.currentTimeMillis()) / (1000 * 60 * 60)
        val minutesRemaining = ((expiresAt - System.currentTimeMillis()) / (1000 * 60)) % 60
        val slotsRemaining = if (maxWinners > 0) maxWinners - currentWinners else 0
        
        return when {
            hoursRemaining < 1 -> "🚨 LAST CHANCE! Only ${minutesRemaining}min left!"
            hoursRemaining < 3 && slotsRemaining in 1..5 -> "⚠️ ${hoursRemaining}h left + Only $slotsRemaining spots!"
            hoursRemaining < 3 -> "⏰ Hurry! ${hoursRemaining} hours remaining!"
            slotsRemaining in 1..5 -> "🔥 Only $slotsRemaining winner spots left!"
            slotsRemaining in 6..20 -> "⚡ Limited spots! $slotsRemaining remaining!"
            hoursRemaining < 12 -> "⏳ ${hoursRemaining} hours left to win!"
            hoursRemaining < 24 -> "📢 Less than a day remaining!"
            else -> "🎯 Limited time challenge!"
        }
    }
    
    /**
     * Update challenge urgency (called periodically or after participation)
     */
    suspend fun updateChallengeUrgency(gameId: String): Result<Unit> {
        return try {
            val gameDoc = firestore.collection("games").document(gameId).get().await()
            val game = gameDoc.toObject(Game::class.java) ?: return Result.failure(Exception("Game not found"))
            
            val urgencyLevel = calculateUrgencyLevel(game.expiresAt, game.maxWinners, game.currentWinners)
            val urgencyMessage = generateUrgencyMessage(game.expiresAt, game.maxWinners, game.currentWinners)
            
            firestore.collection("games").document(gameId)
                .update(mapOf(
                    "urgencyLevel" to urgencyLevel,
                    "urgencyMessage" to urgencyMessage
                ))
                .await()
            
            // Send push notification if urgency level changed to critical
            if (urgencyLevel == "critical" && game.urgencyLevel != "critical") {
                sendUrgencyNotification(gameId, urgencyMessage)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Schedule urgency notifications at key intervals
     */
    private suspend fun scheduleUrgencyNotifications(gameId: String, expiresAt: Long) {
        val notifications = listOf(
            NOTIFY_AT_24_HOURS to "📢 24 hours left! Don't miss this limited challenge!",
            NOTIFY_AT_12_HOURS to "⏳ 12 hours remaining! Rewards won't last!",
            NOTIFY_AT_3_HOURS to "⚠️ Only 3 hours left! Last chance to win!",
            NOTIFY_AT_1_HOUR to "🚨 FINAL HOUR! Miss now, regret later!"
        )
        
        for ((minutesBefore, message) in notifications) {
            val notifyAt = expiresAt - TimeUnit.MINUTES.toMillis(minutesBefore.toLong())
            if (notifyAt > System.currentTimeMillis()) {
                // Schedule notification (would use WorkManager or Firebase Cloud Functions in production)
                scheduleNotification(gameId, notifyAt, message)
            }
        }
    }
    
    /**
     * Send push notification to all users
     */
    private suspend fun sendUrgencyNotification(gameId: String, message: String) {
        try {
            // Send to topic "all_users" (users subscribe on app start)
            val notification = mapOf(
                "topic" to "all_users",
                "notification" to mapOf(
                    "title" to "⏰ Limited Challenge Alert!",
                    "body" to message,
                    "sound" to "default",
                    "priority" to "high"
                ),
                "data" to mapOf(
                    "type" to "challenge_urgency",
                    "gameId" to gameId,
                    "urgency" to "critical"
                )
            )
            
            // Store notification in Firestore for delivery
            firestore.collection("pending_notifications").add(notification).await()
        } catch (e: Exception) {
            // Log error but don't fail
        }
    }
    
    /**
     * Schedule notification (stub - implement with WorkManager or Cloud Functions)
     */
    private fun scheduleNotification(gameId: String, notifyAt: Long, message: String) {
        // In production, use:
        // - Android WorkManager for scheduled notifications
        // - Firebase Cloud Functions with Cloud Scheduler
        // - Or Firebase Cloud Messaging scheduled sends
        
        // Store scheduled notification in Firestore
        val notification = mapOf(
            "gameId" to gameId,
            "scheduledAt" to notifyAt,
            "message" to message,
            "delivered" to false
        )
        firestore.collection("scheduled_notifications").add(notification)
    }
    
    /**
     * Get countdown timer data for UI
     */
    fun getCountdownData(expiresAt: Long): CountdownData {
        val timeRemaining = expiresAt - System.currentTimeMillis()
        
        if (timeRemaining <= 0) {
            return CountdownData(
                isExpired = true,
                displayText = "EXPIRED",
                urgencyColor = "#FF0000" // Red
            )
        }
        
        val days = TimeUnit.MILLISECONDS.toDays(timeRemaining)
        val hours = TimeUnit.MILLISECONDS.toHours(timeRemaining) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) % 60
        
        val displayText = when {
            days > 0 -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
        
        val urgencyColor = when {
            hours < 1 -> "#FF0000" // Red - Critical
            hours < 3 -> "#FF6B00" // Orange - High
            hours < 12 -> "#FFB800" // Yellow - Medium
            else -> "#00C853" // Green - Low
        }
        
        return CountdownData(
            isExpired = false,
            timeRemaining = timeRemaining,
            days = days.toInt(),
            hours = hours.toInt(),
            minutes = minutes.toInt(),
            seconds = seconds.toInt(),
            displayText = displayText,
            urgencyColor = urgencyColor
        )
    }
    
    /**
     * Expire old challenges (cleanup job)
     */
    suspend fun expireOldChallenges(): Result<Int> {
        return try {
            val currentTime = System.currentTimeMillis()
            
            val expiredChallenges = firestore.collection("games")
                .whereEqualTo("isTimeLimited", true)
                .whereEqualTo("isActive", true)
                .whereLessThan("expiresAt", currentTime)
                .get()
                .await()
            
            var expiredCount = 0
            for (doc in expiredChallenges.documents) {
                doc.reference.update("isActive", false).await()
                expiredCount++
            }
            
            Result.success(expiredCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * FOMO check result
 */
data class FomoCheckResult(
    val canParticipate: Boolean,
    val reason: String = "",
    val urgencyLevel: String = "none",
    val urgencyMessage: String = "",
    val timeRemaining: Long = 0,
    val slotsRemaining: Int = 0,
    val bonusMultiplier: Float = 1.0f
)

/**
 * Countdown timer data for UI
 */
data class CountdownData(
    val isExpired: Boolean,
    val timeRemaining: Long = 0,
    val days: Int = 0,
    val hours: Int = 0,
    val minutes: Int = 0,
    val seconds: Int = 0,
    val displayText: String,
    val urgencyColor: String
)

/**
 * FOMO notification model
 */
data class FomoNotification(
    val notificationId: String = "",
    val type: String = "",            // "challenge_ending", "slots_filling", "last_chance"
    val title: String = "",
    val message: String = "",
    val gameId: String = "",
    val urgencyLevel: String = "",    // "low", "medium", "high", "critical"
    val scheduledAt: Long = 0,
    val delivered: Boolean = false,
    @com.google.firebase.firestore.ServerTimestamp
    val createdAt: java.util.Date? = null
)
