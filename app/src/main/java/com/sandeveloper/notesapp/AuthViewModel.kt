package com.sandeveloper.notesapp

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeveloper.notesapp.models.UserRequest
import com.sandeveloper.notesapp.models.UserResponse
import com.sandeveloper.notesapp.repository.UserRepository
import com.sandeveloper.notesapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository):ViewModel() {
    val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = userRepository.userResponseLiveData

    fun registerUser(userRequest: UserRequest){
        viewModelScope.launch {
            userRepository.registerUser(userRequest)
        }

    }
    fun loginUser(userRequest: UserRequest){
        viewModelScope.launch {
            userRepository.loginUser(userRequest)
        }
    }
    fun logOutUser(){
        viewModelScope.launch {
            userRepository.userLogOut()
        }
    }
    //check if credentials are entered by users or not
    fun validateCredentials(username: String,emailaddress:String,password:String,isLogin:Boolean):Pair<Boolean,String> {
        var result = Pair(true,"")
        if(TextUtils.isEmpty(emailaddress) || (!isLogin && TextUtils.isEmpty(username)) || TextUtils.isEmpty(password)){
            result = Pair(false,"Please Provide Credentials")
        }else if(!Patterns.EMAIL_ADDRESS.matcher(emailaddress).matches()){
            result=Pair(false,"please provide valid email")
        }else if(password.length<=5){
            result = Pair(false,"Password Length Should Be greater than 5")
        }
        return result
    }
}