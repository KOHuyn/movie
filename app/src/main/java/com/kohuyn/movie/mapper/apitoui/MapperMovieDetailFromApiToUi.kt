package com.kohuyn.movie.mapper.apitoui

import com.kohuyn.movie.mapper.base.Mapper
import com.kohuyn.movie.model.MovieDetail
import com.kohuyn.movie.model.response.MovieDetailResponse
import com.kohuyn.movie.utils.DateTimeFormatter
import com.kohuyn.movie.utils.MovieImageLoader

object MapperMovieDetailFromApiToUi :
    Mapper<MovieDetailResponse, MovieDetail> {
    override fun mapperFrom(
        from: MovieDetailResponse,
    ): MovieDetail {
        return MovieDetail(
            movieId = from.id ?: -1,
            backdropPath = MovieImageLoader.loadBackdropPath(from.backdropPath),
            title = from.title,
            overview = from.overview,
            posterPath = MovieImageLoader.loadPoster(from.posterPath),
            voteCount = from.voteCount,
            status = from.status,
            spokenLanguage = from.spokenLanguages?.ifEmpty { null }
                ?.filter { it.englishName != null }
                ?.joinToString(", ") { it.englishName ?: "" },
            releaseDate = from.releaseDate?.let {
                DateTimeFormatter.format(
                    it,
                    "yyyy-MM-dd",
                    "dd/MM/yyyy"
                )
            },
            genres = from.genres?.ifEmpty { null }
                ?.filter { it.name != null }
                ?.joinToString(", ") { it.name ?: "" },
            rating = from.voteAverage?.toFloat() ?: 0.0F,)
    }
}