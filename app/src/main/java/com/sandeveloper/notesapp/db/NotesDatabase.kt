package com.sandeveloper.notesapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sandeveloper.notesapp.models.NoteResponse

//if _v=0 normal, =1 added without internet, =2 deleted without internet, =3

@Database(entities = [NoteResponse::class], version = 1)
abstract class NotesDatabase:RoomDatabase() {

    abstract fun noteDAO():NotesDAO

}