package com.example.rickandmortyapiapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rickandmortyapiapp.database.AppDatabase
import com.example.rickandmortyapiapp.databinding.FragmentHomeBinding
import com.example.rickandmortyapiapp.model.Characters
import com.example.rickandmortyapiapp.network.ApiClient
import com.example.rickandmortyapiapp.model.CharactersResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val client = ApiClient.getInstance()
    private lateinit var adapter: CharactersAdapter
    private lateinit var db: AppDatabase
    private var charactersList: List<Characters> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.getInstance(requireContext())

        // Setup the adapter for the RecyclerView
        adapter = CharactersAdapter(listOf(), { character: Characters ->
            toggleFavorite(character)
        })
        binding.rvMorty.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMorty.adapter = adapter

        // Setup search functionality
        setupSearch()

        // Fetch data from the API once
        if (charactersList.isEmpty()) {
            fetchData()
        }

        // Observe favorites once after fetching the data
        observeFavorites()
    }

    private fun setupSearch() {
        binding.svSearch.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = if (!newText.isNullOrEmpty()) {
                    charactersList.filter { it.name.contains(newText, ignoreCase = true) }
                } else {
                    charactersList
                }
                adapter.updateCharacters(filteredList)
                binding.tvDataNotFound.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
                return true
            }
        })
    }


    private fun fetchData() {
        val response = client.getAllCharacters()
        response.enqueue(object : Callback<CharactersResponse> {
            override fun onResponse(call: Call<CharactersResponse>, response: Response<CharactersResponse>) {
                val characterList = response.body()?.results ?: return
                lifecycleScope.launch {
                    // Ambil ID karakter favorit dari database
                    db.favoriteDao().getAllFavorites().observe(viewLifecycleOwner) { favorites ->
                        val favoriteIds = favorites.map { it.id }.toSet()

                        // Update status isFavorite untuk setiap karakter
                        charactersList = characterList.map { it.copy(isFavorite = it.id in favoriteIds) }

                        // Perbarui adapter dengan status favorit yang benar
                        adapter.updateCharacters(charactersList)
                    }
                }
            }

            override fun onFailure(call: Call<CharactersResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Koneksi Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleFavorite(character: Characters) {
        lifecycleScope.launch {
            // Toggle the favorite status
            val newFavoriteStatus = !character.isFavorite
            character.isFavorite = newFavoriteStatus

            // Update Room database
            if (newFavoriteStatus) {
                db.favoriteDao().addFavorite(character)
            } else {
                db.favoriteDao().removeFavoriteById(character.id)
            }

            // Perbarui status karakter dalam daftar secara lokal (tanpa memuat ulang seluruh daftar)
            val updatedList = charactersList.map {
                if (it.id == character.id) it.copy(isFavorite = newFavoriteStatus) else it
            }

            // Tetap gunakan hasil pencarian yang ada, jika ada
            val query = binding.svSearch.query.toString()
            val filteredList = if (query.isNotEmpty()) {
                updatedList.filter { it.name.contains(query, ignoreCase = true) }
            } else {
                updatedList
            }

            // Update adapter dengan data yang baru tanpa memuat ulang API
            charactersList = updatedList
            adapter.updateCharacters(filteredList)
        }
    }


    // Untuk mengamati perubahan data favorit
    private fun observeFavorites() {
        db.favoriteDao().getAllFavorites().observe(viewLifecycleOwner) { favoriteCharacters ->
            val favoriteIds = favoriteCharacters.map { it.id }.toSet()
            charactersList = charactersList.map { it.copy(isFavorite = it.id in favoriteIds) }

            // Pastikan daftar yang sedang ditampilkan tetap diperbarui
            val query = binding.svSearch.query.toString()
            val filteredList = if (query.isNotEmpty()) {
                charactersList.filter { it.name.contains(query, ignoreCase = true) }
            } else {
                charactersList
            }
            adapter.updateCharacters(filteredList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
