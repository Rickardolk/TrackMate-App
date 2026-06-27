package com.trackmate.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveOnboardingState(complete: Boolean)
    fun readOnboardingState(): Flow<Boolean>
}