package com.sandeveloper.notesapp.login_signup_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sandeveloper.notesapp.AuthViewModel
import com.sandeveloper.notesapp.R
import com.sandeveloper.notesapp.databinding.FragmentLoginBinding
import com.sandeveloper.notesapp.models.UserRequest
import com.sandeveloper.notesapp.utils.Constants.TAG
import com.sandeveloper.notesapp.utils.NetworkResult
import com.sandeveloper.notesapp.utils.Networkstatus
import com.sandeveloper.notesapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding?=null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    @Inject
    lateinit var tokenManager: TokenManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentLoginBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLog.setOnClickListener{
            if(Networkstatus.isNetworkAvailable(requireContext())) {
                val validateUserInput = validateUserInput()
                if (validateUserInput.first) {
                    authViewModel.loginUser(getuserRequest())
                } else {
                    binding.txtError.text = validateUserInput.second
                }
            }else{
                Toast.makeText(context,"Unable to connect to internet!!",Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnSignup.setOnClickListener{
            findNavController().popBackStack()
        }
        bindObservers()
    }
    private fun getuserRequest(): UserRequest {
        val emailAddress = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        return UserRequest("",emailAddress,password)
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val userRequest = getuserRequest()
        return authViewModel.validateCredentials(userRequest.username,userRequest.email,userRequest.password,true)
    }

    private fun bindObservers() {
        authViewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible=false
            when(it){
                is NetworkResult.Success->{
                   //storing token
                    Log.d(TAG,"before token")
                    Log.d(TAG,it.data!!.token)
                    tokenManager.saveToken(it.data.token)
                    Log.d(TAG,"AFTER token")
                    Log.d(TAG,it.data!!.toString())
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                }
                is NetworkResult.Error->{
                    binding.txtError.text= it.message
                    Log.d(TAG,it.message.toString())
                }
                is NetworkResult.Loading->{
                    binding.progressBar.isVisible=true

                }
                is NetworkResult.Logout->{}
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}