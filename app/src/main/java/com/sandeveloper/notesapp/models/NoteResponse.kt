package com.sandeveloper.notesapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Notes")
data class NoteResponse(
    @PrimaryKey()
    val _id: String,
    val title: String,
    val description: String,
    val userId: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)