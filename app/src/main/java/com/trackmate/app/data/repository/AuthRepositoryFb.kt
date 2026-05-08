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
        email: String,
        password: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Gagal registrasi")
            emit(Resource.Success(uid))
        }catch (e: Exception) {
            emit(Resource.Error(translateError(e)))
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    private fun translateError(exception: Exception) :String {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                "Password minimal 6 karakter"
            }
            is FirebaseAuthInvalidCredentialsException -> {
                if (exception.message?.contains("badly formatted" , ignoreCase = true) == true) {
                    "Format email tidak valid"
                } else {
                    "Email atau password Anda salah"
                }
            }
            is FirebaseAuthInvalidUserException -> {
                "Email atau password Anda salah"
            }
            is FirebaseAuthUserCollisionException -> {
                "Email sudah terdaftar. Silahkan gunakan email lain atau login"
            }
            is FirebaseAuthActionCodeException -> {
                "Gagal melakukan registrasi"
            }
            else -> {
                "Terjadi kesalahan jaringan. Silahkan coba lagi"
            }
        }
    }
}