package com.sharpbytes.rewards.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GameRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val fomoService = FomoService()

    suspend fun getQuizQuestions(limit: Int = 5): Result<List<QuizQuestion>> = try {
        val questions = firestore.collection("quiz_questions")
            .limit(limit.toLong())
            .get()
            .await()
            .toObjects(QuizQuestion::class.java)
        Result.success(questions)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    /**
     * Get active challenges (includes FOMO filtering)
     */
    suspend fun getActiveChallenges(): Result<List<Game>> = try {
        val currentTime = System.currentTimeMillis()
        
        val challenges = firestore.collection("games")
            .whereEqualTo("isActive", true)
            .get()
            .await()
            .toObjects(Game::class.java)
            .filter { game ->
                // Filter out expired challenges
                !game.isTimeLimited || game.expiresAt > currentTime
            }
            .filter { game ->
                // Filter out full challenges
                game.maxWinners == 0 || game.currentWinners < game.maxWinners
            }
            .sortedByDescending { game ->
                // Sort by urgency (critical first)
                when (game.urgencyLevel) {
                    "critical" -> 4
                    "high" -> 3
                    "medium" -> 2
                    "low" -> 1
                    else -> 0
                }
            }
        
        Result.success(challenges)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    /**
     * Check if user can participate (FOMO validation)
     */
    suspend fun canParticipate(userId: String, gameId: String): Result<FomoCheckResult> {
        return try {
            val checkResult = fomoService.canUserParticipate(userId, gameId)
            Result.success(checkResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveGameResult(result: Participation): Result<String> = try {
        val docRef = firestore.collection("participations").document()
        val resultWithId = result.copy(participationId = docRef.id)
        docRef.set(resultWithId).await()
        
        // Update user wallet in users/{userId}
        firestore.collection("users").document(result.userId)
            .update("wallet", com.google.firebase.firestore.FieldValue.increment(result.pointsEarned.toLong()))
            .await()
        
        // Update leaderboard/{userId}
        firestore.collection("leaderboard").document(result.userId)
            .update("score", com.google.firebase.firestore.FieldValue.increment(result.pointsEarned.toLong()))
            .await()
        
        // Record participation for FOMO tracking (if winner)
        val isWinner = result.score > 0 // Or other winning criteria
        fomoService.recordParticipation(result.userId, result.gameId, isWinner)
        
        Result.success(docRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getGameHistory(uid: String): Result<List<Participation>> = try {
        val results = firestore.collection("participations")
            .whereEqualTo("userId", uid)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Participation::class.java)
        Result.success(results)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
