package com.kohuyn.movie.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.kohuyn.movie.mapper.apitoui.MapperDiscoverFromApiToUi
import com.kohuyn.movie.model.Poster
import com.kohuyn.movie.model.response.StatusResponse
import com.kohuyn.movie.network.RetrofitUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException


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
                emit(RetrofitUtils.apiService.getDiscover())
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

    private fun getApiError(e: Throwable): StatusResponse {
        return if (e is HttpException) {
            try {
                Gson().fromJson(e.response()?.errorBody()?.string(), StatusResponse::class.java)
            } catch (e: JsonParseException) {
                StatusResponse(-1, "Json Parser Exception")
            }
        } else {
            StatusResponse(-1, e.message ?: "Unknown message")
        }
    }

    fun setMessageShown(message: String) {
        _messages.update { messages ->
            messages.filterNot { it == message }
        }
    }
}