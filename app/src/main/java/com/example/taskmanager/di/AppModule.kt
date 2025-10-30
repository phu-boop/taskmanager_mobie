package com.example.taskmanager.di

import android.content.Context
import com.example.taskmanager.database.TaskDatabase
import com.example.taskmanager.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTaskDatabase(@ApplicationContext context: Context): TaskDatabase {
        return TaskDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideTaskDao(database: TaskDatabase) = database.taskDao()

    @Singleton
    @Provides
    fun provideTaskRepository(taskDao: com.example.taskmanager.database.TaskDao): TaskRepository {
        return TaskRepository(taskDao)
    }
}