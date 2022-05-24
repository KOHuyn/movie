package com.kohuyn.movie.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kohuyn.movie.model.Poster
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavouriteViewModel : ViewModel() {
    private val _posters: MutableStateFlow<List<Poster>> = MutableStateFlow(listOf())
    val posters: StateFlow<List<Poster>> get() = _posters

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    fun loadPosters() {
        viewModelScope.launch {
            flow {
                //loading
                delay(2000)
                emit(generatePostersFavourite())
            }
                .onStart { _loading.update { true } }
                .onCompletion { _loading.update { false } }
                .collect { posters ->
                    _posters.update { posters }
                }
        }
    }

    private fun generatePostersFavourite(): List<Poster> {
        return List(3) {
            Poster(
                id = it,
                title = "How to Move On in 30 Days",
                posterPath = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/2cLWJiDOZvhoIaX2r2Zb78K66JQ.jpg",
                releaseDate = "Apr 04, 2022",
                adult = false,
                overview = "We don't have an overview translated in English. Help us expand our database by adding one.",
                originalTitle = "How to Move On in 30 Days",
                originalLanguage = "English",
                backdropPath = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/2cLWJiDOZvhoIaX2r2Zb78K66JQ.jpg",
                popularity = 1.0,
                voteCount = 1,
                video = false,
                votePercent = 10
            )
        }
    }

    fun removeFavourite(id: Int) {
        _posters.update { posters ->
            posters.filterNot { it.id == id }
        }
    }
}