package com.sandeveloper.notesapp.di

import android.content.Context
import androidx.room.Room
import com.sandeveloper.notesapp.db.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesNotesDB(@ApplicationContext context: Context):NotesDatabase{
        return Room.databaseBuilder(context,NotesDatabase::class.java,"NoteDB").build()
    }

}