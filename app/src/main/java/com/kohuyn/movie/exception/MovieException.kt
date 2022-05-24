package com.kohuyn.movie.exception

import java.io.IOException

data class MovieException(val code: Int, override val message: String) : IOException(message)