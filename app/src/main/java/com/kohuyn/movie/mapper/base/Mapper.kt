package com.kohuyn.movie.mapper.base

interface Mapper<F, T> {
    fun mapperFrom(from: F): T
}