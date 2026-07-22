package coffee.app.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DateFormatUtilTest {

    @Test
    fun formatDate_knownTimestamp_returnsFormattedString() {
        // 2025-01-01T00:00:00Z
        val ts = 1735689600000L
        val formatted = DateFormatUtil.formatDate(ts)
        assertTrue(formatted.startsWith("2025-01-01"), "Expected 2025-01-01 but got: $formatted")
    }

    @Test
    fun formatDateLong_knownTimestamp_returnsUTCISODate() {
        // 2025-01-01T00:00:00Z
        val ts = 1735689600000L
        assertEquals("2025-01-01", DateFormatUtil.formatDateLong(ts))
    }

    @Test
    fun nowMillis_returnsPositiveValue() {
        val now = DateFormatUtil.nowMillis()
        assertTrue(now > 1700000000000L, "Current time should be after 2023")
    }
}
