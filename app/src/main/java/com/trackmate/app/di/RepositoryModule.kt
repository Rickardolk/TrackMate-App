package com.trackmate.app.di

import com.trackmate.app.data.repository.AuthRepositoryFb
import com.trackmate.app.data.repository.DataRepositoryImpl
import com.trackmate.app.data.repository.VehicleRepositoryImpl
import com.trackmate.app.domain.repository.AuthRepository
import com.trackmate.app.domain.repository.DataStoreRepository
import com.trackmate.app.domain.repository.VehicleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryFb
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDataStoreRepository(
        dataStoreRepositoryImpl: DataRepositoryImpl
    ) : DataStoreRepository

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(
        vehicleRepositoryImpl: VehicleRepositoryImpl
    ): VehicleRepository
}