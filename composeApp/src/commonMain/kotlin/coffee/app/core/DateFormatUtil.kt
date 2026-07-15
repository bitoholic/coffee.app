package coffee.app.core

import kotlinx.datetime.Instant

object DateFormatUtil {
    fun formatDateLong(dateTime: Long): String =
        Instant.fromEpochMilliseconds(dateTime).toString().substringBefore('T')

    fun formatDateShort(dateTime: Long): String = formatDateLong(dateTime)
}
