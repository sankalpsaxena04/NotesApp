package com.sandeveloper.notesapp.models

data class User(
    val _id: String,
    val username: String,
    val email: String,
    val password: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)