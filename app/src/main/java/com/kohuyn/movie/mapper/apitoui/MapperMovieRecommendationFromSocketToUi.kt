package com.kohuyn.movie.mapper.apitoui

import com.kohuyn.movie.mapper.base.Mapper
import com.kohuyn.movie.model.MovieRecommendPreview
import com.kohuyn.movie.model.response.PostersResponse
import com.kohuyn.movie.utils.MovieImageLoader

object MapperMovieRecommendationFromSocketToUi :
    Mapper<PostersResponse.ItemPosterResponse, MovieRecommendPreview> {
    override fun mapperFrom(from: PostersResponse.ItemPosterResponse): MovieRecommendPreview {
        return MovieRecommendPreview(
            id = from.id ?: -1,
            title = from.title ?: "-",
            posterPath = MovieImageLoader.loadBackdropPath(from.backdropPath),
            ratePercent = from.voteAverage?.times(10)
                ?.let { "%.2f".format(it)+ "%" }
        )
    }
}