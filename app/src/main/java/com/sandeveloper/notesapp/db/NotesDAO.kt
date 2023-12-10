package com.sandeveloper.notesapp.db

import android.text.Editable
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sandeveloper.notesapp.models.NoteResponse

@Dao
interface NotesDAO {

@Insert
suspend fun addNotes(notes:List<NoteResponse>)

@Insert
suspend fun addNote(note: NoteResponse)

@Query("SELECT * FROM Notes WHERE __v!=2")
suspend fun getNotes():List<NoteResponse>

@Query("UPDATE Notes SET __v = 2 WHERE _id =:Id")
suspend fun deleteOneOffline(Id:String)

@Query("DELETE FROM Notes WHERE _id=:Id")
suspend fun deleteOne(Id: String)

@Query("DELETE FROM Notes")
suspend fun deleteAll()

@Query("UPDATE Notes SET description = :desc,title = :title, __v = 3 WHERE _id=:noteId")
suspend fun dataUpdate(noteId:String,title:String,desc:String)

@Query("SELECT * FROM Notes WHERE __v=2")
suspend fun notesToDelete():List<NoteResponse>

@Query("SELECT * FROM Notes WHERE __v= 3")
suspend fun notesToUpdate():List<NoteResponse>

@Query("SELECT * FROM Notes WHERE __v = 1")
suspend fun notesToAdd():List<NoteResponse>

@Query("SELECT * FROM Notes WHERE title LIKE '%'||:it||'%'  OR description LIKE '%'||:it||'%'")
suspend fun search(it: String):List<NoteResponse>
}