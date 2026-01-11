package com.sharpbytes.rewards.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class IdeaRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun submitIdea(idea: Idea): Result<String> = try {
        val docRef = firestore.collection("ideas").document()
        val ideaWithId = idea.copy(ideaId = docRef.id)
        docRef.set(ideaWithId).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUserIdeas(uid: String): Result<List<Idea>> = try {
        val ideas = firestore.collection("ideas")
            .whereEqualTo("userId", uid)
            .orderBy("submittedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Idea::class.java)
        Result.success(ideas)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getIdeas(status: String = "approved", limit: Int = 20): Result<List<Idea>> = try {
        val ideas = firestore.collection("ideas")
            .whereEqualTo("status", status)
            .limit(limit.toLong())
            .get()
            .await()
            .toObjects(Idea::class.java)
        Result.success(ideas)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
