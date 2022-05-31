package com.kohuyn.movie.utils

object MovieImageLoader {
    fun loadPoster(path: String?): String? {
        return path?.let { "https://www.themoviedb.org/t/p/w600_and_h900_bestv2$path" }
    }

    fun loadBackdropPath(path: String?): String? {
        return path?.let { "https://image.tmdb.org/t/p/original$path" }
    }

    fun loadFacePath(path: String?): String? {
        return path?.let { "https://www.themoviedb.org/t/p/w138_and_h175_face/$path" }
    }
}