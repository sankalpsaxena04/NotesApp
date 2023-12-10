package com.sandeveloper.notesapp

import android.os.Build
import android.text.Editable
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeveloper.notesapp.models.NoteRequest
import com.sandeveloper.notesapp.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class NoteViewModel@Inject constructor(private val noteRepository   : NoteRepository):ViewModel() {
    val notesLivedata get() = noteRepository.notesLiveData
    val statusLiveData get() = noteRepository.statusLiveData
    val searchLiveData get() = noteRepository.searchLiveData
    fun getNotes(){
        viewModelScope.launch {
            noteRepository.getNotes()
        }
    }
    fun createNote(noteRequest: NoteRequest){
        viewModelScope.launch {
            noteRepository.createNote(noteRequest)
        }
    }
    fun updateNote(noteId:String,noteRequest: NoteRequest){
        viewModelScope.launch {
            noteRepository.updateNote(noteId,noteRequest)
        }
    }
    fun logOutNote(){
        viewModelScope.launch{
            noteRepository.noteUserLogOut()
            noteRepository.deleteAllDb()
        }
    }
    fun deleteNote(noteId :String){
        viewModelScope.launch {
            noteRepository.deleteNote(noteId)
        }
    }

    fun getNotesDb(){
        viewModelScope.launch {
            noteRepository.getNoteDb()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNoteDb(noteRequest: NoteRequest){
        viewModelScope.launch {
            noteRepository.createNoteDb("",noteRequest)
        }
    }

    fun deleteNoteDb(noteId: String){
        viewModelScope.launch {
            noteRepository.deleteNoteDb(noteId)
        }
    }
    fun deleteAllDb(){
        viewModelScope.launch {
            noteRepository.deleteAllDb()
        }
    }

    fun sync(){
        viewModelScope.launch {
            noteRepository.syncNotes()
        }
    }
    fun updateNoteDb(id:String,noteRequest: NoteRequest){
        viewModelScope.launch {
            noteRepository.UpdateNoteDb(id,noteRequest)
        }
    }
    fun search(key: String){
        viewModelScope.launch {
            noteRepository.search(key)
        }
    }
}