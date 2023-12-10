package com.sandeveloper.notesapp.models

data class UserRequest(
    val username: String,
    val email: String,
    val password: String
)