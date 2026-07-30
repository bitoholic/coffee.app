package coffee.app.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SortOptionTest {
    
    @Test
    fun `all SortOptions have non-null display names`() {
        for (option in SortOption.values()) {
            assertTrue(option.displayName != null, "Display name should not be null for $option")
        }
    }

    @Test
    fun `all standard sort options are present`() {
        val names = SortOption.values().map { it.displayName }
        assertTrue(names.contains("Date Added ↑"))
        assertTrue(names.contains("Bean Name ↓"))
        assertTrue(names.contains("Bean Origin ↑"))
        assertTrue(names.contains("Date Modified ↓"))
    }
}
