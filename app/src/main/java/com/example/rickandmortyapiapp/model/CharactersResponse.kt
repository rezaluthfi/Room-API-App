package com.example.rickandmortyapiapp.model

import com.google.gson.annotations.SerializedName

data class CharactersResponse(
    @SerializedName("results")
    val results: List<Characters>
)

