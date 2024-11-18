package com.example.rickandmortyapiapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmortyapiapp.databinding.ItemCharacterBinding
import com.example.rickandmortyapiapp.model.Characters

class CharactersAdapter(private var characters: List<Characters>) : RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder>() {

    inner class CharactersViewHolder(val binding: ItemCharacterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder {
        val binding = ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharactersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) {
        val character = characters[position]
        holder.binding.tvCharName.text = character.name
        holder.binding.tvCharStatus.text = character.status
        holder.binding.tvCharSpecies.text = character.species
        holder.binding.tvCharGender.text = character.gender
        Glide.with(holder.itemView.context)
            .load("https://rickandmortyapi.com/api/character/avatar/${character.id}.jpeg")
            .into(holder.binding.imgChar)

        // Ketika item diklik, akan menampilan toast nama karakter tsb
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "${character.name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return characters.size
    }

    // Fungsi untuk memperbarui data adapter
    fun updateCharacters(newCharacters: List<Characters>) {
        characters = newCharacters
        notifyDataSetChanged()
    }
}