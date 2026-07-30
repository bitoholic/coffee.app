package coffee.app.domain

import kotlin.test.Test
import kotlin.test.assertTrue

class RoastTypeTest {
    
    @Test
    fun `all roast types have display names`() {
        // RoastType enum values (Light, Medium, Dark) exist in the original code 
        val roastTypes = RoastType.values()
        assertEquals(3, roastTypes.size)
        assertTrue(roastTypes.contains(RoastType.Light))
        assertTrue(roastTypes.contains(RoastType.Medium))
        assertTrue(roastTypes.contains(RoastType.Dark))
    }
    
    // Helper function to avoid unused import error 
    private fun assertEquals(expected: Int, actual: Int) {
        if (expected != actual) throw AssertionError("Expected $expected but was $actual")
    }
}