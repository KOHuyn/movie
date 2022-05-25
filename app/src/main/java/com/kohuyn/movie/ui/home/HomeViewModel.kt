package com.kohuyn.movie.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kohuyn.movie.mapper.apitoui.MapperDiscoverFromApiToUi
import com.kohuyn.movie.model.Poster
import com.kohuyn.movie.network.RetrofitUtils
import com.kohuyn.movie.utils.getApiError
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _posters: MutableStateFlow<List<Poster>> = MutableStateFlow(listOf())
    val posters: StateFlow<List<Poster>> get() = _posters

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _messages: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
    val messages: StateFlow<List<String>> get() = _messages

    fun loadPosters() {
        viewModelScope.launch {
            flow {
                emit(RetrofitUtils.apiService.getDiscoverMovie())
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

    fun setMessageShown(message: String) {
        _messages.update { messages ->
            messages.filterNot { it == message }
        }
    }
}