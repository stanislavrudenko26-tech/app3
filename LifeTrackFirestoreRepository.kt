package com.example.data.firebase

import android.content.Context
import com.example.R
import com.example.data.local.TransactionEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LifeTrackFirestoreRepository(private val db: FirebaseFirestore) {
    companion object {
        fun create(context: Context): LifeTrackFirestoreRepository {
            val dbId = context.getString(R.string.firestore_database_id)
            return LifeTrackFirestoreRepository(FirebaseFirestore.getInstance(dbId))
        }
    }

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    suspend fun syncTransactionToCloud(transaction: TransactionEntity) {
        val uid = auth.currentUser?.uid ?: return
        val path = "users/$uid/transactions/${transaction.id}"
        val docRef = db.collection("users").document(uid).collection("transactions").document(transaction.id)
        val payload = mapOf(
            "userId" to uid,
            "type" to transaction.type,
            "amount" to transaction.amount,
            "category" to transaction.category,
            "note" to transaction.note,
            "date" to transaction.date,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
        }
    }

    suspend fun syncUserProfileToCloud(displayName: String, email: String, currency: String, themeMode: String) {
        val uid = auth.currentUser?.uid ?: return
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)
        val payload = mapOf(
            "userId" to uid,
            "displayName" to displayName,
            "email" to email,
            "currency" to currency,
            "themeMode" to themeMode,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
        }
    }
}
