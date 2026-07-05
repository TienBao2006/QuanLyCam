package com.example.quanlycam.data.repository

import com.example.quanlycam.data.model.PhieuNhapCam
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PhieuNhapCamRepository {

    private val db = FirebaseFirestore.getInstance().collection("phieuNhap")

    fun getPhieuList(): Flow<List<PhieuNhapCam>> = callbackFlow {
        val listener = db
            .orderBy("taoLuc", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PhieuNhapCam::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun them(phieu: PhieuNhapCam): Result<Unit> = runCatching {
        db.add(phieu).await()
    }

suspend fun sua(phieu: PhieuNhapCam): Result<Unit> = runCatching {
        db.document(phieu.id).set(phieu).await()
    }

    suspend fun xoa(id: String): Result<Unit> = runCatching {
        db.document(id).delete().await()
    }
    suspend fun getById(id: String): PhieuNhapCam? {
        return db.document(id)
            .get()
            .await()
            .toObject(PhieuNhapCam::class.java)
            ?.copy(id = id)
    }
    suspend fun capNhat(phieu: PhieuNhapCam): Result<Unit> = runCatching {

        db.document(phieu.id)
            .set(phieu)
            .await()

    }

}
