package com.kohuyn.movie.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeFormatter {
    fun format(dateStr: String, oldPattern: String, newPattern: String): String {
        return try {
            SimpleDateFormat(oldPattern, Locale.ROOT).parse(dateStr)?.let { date ->
                SimpleDateFormat(newPattern, Locale.ROOT).format(date)
            } ?: dateStr
        } catch (e: Exception) {
            dateStr
        }
    }
}