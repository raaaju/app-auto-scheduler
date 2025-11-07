package com.autoappsheduler.di

import android.content.Context
import com.autoappsheduler.data.AppSchedulerRepository
import com.autoappsheduler.db.ScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideAppSchedulerRepository(
        scheduleDao: ScheduleDao,
        @ApplicationContext context: Context
    ): AppSchedulerRepository {
        return AppSchedulerRepository(scheduleDao, context)
    }
}
