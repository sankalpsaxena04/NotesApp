package com.sandeveloper.notesapp

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.sandeveloper.notesapp.databinding.FragmentNotesBinding
import com.sandeveloper.notesapp.models.NoteRequest
import com.sandeveloper.notesapp.models.NoteResponse
import com.sandeveloper.notesapp.models.UserResponse
import com.sandeveloper.notesapp.utils.NetworkResult
import com.sandeveloper.notesapp.utils.Networkstatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesFragment : Fragment() {
    private var _binding : FragmentNotesBinding?=null

    private val binding get() = _binding!!
    private val noteViewModel by viewModels<NoteViewModel>()
    private var note:NoteResponse?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInitialData()
        //for onclick listeners
        bindHandlers()
        //for binding observers
        bindObservers()
    }

    private fun bindObservers() {
        noteViewModel.statusLiveData.observe(viewLifecycleOwner, Observer {
            when(it){
                is NetworkResult.Success->{
                    findNavController().popBackStack()
                }

                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading ->{

                }
                is NetworkResult.Logout->{

                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindHandlers() {
        //deleting note
        binding.btnDelete.setOnClickListener{
            note?.let {
                if(Networkstatus.isNetworkAvailable(requireContext())){
                    noteViewModel.deleteNote(it._id)
                }else{
                    noteViewModel.deleteNoteDb(it._id)
                }
                }

        }
        //updating note
        binding.btnSubmit.setOnClickListener {
            val title = binding.txtTitle.text.toString()
            val description = binding.txtDescription.text.toString()
            val noteRequest = NoteRequest(title,description)
            if(Networkstatus.isNetworkAvailable(requireContext())){
            if(note==null){
                noteViewModel.createNote(noteRequest)
            }else{
                noteViewModel.updateNote(note!!._id,noteRequest)
            }
            }else{
                if(note==null){
                    noteViewModel.createNoteDb(noteRequest)
                }else{
                    noteViewModel.updateNoteDb(note!!._id,noteRequest)

                }
            }
        }
    }

    private fun setInitialData() {
        //fragments has arguments to access bundle setting fragment for add / edit note
        val jsonNote = arguments?.getString("note")

        if(jsonNote!=null){
            note = Gson().fromJson(jsonNote,NoteResponse::class.java)
            note?.let {

                binding.txtTitle.setText(it.title)
                binding.txtDescription.setText(it.description)
            }
        }else{
            binding.addEditText.text = "Add Note"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}