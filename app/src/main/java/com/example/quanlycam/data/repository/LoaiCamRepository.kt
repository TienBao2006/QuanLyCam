package com.example.quanlycam.data.repository

import com.example.quanlycam.data.model.LoaiCam
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LoaiCamRepository {

    private val db = FirebaseFirestore.getInstance().collection("loaiCam")

    fun getLoaiCamList(): Flow<List<LoaiCam>> = callbackFlow {
        val listener = db.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(LoaiCam::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    suspend fun them(loaiCam: LoaiCam): Result<Unit> = runCatching {
        db.add(loaiCam).await()
    }

    suspend fun xoa(id: String): Result<Unit> = runCatching {
        db.document(id).delete().await()
    }
}
