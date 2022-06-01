package com.kohuyn.movie.utils

object MovieImageLoader {
    fun load(path: String?): String? {
        return path?.let { "https://www.themoviedb.org/t/p/w600_and_h900_bestv2$path" }
    }
}