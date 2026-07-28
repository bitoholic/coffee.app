package coffee.app.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SortOptionTest {
    
    @Test
    fun `STARRED option exists in SortOption enum`() {
        val sortOptions = SortOption.values()
        val hasStarred = sortOptions.any { it == SortOption.STARRED }
        assertTrue(hasStarred, "SortOption.STARRED should exist in the enum")
    }
    
    @Test
    fun `STARRED has correct display name`() {
        assertEquals("Starred", SortOption.STARRED.displayName)
    }
    
    @Test
    fun `all SortOptions have non-null display names`() {
        for (option in SortOption.values()) {
            assertTrue(option.displayName != null, "Display name should not be null for $option")
        }
    }
}