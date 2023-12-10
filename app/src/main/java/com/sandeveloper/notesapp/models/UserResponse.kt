package com.sandeveloper.notesapp.models

data class UserResponse(
    val user: User,
    val token: String
)