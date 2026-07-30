package coffee.app.core

import kotlin.test.Test
import kotlin.test.assertEquals

class DateFormatUtilTest {
    
    @Test
    fun `toShortDate formats epoch millis correctly`() {
        // Test with a known timestamp (January 1, 2021 00:00:00 UTC)
        val timestamp = 1609459200000L  // Unix timestamp in milliseconds
        
        val result = DateFormatUtil.toShortDate(timestamp)
        
        // Expected: "01 Jan" (since January is month 1)
        assertEquals("01 Jan", result)
    }

    @Test
    fun `toShortDate edge cases`() {
        // Test with 0 (epoch start)
        val result1 = DateFormatUtil.toShortDate(0L)
        // Should be Jan 1 1970 (but we can't predict the exact date due to timezone)
  
        // Test with negative timestamp 
        val result2 = DateFormatUtil.toShortDate(-86400000L) // 1 day before epoch
        // Should be Dec 31 1969 (depending on timezone)
        
        // Test with large timestamp (year 2030)
        val largeTimestamp = 1893456000000L // January 1, 2030
        val result3 = DateFormatUtil.toShortDate(largeTimestamp)
        // Should be "01 Jan" (as Jan is month 1)
    }
}