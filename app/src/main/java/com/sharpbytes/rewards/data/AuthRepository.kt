package com.sharpbytes.rewards.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun signUp(email: String, password: String, displayName: String): Result<String> = try {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw Exception("Failed to get UID")

        // Create user document in Firestore: users/{userId}
        val user = User(
            userId = uid,
            email = email,
            name = displayName,
            wallet = 0,
            streak = 0
        )
        firestore.collection("users").document(uid).set(user).await()
        
        // Initialize leaderboard entry
        val leaderboard = Leaderboard(
            userId = uid,
            score = 0,
            name = displayName
        )
        firestore.collection("leaderboard").document(uid).set(leaderboard).await()

        Result.success(uid)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun login(email: String, password: String): Result<String> = try {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw Exception("Failed to get UID")
        Result.success(uid)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    suspend fun getUserData(uid: String): Result<User> = try {
        val user = firestore.collection("users").document(uid).get().await()
            .toObject(User::class.java) ?: throw Exception("User not found")
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateUserPoints(uid: String, points: Long): Result<Unit> = try {
        // Update wallet field in users/{userId}
        firestore.collection("users").document(uid)
            .update("wallet", com.google.firebase.firestore.FieldValue.increment(points)).await()
        
        // Also update denormalized leaderboard/{userId}
        firestore.collection("leaderboard").document(uid)
            .update("score", com.google.firebase.firestore.FieldValue.increment(points)).await()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
