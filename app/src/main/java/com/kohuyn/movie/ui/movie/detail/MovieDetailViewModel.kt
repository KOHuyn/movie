package com.kohuyn.movie.ui.movie.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.kohuyn.movie.mapper.apitoui.MapperMovieDetailFromApiToUi
import com.kohuyn.movie.mapper.apitoui.MapperMovieRecommendationFromSocketToUi
import com.kohuyn.movie.mapper.apitoui.MapperSeriesCastFromSocketToUi
import com.kohuyn.movie.model.MovieDetail
import com.kohuyn.movie.model.response.MovieDetailResponse
import com.kohuyn.movie.model.response.PostersResponse
import com.kohuyn.movie.model.response.SeriesCastResponse
import com.kohuyn.movie.network.RetrofitUtils
import com.kohuyn.movie.utils.UiMessage
import com.kohuyn.movie.utils.addMessage
import com.kohuyn.movie.utils.getApiError
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieDetailViewModel : ViewModel() {
    private val _movieDetail: MutableStateFlow<MovieDetail?> = MutableStateFlow(null)
    val movieDetail: StateFlow<MovieDetail?> get() = _movieDetail

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading
    private val _loadingMovieRecommendation: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loadingMovieRecommendation: StateFlow<Boolean> get() = _loading

    private val _loadingSeriesCast: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loadingSeriesCast: StateFlow<Boolean> get() = _loadingSeriesCast

    private val _messages: MutableStateFlow<List<UiMessage>> = MutableStateFlow(listOf())
    val messages: StateFlow<List<UiMessage>> get() = _messages

    private val _isLoadingFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loadingFavorite: StateFlow<Boolean> get() = _isLoadingFavorite

    private fun getMovieDetailResponse(movieId: Int): Flow<MovieDetailResponse> {
        return flow { emit(RetrofitUtils.apiService.getMovieDetail(movieId)) }
            .onStart { _loading.update { true } }
            .onCompletion { _loading.update { false } }
    }

    private fun getMovieRecommendationResponse(movieId: Int): Flow<PostersResponse> {
        return flow { emit(RetrofitUtils.apiService.getMovieRecommendations(movieId)) }
            .onStart { _loadingMovieRecommendation.update { true } }
            .onCompletion { _loadingMovieRecommendation.update { false } }
    }

    private fun getSeriesCastResponse(movieId: Int): Flow<SeriesCastResponse> {
        return flow { emit(RetrofitUtils.apiService.getSeriesCastMovie(movieId)) }
            .onStart { _loadingSeriesCast.update { true } }
            .onCompletion { _loadingSeriesCast.update { false } }
    }

    fun getMovie(movieId: Int) {
        viewModelScope.launch {
            combine(
                getMovieDetailResponse(movieId),
                getMovieRecommendationResponse(movieId),
                getSeriesCastResponse(movieId)
            ) { movieDetail, movieRecommendation, seriesCast ->
                val movieRecommendationList = movieRecommendation.results.map {
                    MapperMovieRecommendationFromSocketToUi.mapperFrom(it)
                }
                val seriesCastList = MapperSeriesCastFromSocketToUi.mapperFrom(seriesCast)
                MapperMovieDetailFromApiToUi.mapperFrom(
                    movieDetail,
                    movieRecommendationList,
                    seriesCastList
                )
            }
                .catch { e ->
                    val statusResponse = getApiError(e)
                    _messages.addMessage(statusResponse.statusMessage)
                }
                .collect { movieDetail ->
                    _movieDetail.update { movieDetail }
                }
        }
    }

    fun favorite(movieId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            val data = JsonObject().apply {
                addProperty("media_type", "movie")
                addProperty("media_id", movieId)
                addProperty("favorite", isFavorite)
            }
            flow {
                emit(RetrofitUtils.apiService.favorite(body = data))
            }
                .onStart {
                    _isLoadingFavorite.update { true }
                }
                .onCompletion {
                    _isLoadingFavorite.update { false }
                }
                .catch { e ->
                    val statusResponse = getApiError(e)
                    _messages.addMessage(statusResponse.statusMessage)
                }
                .collect {
                    _movieDetail.update { movie ->
                        movie?.copy(isFavorite = movie.isFavorite.not())
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