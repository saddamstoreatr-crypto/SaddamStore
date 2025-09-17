package com.sdstore.auth.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sdstore.core.data.Result
import com.sdstore.core.data.repository.RegisterRepository
import com.sdstore.core.models.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : RegisterRepository {

    override suspend fun registerUser(user: User, imageUri: Uri?): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(user.email, user.password).await()
            val firestoreUser = user.copy(
                id = result.user!!.uid,
                password = "" // Don't store password in Firestore
            )
            firestore.collection("users").document(result.user!!.uid).set(firestoreUser).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}