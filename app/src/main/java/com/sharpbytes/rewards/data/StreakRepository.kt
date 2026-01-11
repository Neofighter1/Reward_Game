package com.sharpbytes.rewards.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StreakRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun updateStreak(uid: String, taskId: String): Result<Int> = try {
        val today = System.currentTimeMillis()
        val todayDate = today / (1000 * 60 * 60 * 24)

        // Check if task already completed today
        val existing = firestore.collection("streak_records")
            .whereEqualTo("userId", uid)
            .whereEqualTo("date", todayDate)
            .whereEqualTo("taskId", taskId)
            .get()
            .await()

        if (existing.documents.isNotEmpty()) {
            return Result.success(0) // Already completed
        }

        // Add streak record
        val record = StreakRecord(
            userId = uid,
            date = todayDate,
            isCompleted = true,
            taskId = taskId
        )
        firestore.collection("streak_records").add(record).await()

        // Update user wallet and streak in users/{userId}
        firestore.collection("users").document(uid)
            .update(mapOf(
                "wallet" to com.google.firebase.firestore.FieldValue.increment(10),
                "streak" to com.google.firebase.firestore.FieldValue.increment(1)
            ))
            .await()
        
        // Update leaderboard/{userId}
        firestore.collection("leaderboard").document(uid)
            .update("score", com.google.firebase.firestore.FieldValue.increment(10))
            .await()

        // Get current streak
        val streakCount = getStreakCount(uid)
        Result.success(streakCount)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun getStreakCount(uid: String): Int {
        val records = firestore.collection("streak_records")
            .whereEqualTo("userId", uid)
            .get()
            .await()
            .toObjects(StreakRecord::class.java)

        // Count consecutive days
        return records.size
    }

    suspend fun getStreakData(uid: String): Result<StreakRecord?> = try {
        val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        val record = firestore.collection("streak_records")
            .whereEqualTo("userId", uid)
            .whereEqualTo("date", today)
            .get()
            .await()
            .toObjects(StreakRecord::class.java)
            .firstOrNull()
        Result.success(record)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
