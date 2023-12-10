package com.sandeveloper.notesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.sandeveloper.notesapp.databinding.FragmentSearchBinding
import com.sandeveloper.notesapp.models.NoteResponse
import com.sandeveloper.notesapp.utils.NetworkResult
import com.sandeveloper.notesapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding:FragmentSearchBinding?=null
    private val binding get() = _binding!!
    private  val noteViewModel by viewModels<NoteViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    @Inject
    lateinit var token: TokenManager
    //defining adapter
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater,container,false)
        adapter = NoteAdapter(::onNoteClicked)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()

        binding.searchBtn.setOnClickListener {
            val key:String = binding.searchText.text.toString()
            noteViewModel.search(key)
        }
        binding.noteList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.noteList.adapter=adapter
    }
    private fun bindObserver(){
        noteViewModel.searchLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible=false
            binding.img.isVisible = false
            when(it){
                is NetworkResult.Success->{
                    adapter.submitList(it.data)
                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(),it.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading ->{
                    binding.progressBar.isVisible=true
                }
                is NetworkResult.Logout->{
                 }
            }
        })
    }

    private fun onNoteClicked(noteResponse: NoteResponse){
        //sending data from main to note
        val bundle=Bundle()
        bundle.putString("note", Gson().toJson(noteResponse))
        findNavController().navigate(R.id.action_searchFragment_to_notesFragment,bundle)

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    }
