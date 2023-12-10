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
import com.sandeveloper.notesapp.databinding.FragmentMainBinding
import com.sandeveloper.notesapp.models.NoteResponse
import com.sandeveloper.notesapp.models.UserResponse
import com.sandeveloper.notesapp.utils.NetworkResult
import com.sandeveloper.notesapp.utils.Networkstatus
import com.sandeveloper.notesapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding : FragmentMainBinding?=null
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
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        //calling adapter and passing reference of function to adapter
        adapter= NoteAdapter(::onNoteClicked)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
            if((Networkstatus.isNetworkAvailable(requireContext())))
            noteViewModel.getNotes()
            else
                noteViewModel.getNotesDb()



        //setting adapter

        binding.noteList.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        binding.noteList.adapter=adapter
        binding.addNote.setOnClickListener{

            findNavController().navigate(R.id.action_mainFragment_to_notesFragment)
        }

        binding.sync.setOnClickListener{
            if(Networkstatus.isNetworkAvailable(requireContext())){
                noteViewModel.sync()
                noteViewModel.getNotes()
                Toast.makeText(requireContext(),"Notes Synced!!",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),"Unable to sync notes!!\n Check Internet Connection.",Toast.LENGTH_SHORT).show()
            }
        }
        binding.logOut.setOnClickListener {
            token.saveToken(null)
            noteViewModel.logOutNote()
            authViewModel.logOutUser()

        }
        binding.search.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
        }
    }

    private fun bindObservers() {
        noteViewModel.notesLivedata.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible=false
            when(it){
                is NetworkResult.Success->{
                    adapter.submitList(it.data)
                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading ->{
                    binding.progressBar.isVisible=true
                }
                is NetworkResult.Logout->{
                    Toast.makeText(context?.applicationContext,"Logged out Successfully!!",Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack(R.id.registerFragment,false)
                    findNavController().navigate(R.id.action_mainFragment_to_registerFragment)
                }
            }
        })
    }

    //for opening note on
    private fun onNoteClicked(noteResponse: NoteResponse){
        //sending data from main to note
        val bundle=Bundle()
        bundle.putString("note",Gson().toJson(noteResponse))
        findNavController().navigate(R.id.action_mainFragment_to_notesFragment,bundle)

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}