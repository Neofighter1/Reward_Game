package com.sharpbytes.rewards.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WalletRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getWalletBalance(uid: String): Result<Long> = try {
        val user = firestore.collection("users").document(uid).get().await()
            .toObject(User::class.java) ?: throw Exception("User not found")
        Result.success(user.wallet)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun addTransaction(transaction: WalletTransaction): Result<String> = try {
        // Add transaction to subcollection: users/{userId}/transactions/{transactionId}
        val docRef = firestore.collection("users").document(transaction.userId)
            .collection("transactions").document()
        val txWithId = transaction.copy(transactionId = docRef.id)
        docRef.set(txWithId).await()

        // Update user wallet in users/{userId}
        firestore.collection("users").document(transaction.userId)
            .update("wallet", com.google.firebase.firestore.FieldValue.increment(transaction.points))
            .await()
        
        // Update leaderboard/{userId}
        firestore.collection("leaderboard").document(transaction.userId)
            .update("score", com.google.firebase.firestore.FieldValue.increment(transaction.points))
            .await()

        Result.success(docRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getTransactionHistory(uid: String, limit: Int = 50): Result<List<WalletTransaction>> = try {
        // Query subcollection: users/{userId}/transactions
        val transactions = firestore.collection("users").document(uid)
            .collection("transactions")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            .toObjects(WalletTransaction::class.java)
        Result.success(transactions)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
