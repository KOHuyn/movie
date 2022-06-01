package com.kohuyn.movie.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kohuyn.movie.mapper.apitoui.MapperMovieFromApiToUi
import com.kohuyn.movie.model.Poster
import com.kohuyn.movie.network.RetrofitUtils
import com.kohuyn.movie.utils.UiMessage
import com.kohuyn.movie.utils.addMessage
import com.kohuyn.movie.utils.getApiError
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _posters: MutableStateFlow<List<Poster>> = MutableStateFlow(listOf())
    val posters: StateFlow<List<Poster>> get() = _posters

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _messages: MutableStateFlow<List<UiMessage<Unit>>> = MutableStateFlow(listOf())
    val messages: StateFlow<List<UiMessage<Unit>>> get() = _messages

    fun loadPosters() {
        viewModelScope.launch {
            flow {
                emit(RetrofitUtils.apiService.getDiscoverMovie())
            }
                .onStart { _loading.update { true } }
                .onCompletion { _loading.update { false } }
                .catch { e ->
                    val statusResponse = getApiError(e)
                    _messages.addMessage(statusResponse.statusMessage)
                }
                .collect { posters ->
                    _posters.update {
                        posters.results.map {
                            MapperMovieFromApiToUi.mapperFrom(it)
                        }
                    }
                }
        }
    }

    fun setMessageShown(message: String) {
        _messages.update { messages ->
            messages.filterNot { it.message == message }
        }
    }
}