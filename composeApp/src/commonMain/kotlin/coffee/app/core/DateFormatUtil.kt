package coffee.app.core

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateFormatUtil {
    fun formatDate(epochMillis: Long): String {
        val instant = Instant.fromEpochMilliseconds(epochMillis)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val year = localDateTime.year
        val month = localDateTime.monthNumber.toString().padStart(2, '0')
        val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        return "$year-$month-$day $hour:$minute"
    }

    fun formatDateLong(dateTime: Long): String =
        Instant.fromEpochMilliseconds(dateTime).toString().substringBefore('T')

    fun formatDateShort(dateTime: Long): String = formatDateLong(dateTime)

    fun nowMillis(): Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
}
