package ch.pete.appconfigapp.db

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class CalendarConverter {
    companion object {
        const val ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    }

    @TypeConverter
    fun toCalendar(dateString: String): Calendar {
        val dateFormatter = SimpleDateFormat(ISO_8601_PATTERN, Locale.getDefault())
        return Calendar.getInstance().apply {
            time = dateFormatter.parse(dateString)
                ?: throw IllegalArgumentException("time is null")
        }
    }

    @TypeConverter
    fun toString(cal: Calendar): String {
        val dateFormatter = SimpleDateFormat(ISO_8601_PATTERN, Locale.getDefault())
        dateFormatter.calendar = cal
        return dateFormatter.format(cal.time)
    }
}
