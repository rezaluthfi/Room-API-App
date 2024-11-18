package com.example.rickandmortyapiapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorite_characters")
data class Characters(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("species")
    val species: String,

    @SerializedName("gender")
    val gender: String
)

