package com.trackmate.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthActionCodeException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.trackmate.app.domain.repository.AuthRepository
import com.trackmate.app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryFb @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("User tidak ditemukan")
            emit(Resource.Success(uid))
        }catch (e: Exception) {
            emit(Resource.Error(translateError(e)))
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Gagal registrasi")

            // ← TAMBAHAN: simpan username sebagai displayName
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            user.updateProfile(profileUpdates).await()

            emit(Resource.Success(user.uid))
        }catch (e: Exception) {
            emit(Resource.Error(translateError(e)))
        }
    }

    override suspend fun logout(): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            firebaseAuth.signOut()
            emit(Resource.Success("Logout berhasil"))
        } catch (e: Exception) {
            emit(Resource.Error(translateError(e)))
        }
    }

    // ← FUNGSI BARU: update username
    override suspend fun updateUsername(newUsername: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val user = firebaseAuth.currentUser ?: throw Exception("Sesi login tidak ditemukan")
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build()
            user.updateProfile(profileUpdates).await()
            emit(Resource.Success(newUsername))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal memperbarui nama"))
        }
    }

    // ← FUNGSI BARU: ambil data user saat ini
    override fun getCurrentUsername(): String {
        return firebaseAuth.currentUser?.displayName ?: "Pengguna"
    }

    override fun getCurrentEmail(): String {
        return firebaseAuth.currentUser?.email ?: "-"
    }

    private fun translateError(exception: Exception) :String {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> "Password minimal 6 karakter"
            is FirebaseAuthInvalidCredentialsException -> {
                if (exception.message?.contains("badly formatted", ignoreCase = true) == true) {
                    "Format email tidak valid"
                } else {
                    "Email atau password Anda salah"
                }
            }
            is FirebaseAuthInvalidUserException -> "Email atau password Anda salah"
            is FirebaseAuthUserCollisionException -> "Email sudah terdaftar. Silahkan gunakan email lain atau login"
            is FirebaseAuthActionCodeException -> "Gagal melakukan registrasi"
            else -> "Terjadi kesalahan jaringan. Silahkan coba lagi"
        }
    }
}