package com.sandeveloper.notesapp.repository

import android.os.Build
import android.text.Editable
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.sandeveloper.notesapp.api.NotesAPI
import com.sandeveloper.notesapp.db.NotesDatabase
import com.sandeveloper.notesapp.models.NoteRequest
import com.sandeveloper.notesapp.models.NoteResponse
import com.sandeveloper.notesapp.utils.Constants.TAG
import com.sandeveloper.notesapp.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.random.Random

class NoteRepository @Inject constructor(private val notesAPI: NotesAPI,private val notesDatabase: NotesDatabase) {

    private val _notesLiveData = MutableLiveData<NetworkResult<List<NoteResponse>>>()
    val notesLiveData get() = _notesLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean, String>>>()
    val statusLiveData get() = _statusLiveData

    private val _searchLiveData = MutableLiveData<NetworkResult<List<NoteResponse>>>()
    val searchLiveData get() = _searchLiveData

        //note accessing from api
    suspend fun createNote(noteRequest: NoteRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = notesAPI.createNote(noteRequest)

        handleResponse(response, "Note Created")
    }

     fun noteUserLogOut(){
        _notesLiveData.postValue(NetworkResult.Logout())
    }

    suspend fun getNotes() {
        _notesLiveData.postValue(NetworkResult.Loading())
        val databaseNotes = notesDatabase.noteDAO().getNotes()
        val response = notesAPI.getNotes()

        if (response.isSuccessful && response.body() != null) {
            //adding data to database


            response.body()!!.forEach {apinote->
                if(apinote !in databaseNotes){
                    notesDatabase.noteDAO().addNote(apinote)
                }
            }
            _notesLiveData.postValue(NetworkResult.Success(notesDatabase.noteDAO().getNotes()))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _notesLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
            Log.d(TAG,errorObj.getString("message"))
        } else {
            _notesLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    suspend fun updateNote(id: String, noteRequest: NoteRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        notesDatabase.noteDAO().deleteOne(id)
        val response = notesAPI.updateNote(id, noteRequest)
        handleResponse(response, "Note Updated")
    }

    suspend fun deleteNote(noteId: String) {
        _statusLiveData.postValue(NetworkResult.Loading())
        notesDatabase.noteDAO().deleteOne(noteId)
        val response = notesAPI.deleteNote(noteId)
        handleResponse(response, "Note Deleted")
    }

    suspend fun syncNotes() {
        _notesLiveData.postValue(NetworkResult.Loading())
        val updateNotes = notesDatabase.noteDAO().notesToUpdate()
        val deleteNotes = notesDatabase.noteDAO().notesToDelete()
        val addNotes = notesDatabase.noteDAO().notesToAdd()

        addNotes.forEach{
            notesDatabase.noteDAO().deleteOne(it._id)
             notesAPI.createNote(NoteRequest(it.title,it.description))
        }
        updateNotes.forEach{
            if (it.userId==""){
                notesDatabase.noteDAO().deleteOne(it._id)
                notesAPI.createNote(NoteRequest(it.title,it.description))
            }else {
                notesDatabase.noteDAO().deleteOne(it._id)
                 notesAPI.updateNote(it._id, NoteRequest(it.title, it.description))
            }

        }
        deleteNotes.forEach {
            if (it.userId!=""){
                val response = notesAPI.deleteNote(it._id)
            }
            notesDatabase.noteDAO().deleteOne(it._id)
        }
        _statusLiveData.postValue(NetworkResult.Success(Pair(true, "successful")))
    }

    suspend fun getNoteDb(){
        _notesLiveData.postValue(NetworkResult.Loading())
        val databaseNotes = notesDatabase.noteDAO().getNotes()
        _notesLiveData.postValue(NetworkResult.Success(databaseNotes))

    }

    suspend fun UpdateNoteDb(id: String, noteRequest: NoteRequest){
        _statusLiveData.postValue(NetworkResult.Loading())
        notesDatabase.noteDAO().dataUpdate(id,noteRequest.title,noteRequest.description)
        _statusLiveData.postValue(NetworkResult.Success(Pair(true, "successful")))
    }

    suspend fun deleteNoteDb(noteId:String){
        _statusLiveData.postValue(NetworkResult.Loading())
        notesDatabase.noteDAO().deleteOneOffline(noteId)
        _statusLiveData.postValue(NetworkResult.Success(Pair(true, "successful")))
    }

    suspend fun deleteAllDb(){
        _statusLiveData.postValue(NetworkResult.Loading())
        notesDatabase.noteDAO().deleteAll()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createNoteDb(UId:String, noteRequest: NoteRequest){
        val time = LocalDateTime.now()

        _statusLiveData.postValue(NetworkResult.Loading())
        notesDatabase.noteDAO().addNote(NoteResponse("${Random.nextLong(999999999,9999999999)}",noteRequest.title,noteRequest.description,UId,"$time","$time",1))
        _statusLiveData.postValue(NetworkResult.Success(Pair(true, "successful")))
    }

    private fun handleResponse(response: Response<NoteResponse>, message: String) {
        if (response.isSuccessful && response.body() != null) {
            _statusLiveData.postValue(NetworkResult.Success(Pair(true, message)))
        } else {
            _statusLiveData.postValue(NetworkResult.Success(Pair(false, "Something went wrong")))
        }
    }

    suspend fun search(key: String){
       _searchLiveData.postValue(NetworkResult.Loading())
       val notes=notesDatabase.noteDAO().search(key)
       _searchLiveData.postValue(NetworkResult.Success(notes))
   }

}