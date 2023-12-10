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
import com.sandeveloper.notesapp.databinding.FragmentRegisterBinding
import com.sandeveloper.notesapp.models.UserRequest
import com.sandeveloper.notesapp.utils.Constants.TAG
import com.sandeveloper.notesapp.utils.NetworkResult
import com.sandeveloper.notesapp.utils.Networkstatus
import com.sandeveloper.notesapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding:FragmentRegisterBinding?=null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentRegisterBinding.inflate(inflater,container,false)

        //getting the token and checking if it is present to open the app in logged in state
        if (tokenManager.getToken()!=null){
            findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUp.setOnClickListener{
            if(Networkstatus.isNetworkAvailable(requireContext())) {

                val validationResults = validateUserInput()
                if (validationResults.first) {
                    authViewModel.registerUser(getuserRequest())
                } else {
                    binding.txtError.text = validationResults.second
                }
            }else{
                Toast.makeText(context,"Internet Unavilable!!",Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnLogin.setOnClickListener{
           findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        bindObservers()
    }
    private fun getuserRequest():UserRequest{
        val emailAddress = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        val userName = binding.txtUsername.text.toString()
        return UserRequest(userName,emailAddress,password)
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val userRequest = getuserRequest()

        return authViewModel.validateCredentials(userRequest.username,userRequest.email,userRequest.password,false)
    }

    private fun bindObservers() {
        authViewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible=false
            when(it){
                is NetworkResult.Success->{
                    //storing token
                    tokenManager.saveToken(it.data!!.token)
                    findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
                }
                is NetworkResult.Error->{
                    binding.txtError.text= it.message
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