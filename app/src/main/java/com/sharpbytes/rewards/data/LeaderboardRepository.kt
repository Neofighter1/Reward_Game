package com.sharpbytes.rewards.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LeaderboardRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getLeaderboard(limit: Int = 50): Result<List<LeaderboardEntry>> = try {
        // Query denormalized leaderboard/{userId} collection for performance
        val snapshot = firestore.collection("leaderboard")
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()

        val leaderboard = snapshot.documents.mapIndexed { index, doc ->
            val entry = doc.toObject(Leaderboard::class.java)
            LeaderboardEntry(
                userId = entry?.userId ?: "",
                name = entry?.name ?: "Unknown",
                score = entry?.score ?: 0,
                rank = index + 1,
                photoUrl = ""
            )
        }
        Result.success(leaderboard)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUserRank(uid: String): Result<Int> = try {
        // Get user's score from leaderboard/{userId}
        val leaderboardDoc = firestore.collection("leaderboard").document(uid).get().await()
        val userScore = leaderboardDoc.toObject(Leaderboard::class.java)?.score ?: 0

        // Count users with higher scores
        val count = firestore.collection("leaderboard")
            .whereGreaterThan("score", userScore)
            .get()
            .await()
            .size

        Result.success(count + 1)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
