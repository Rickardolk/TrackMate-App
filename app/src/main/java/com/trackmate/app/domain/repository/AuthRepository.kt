package com.trackmate.app.domain.repository


import com.google.rpc.context.AttributeContext
import com.trackmate.app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Flow<Resource<String>>
    suspend fun register(email: String, password: String): Flow<Resource<String>>
    fun logout()
}