package com.example.rickandmortyapiapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmortyapiapp.databinding.ItemCharacterBinding
import com.example.rickandmortyapiapp.model.Characters

class CharactersAdapter(
    private var characters: List<Characters>,
    private val onFavoriteClick: (Characters) -> Unit,
    private val showFavoriteIcon: Boolean = true
) : RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder>() {

    inner class CharactersViewHolder(val binding: ItemCharacterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder {
        val binding = ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharactersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) {
        val character = characters[position]
        with(holder.binding) {
            tvCharName.text = character.name
            tvCharStatus.text = character.status
            tvCharSpecies.text = character.species
            tvCharGender.text = character.gender

            Glide.with(holder.itemView.context)
                .load("https://rickandmortyapi.com/api/character/avatar/${character.id}.jpeg")
                .into(imgChar)

            // Tampilkan atau sembunyikan ikon favorit
            if (showFavoriteIcon) {
                val favoriteIcon = if (character.isFavorite) R.drawable.icon_favorite_red else R.drawable.icon_favorite_line
                imgFavorite.setImageResource(favoriteIcon)
                imgFavorite.visibility = View.VISIBLE
                imgFavorite.setOnClickListener {
                    onFavoriteClick(character) // Toggle favorite status
                }
            } else {
                imgFavorite.visibility = View.GONE // Sembunyikan ikon
            }

            // Jika item diklik, tampilkan toast
            holder.itemView.setOnClickListener {
                Toast.makeText(
                    holder.itemView.context,
                    "${character.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun getItemCount(): Int = characters.size

    fun updateCharacters(newCharacters: List<Characters>) {
        characters = newCharacters
        notifyDataSetChanged()
    }
}
