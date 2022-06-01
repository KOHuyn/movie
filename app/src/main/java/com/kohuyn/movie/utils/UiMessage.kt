package com.kohuyn.movie.utils

data class UiMessage<T>(
    val message: String,
    val code: Int = -1,
    val type: T? = null
)