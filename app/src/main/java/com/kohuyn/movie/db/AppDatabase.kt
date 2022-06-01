package com.kohuyn.movie.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kohuyn.movie.db.converter.MovieTypeConverter
import com.kohuyn.movie.db.dao.MovieDao
import com.kohuyn.movie.model.entity.MovieEntity

@Database(entities = [MovieEntity::class], version = 1)
@TypeConverters(MovieTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getMovieDao(): MovieDao
}