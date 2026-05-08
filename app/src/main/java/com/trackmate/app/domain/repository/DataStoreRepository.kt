package com.trackmate.app.domain.repository

import com.google.firebase.database.core.operation.ListenComplete
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveOnboardingState(complete: Boolean)
    fun readOnboardingState(): Flow<Boolean>
}