package com.sdstore.feature_auth.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sdstore.core.data.repository.RegisterRepository
import com.sdstore.core.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : RegisterRepository {

    override suspend fun registerUser(user: User, password: String, imageUri: Uri): Flow<Boolean> = flow {
        // First, create the user in Firebase Auth
        val result = auth.createUserWithEmailAndPassword(user.email, password).await()
        val userId = result.user!!.uid

        // Then, upload the image to Firebase Storage
        val imagePath = "profile_images/${UUID.randomUUID()}"
        val storageRef = storage.reference.child(imagePath)
        storageRef.putFile(imageUri).await()
        val imageUrl = storageRef.downloadUrl.await().toString()

        // Finally, save the user to Firestore
        val firestoreUser = user.copy(
            uid = userId,
            imageUrl = imageUrl
        )
        firestore.collection("users").document(userId).set(firestoreUser).await()

        emit(true)
    }.catch {
        // If anything fails, emit false
        emit(false)
    }
}