package com.kohuyn.movie.ui.movie.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.kohuyn.movie.mapper.apitoui.MapperMovieDetailFromApiToUi
import com.kohuyn.movie.mapper.apitoui.MapperMovieRecommendationFromSocketToUi
import com.kohuyn.movie.mapper.apitoui.MapperMovieVideosFromApiToUi
import com.kohuyn.movie.mapper.apitoui.MapperSeriesCastFromSocketToUi
import com.kohuyn.movie.model.Cast
import com.kohuyn.movie.model.MovieDetail
import com.kohuyn.movie.model.MovieRecommendPreview
import com.kohuyn.movie.model.Video
import com.kohuyn.movie.model.response.MovieDetailResponse
import com.kohuyn.movie.model.response.MovieVideoResponse
import com.kohuyn.movie.model.response.PostersResponse
import com.kohuyn.movie.model.response.SeriesCastResponse
import com.kohuyn.movie.network.RetrofitUtils
import com.kohuyn.movie.utils.UiMessage
import com.kohuyn.movie.utils.addMessage
import com.kohuyn.movie.utils.getApiError
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class TypeMovieDetailUi {
    MOVIE, SERIES_CAST, RECOMMENDATIONS, VIDEOS, FAVORITE
}

data class MovieUiState(
    //data
    val movieDetail: MovieDetail? = null,
    val movieRecommendations: List<MovieRecommendPreview> = emptyList(),
    val seriesCast: List<Cast> = emptyList(),
    val videos: List<Video> = emptyList(),
    val isFavorite: Boolean = false,
    //state
    val isLoadingMovieDetail: Boolean = false,
    val isLoadingSeriesCast: Boolean = false,
    val isLoadingRecommendation: Boolean = false,
    val isLoadingVideos: Boolean = false,
    val isLoadingFavorite: Boolean = false,
    val messageError: Set<UiMessage<TypeMovieDetailUi>> = emptySet(),
) {
    companion object {
        fun getDefault() = MovieUiState()
    }
}

class MovieDetailViewModel : ViewModel() {
    private val _movieUiState: MutableStateFlow<MovieUiState> =
        MutableStateFlow(MovieUiState.getDefault())
    val movieUiState: StateFlow<MovieUiState> get() = _movieUiState

    private fun getMovieDetailResponse(movieId: Int): Flow<MovieDetailResponse> {
        return flow { emit(RetrofitUtils.apiService.getMovieDetail(movieId)) }
            .onStart {
                _movieUiState.update { movieUiState ->
                    movieUiState.copy(isLoadingMovieDetail = true)
                }
            }
            .onCompletion {
                _movieUiState.update { movieUiState ->
                    movieUiState.copy(isLoadingMovieDetail = false)
                }
            }
            .onEach { movie ->
                _movieUiState.update { movieUiState ->
                    val movieDetail = MapperMovieDetailFromApiToUi.mapperFrom(movie)
                    movieUiState.copy(movieDetail = movieDetail)
                }
            }
            .catch { e ->
                _movieUiState.update { movieUiState ->
                    val statusResponse = getApiError(e)
                    movieUiState.copy(
                        messageError = movieUiState.messageError
                            .addMessage(statusResponse.statusMessage, TypeMovieDetailUi.MOVIE)
                    )
                }
            }
    }

    private fun getMovieRecommendationResponse(movieId: Int): Flow<PostersResponse> {
        return flow { emit(RetrofitUtils.apiService.getMovieRecommendations(movieId)) }
            .onStart {
                _movieUiState.update { movieUiState ->
                    movieUiState.copy(isLoadingRecommendation = true)
                }
            }
            .onCompletion {
                _movieUiState.update { movieUiState ->
                    movieUiState.copy(isLoadingRecommendation = false)
                }
            }
            .onEach { recommendation ->
                _movieUiState.update { movieUiState ->
                    val movieRecommendationList =
                        MapperMovieRecommendationFromSocketToUi.mapperFrom(recommendation)
                    movieUiState.copy(movieRecommendations = movieRecommendationList)
                }
            }
            .catch { e ->
                _movieUiState.update { movieUiState ->
                    val statusResponse = getApiError(e)
                    movieUiState.copy(
                        messageError = movieUiState.messageError
                            .addMessage(statusResponse, TypeMovieDetailUi.RECOMMENDATIONS)
                    )
                }
            }
    }

    private fun getSeriesCastResponse(movieId: Int): Flow<SeriesCastResponse> {
        return flow { emit(RetrofitUtils.apiService.getSeriesCastMovie(movieId)) }
            .onStart {
                _movieUiState.update { movieUiState ->
                    movieUiState.copy(isLoadingSeriesCast = true)
                }
            }
            .onCompletion {
                _movieUiState.update { movieUiState ->
                    movieUiState.copy(isLoadingSeriesCast = false)
                }
            }
            .onEach { seriesCastResponse ->
                _movieUiState.update { movieUiState ->
                    val seriesCast =
                        MapperSeriesCastFromSocketToUi.mapperFrom(seriesCastResponse)
                    movieUiState.copy(seriesCast = seriesCast)
                }
            }
            .catch { e ->
                _movieUiState.update { movieUiState ->
                    val statusResponse = getApiError(e)
                    movieUiState.copy(
                        messageError = movieUiState.messageError
                            .addMessage(statusResponse, TypeMovieDetailUi.SERIES_CAST)
                    )
                }
            }
    }

    private fun getMovieVideoResponse(movieId: Int): Flow<MovieVideoResponse> {
        return flow { emit(RetrofitUtils.apiService.getVideoMovie(movieId)) }
            .onStart {
                _movieUiState.update { movieUiState ->
                    movieUiState.copy(isLoadingVideos = true)
                }
            }
            .onCompletion {
                _movieUiState.update { movieUiState ->
                    movieUiState.copy(isLoadingVideos = false)
                }
            }
            .onEach { videosResponse ->
                _movieUiState.update { movieUiState ->
                    val videos = MapperMovieVideosFromApiToUi.mapperFrom(videosResponse)
                    movieUiState.copy(videos = videos)
                }
            }
            .catch { e ->
                _movieUiState.update { movieUiState ->
                    val statusResponse = getApiError(e)
                    movieUiState.copy(
                        messageError = movieUiState.messageError
                            .addMessage(statusResponse, TypeMovieDetailUi.SERIES_CAST)
                    )
                }
            }
    }

    fun getMovie(movieId: Int) {
        merge(
            getMovieDetailResponse(movieId),
            getMovieRecommendationResponse(movieId),
            getSeriesCastResponse(movieId),
            getMovieVideoResponse(movieId)
        ).launchIn(viewModelScope)
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
                    _movieUiState.update { movieUiState ->
                        movieUiState.copy(isLoadingFavorite = true)
                    }
                }
                .onCompletion {
                    _movieUiState.update { movieUiState ->
                        movieUiState.copy(isLoadingFavorite = false)
                    }
                }
                .catch { e ->
                    _movieUiState.update { movieUiState ->
                        val statusResponse = getApiError(e)
                        movieUiState.copy(
                            messageError = movieUiState.messageError
                                .addMessage(statusResponse, TypeMovieDetailUi.FAVORITE)
                        )
                    }
                }
                .collect {
                    _movieUiState.update { movie ->
                        movie.copy(isFavorite = movie.isFavorite.not())
                    }
                }
        }
    }

    fun setMessageShown(message: String) {
        _movieUiState.update { uiState ->
            uiState.copy(messageError = uiState.messageError.filterNot { it.message == message }
                .toSet())
        }
    }

}