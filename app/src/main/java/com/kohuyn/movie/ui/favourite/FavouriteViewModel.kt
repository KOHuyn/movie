package com.kohuyn.movie.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.kohuyn.movie.mapper.apitoui.MapperDiscoverFromApiToUi
import com.kohuyn.movie.model.Poster
import com.kohuyn.movie.network.RetrofitUtils
import com.kohuyn.movie.utils.getApiError
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavouriteViewModel : ViewModel() {
    private val _posters: MutableStateFlow<List<Poster>> = MutableStateFlow(listOf())
    val posters: StateFlow<List<Poster>> get() = _posters

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _messages: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
    val messages: StateFlow<List<String>> get() = _messages

    fun loadPosters() {
        viewModelScope.launch {
            flow {
                emit(RetrofitUtils.apiService.getFavoritesMovie())
            }
                .onStart { _loading.update { true } }
                .onCompletion { _loading.update { false } }
                .catch { e ->
                    val statusResponse = getApiError(e)
                    _messages.update { messages ->
                        messages.toMutableList().apply {
                            add(statusResponse.statusMessage)
                        }
                    }
                }
                .collect { posters ->
                    _posters.update {
                        posters.results.map {
                            MapperDiscoverFromApiToUi.mapperFrom(it)
                        }
                    }
                }
        }
    }

    fun removeFavorite(postId: Int) {
        viewModelScope.launch {
            val data = JsonObject().apply {
                addProperty("media_type", "movie")
                addProperty("media_id", postId)
                addProperty("favorite", false)
            }
            flow { emit(RetrofitUtils.apiService.favorite(body = data)) }
                .catch { e ->
                    val statusResponse = getApiError(e)
                    _messages.update { messages ->
                        messages.toMutableList().apply {
                            add(statusResponse.statusMessage)
                        }
                    }
                }
                .collect {
                    _posters.update { posters ->
                        posters.filterNot { it.id == postId }
                    }
                }
        }
    }

    fun setMessageShown(message: String) {
        _messages.update { messages ->
            messages.filterNot { it == message }
        }
    }
}