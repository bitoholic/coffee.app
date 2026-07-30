package coffee.app.settings

import coffee.app.settings.RestoreMode
import kotlin.test.Test
import kotlin.test.assertEquals

class RestoreModeTest {
    
    @Test
    fun `OVERWRITE and MERGE enum values exist`() {
        val values = RestoreMode.values()
        assertEquals(2, values.size)
        assertEquals(RestoreMode.OVERWRITE, values[0])
        assertEquals(RestoreMode.MERGE, values[1])
    }
}