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

    fun formatToTimestamp(dateStr: String?, pattern: String): Long? {
        if (dateStr == null) return null
        return try {
            SimpleDateFormat(pattern, Locale.ROOT).parse(dateStr)?.time
        } catch (e: Exception) {
            null
        }
    }
}