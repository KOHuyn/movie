package com.kohuyn.movie.mapper.apitoui

import com.kohuyn.movie.mapper.base.Mapper
import com.kohuyn.movie.model.Cast
import com.kohuyn.movie.model.response.SeriesCastResponse
import com.kohuyn.movie.utils.MovieImageLoader

object MapperSeriesCastFromSocketToUi : Mapper<SeriesCastResponse, List<Cast>> {
    override fun mapperFrom(from: SeriesCastResponse): List<Cast> {
        return from.cast?.take(8)?.map {
            Cast(
                castId = it.castId ?: -1,
                castPath = MovieImageLoader.loadFacePath(it.profilePath),
                castName = it.name ?: "",
                characterName = it.character ?: "",
            )
        } ?: emptyList()
    }
}