package com.kohuyn.movie.db.dao

import androidx.room.*
import com.kohuyn.movie.model.entity.MovieEntity

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Query("SELECT * FROM MOVIE_ENTITY")
    suspend fun getAllMovie(): List<MovieEntity>

    @Query("DELETE FROM MOVIE_ENTITY")
    suspend fun deleteAll()
}