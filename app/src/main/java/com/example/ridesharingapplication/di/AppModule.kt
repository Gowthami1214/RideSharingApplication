package com.example.ridesharingapplication.di

import android.content.Context
import androidx.room.Room
import com.example.ridesharingapplication.data.local.RideShareDatabase
import com.example.ridesharingapplication.data.repository.AuthRepositoryImpl
import com.example.ridesharingapplication.data.repository.RideRepositoryImpl
import com.example.ridesharingapplication.domain.repository.AuthRepository
import com.example.ridesharingapplication.domain.repository.RideRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RideShareDatabase =
        Room.databaseBuilder(context, RideShareDatabase::class.java, "ride_share.db")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides fun provideUserDao(db: RideShareDatabase) = db.userDao()
    @Provides fun provideRideDao(db: RideShareDatabase) = db.rideDao()
    @Provides fun provideSavedLocationDao(db: RideShareDatabase) = db.savedLocationDao()
    @Provides fun provideDriverDao(db: RideShareDatabase) = db.driverDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    @Binds abstract fun bindRideRepository(impl: RideRepositoryImpl): RideRepository
}
