package com.example.rickandmortyapiapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rickandmortyapiapp.database.AppDatabase
import com.example.rickandmortyapiapp.databinding.FragmentFavoriteBinding
import com.example.rickandmortyapiapp.model.Characters
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var adapter: CharactersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())
        adapter = CharactersAdapter(listOf(), {}, showFavoriteIcon = false) // Ikon tidak ditampilkan

        binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorite.adapter = adapter

        // Observe the favorite characters from the database
        db.favoriteDao().getAllFavorites().observe(viewLifecycleOwner) { favorites ->
            adapter.updateCharacters(favorites)
        }

        // Observe perubahan data favorit
        observeFavorites()
    }

    private fun observeFavorites() {
        db.favoriteDao().getAllFavorites().observe(viewLifecycleOwner) { favoriteCharacters ->
            adapter.updateCharacters(favoriteCharacters) // Hanya tampilkan karakter favorit
            binding.tvNoFavorite.visibility =
                if (favoriteCharacters.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
