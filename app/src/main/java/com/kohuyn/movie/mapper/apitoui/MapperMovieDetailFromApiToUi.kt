package com.kohuyn.movie.mapper.apitoui

import com.kohuyn.movie.mapper.base.MapperBoth
import com.kohuyn.movie.model.MovieDetail
import com.kohuyn.movie.model.MovieRecommendPreview
import com.kohuyn.movie.model.response.MovieDetailResponse
import com.kohuyn.movie.utils.DateTimeFormatter
import com.kohuyn.movie.utils.MovieImageLoader

object MapperMovieDetailFromApiToUi :
    MapperBoth<MovieDetailResponse, List<MovieRecommendPreview>, MovieDetail> {
    override fun mapperFrom(
        from1: MovieDetailResponse,
        from2: List<MovieRecommendPreview>
    ): MovieDetail {
        return MovieDetail(
            movieId = from1.id ?: -1,
            backdropPath = MovieImageLoader.loadBackdropPath(from1.backdropPath),
            title = from1.title,
            overview = from1.overview,
            posterPath = MovieImageLoader.loadPoster(from1.posterPath),
            voteCount = from1.voteCount,
            status = from1.status,
            spokenLanguage = from1.spokenLanguages?.ifEmpty { null }
                ?.filter { it.englishName != null }
                ?.joinToString(", ") { it.englishName ?: "" },
            releaseDate = from1.releaseDate?.let {
                DateTimeFormatter.format(
                    it,
                    "yyyy-MM-dd",
                    "dd/MM/yyyy"
                )
            },
            genres = from1.genres?.ifEmpty { null }
                ?.filter { it.name != null }
                ?.joinToString(", ") { it.name ?: "" },
            rating = from1.voteAverage?.toFloat() ?: 0.0F,
            movieRecommendations = from2
        )
    }
}