package com.trackmate.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.trackmate.app.domain.repository.DataStoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "trackmate_prefs")

class DataRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DataStoreRepository {
    private object PreferencesKey {
        val onBoardingKey = booleanPreferencesKey(name = "on_boarding_completed")
    }

    override suspend fun saveOnboardingState(complete: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.onBoardingKey] = complete
        }
    }

    override fun readOnboardingState(): Flow<Boolean> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
            }
            .map { preferences ->
                val onboardingState = preferences[PreferencesKey.onBoardingKey] ?: false
                onboardingState
            }
    }

}