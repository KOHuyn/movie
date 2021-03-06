package com.kohuyn.movie.mapper.base

interface Mapper<F, T> {
    fun mapperFrom(from: F): T
}

interface MapperBoth<F1, F2, T> {
    fun mapperFrom(from1: F1, from2: F2): T
}

interface MapperTriple<F1, F2, F3, T> {
    fun mapperFrom(from1: F1, from2: F2, from3: F3): T
}