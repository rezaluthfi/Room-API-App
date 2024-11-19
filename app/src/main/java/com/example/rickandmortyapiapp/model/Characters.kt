package com.example.rickandmortyapiapp.model

import androidx.room.Entity
import androidx.room.Ignore
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
    val gender: String,

    @Ignore // Abaikan oleh Room, hanya digunakan dalam aplikasi
    var isFavorite: Boolean = false // Default: bukan favorit
) {
    // Constructor sekunder untuk Room
    constructor(
        id: Int,
        name: String,
        status: String,
        species: String,
        gender: String
    ) : this(id, name, status, species, gender, false)
}

