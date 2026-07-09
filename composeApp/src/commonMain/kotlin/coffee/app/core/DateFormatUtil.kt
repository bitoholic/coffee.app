package coffee.app.core

import kotlinx.datetime.Instant
import kotlinx.datetime.format.DateTimeFormatter

object DateFormatUtil {

    fun formatDateLong(dateTime: Long): String {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(Instant.fromEpochMilliseconds(dateTime))
    }

    fun formatDateShort(dateTime: Long): String = formatDateLong(dateTime)
}
