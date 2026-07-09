package coffee.app.core

import kotlin.test.Test
import kotlin.test.assertEquals

class DateFormatUtilTest {

    @Test
    fun formatDate_knownTimestamp_returnsUTCISODate() {
        // 2025-01-01T00:00:00Z
        val ts = 1735689600000L
        assertEquals("2025-01-01", DateFormatUtil.formatDateLong(ts))
    }
}
