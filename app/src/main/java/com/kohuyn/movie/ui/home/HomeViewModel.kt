package com.kohuyn.movie.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kohuyn.movie.db.RoomDbUtils
import com.kohuyn.movie.mapper.apitodb.MapperMovieFromApiToDb
import com.kohuyn.movie.mapper.apitoui.MapperMovieFromApiToUi
import com.kohuyn.movie.mapper.dbtoui.MapperMovieFromDbToUi
import com.kohuyn.movie.model.Poster
import com.kohuyn.movie.network.RetrofitUtils
import com.kohuyn.movie.utils.UiMessage
import com.kohuyn.movie.utils.addMessage
import com.kohuyn.movie.utils.getApiError
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel : ViewModel() {

    val movieDao = RoomDbUtils.movieDao

    private val _posters: MutableStateFlow<List<Poster>> = MutableStateFlow(listOf())
    val posters: StateFlow<List<Poster>> get() = _posters

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _messages: MutableStateFlow<List<UiMessage<Unit>>> = MutableStateFlow(listOf())
    val messages: StateFlow<List<UiMessage<Unit>>> get() = _messages

    private fun getDiscoverApi(): Flow<List<Poster>> {
        return flow {
            emit(RetrofitUtils.apiService.getDiscoverMovie())
        }
            .onEach { response ->
                val moviesDb = MapperMovieFromApiToDb.mapperFrom(response)
                movieDao.insertAll(moviesDb)
            }.map { MapperMovieFromApiToUi.mapperFrom(it) }
            .onEach { Timber.d("getDiscoverApi") }
    }

    private fun getDiscoverDb(): Flow<List<Poster>> {
        return flow { emit(movieDao.getAllMovie()) }
            .map { MapperMovieFromDbToUi.mapperFrom(it) }
            .onEach { Timber.d("getDiscoverDb") }
    }

    fun loadPosters() {
        viewModelScope.launch {
            kotlin.run {
                if (movieDao.getAllMovie().isNotEmpty()) {
                    getDiscoverDb()
                } else {
                    getDiscoverApi()
                }
            }
                .onStart { _loading.update { true } }
                .onCompletion { _loading.update { false } }
                .catch { e ->
                    val statusResponse = getApiError(e)
                    _messages.addMessage(statusResponse.statusMessage)
                }
                .collect { posters ->
                    _posters.update { posters }
                }
        }
    }

    fun setMessageShown(message: String) {
        _messages.update { messages ->
            messages.filterNot { it.message == message }
        }
    }
}