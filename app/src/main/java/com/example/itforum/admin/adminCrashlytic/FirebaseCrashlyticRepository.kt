package com.example.itforum.admin.adminCrashlytic

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//lấy firestore
object FirebaseCrashlyticRepository {
    suspend fun getAllCrashes(): List<CrashLog> {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("crash_logs")
                .get()
                .await()

            val list = snapshot.documents.mapNotNull {
                val log = it.toObject(CrashLog::class.java)
                println("Document loaded: $log")
                log
            }

            list
        } catch (e: Exception) {
            println(" Lỗi khi lấy crash logs: ${e.message}")
            emptyList()
        }
    }
}
