package com.sdstore.core.data.repository

import android.net.Uri
import com.sdstore.core.models.User
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    suspend fun registerUser(user: User, password: String, imageUri: Uri): Flow<Boolean>
}