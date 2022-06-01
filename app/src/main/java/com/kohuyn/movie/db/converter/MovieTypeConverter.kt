package com.kohuyn.movie.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MovieTypeConverter {
    @TypeConverter
    fun fromString(value: List<Int>) = Gson().toJson(value)

    @TypeConverter
    fun toListInt(value: String): List<Int> = try {
        Gson().fromJson(value, object : TypeToken<List<Int>>() {}.type)
    } catch (e: Exception) {
        emptyList()
    }
}