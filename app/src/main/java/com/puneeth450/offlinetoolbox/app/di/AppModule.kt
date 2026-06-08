package com.puneeth450.offlinetoolbox.app.di

import android.content.Context
import androidx.room.Room
import com.puneeth450.offlinetoolbox.app.data.local.AppDatabase
import com.puneeth450.offlinetoolbox.app.data.local.dao.ToolHistoryDao
import com.puneeth450.offlinetoolbox.app.data.repository.SettingsRepository
import com.puneeth450.offlinetoolbox.app.data.repository.ToolHistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "offline_toolbox.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideToolHistoryDao(database: AppDatabase): ToolHistoryDao = database.toolHistoryDao()

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository =
        SettingsRepository(context)

    @Provides
    @Singleton
    fun provideToolHistoryRepository(dao: ToolHistoryDao): ToolHistoryRepository =
        ToolHistoryRepository(dao)
}
