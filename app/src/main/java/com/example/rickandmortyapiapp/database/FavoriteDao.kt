package com.example.rickandmortyapiapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rickandmortyapiapp.model.Characters

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(character: Characters)

    @Query("DELETE FROM favorite_characters WHERE id = :characterId")
    suspend fun removeFavoriteById(characterId: Int)

    @Query("SELECT * FROM favorite_characters")
    fun getAllFavorites(): LiveData<List<Characters>>
}




