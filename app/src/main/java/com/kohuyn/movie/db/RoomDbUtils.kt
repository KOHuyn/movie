package com.kohuyn.movie.db

import androidx.room.Room
import com.kohuyn.movie.MovieApp

object RoomDbUtils {
    @Volatile
    private var db: AppDatabase? = null

    @Synchronized
    fun getRoomDatabase(): AppDatabase {
        return db ?: synchronized(this) {
            Room.databaseBuilder(MovieApp.getInstance(), AppDatabase::class.java, "movie.db")
                .build()
        }.also { db = it }
    }

    val movieDao get() = getRoomDatabase().getMovieDao()
}