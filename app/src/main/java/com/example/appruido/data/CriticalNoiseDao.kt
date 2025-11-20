package com.example.appruido.data

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CriticalNoiseDao {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("audio_critical")

    suspend fun insert(criticalNoise: CriticalNoise) {
        collection.document().set(criticalNoise).await()
    }

    suspend fun getAll(userId: String): List<CriticalNoise> {
        return collection.where(
            Filter.equalTo("userId", userId)
        ).get().await().toObjects(CriticalNoise::class.java)
    }
}