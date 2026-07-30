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
    fun `each option's display name matches expected value`() {
        val expectedNames = mapOf(
            SortOption.CreatedDateAsc to "Date Added ↑",
            SortOption.CreatedDateDesc to "Date Added ↓",
            SortOption.BeanNameAsc to "Bean Name ↑",
            SortOption.BeanNameDesc to "Bean Name ↓",
            SortOption.OriginAsc to "Bean Origin ↑",
            SortOption.OriginDesc to "Bean Origin ↓",
            SortOption.LastModifiedDateAsc to "Date Modified ↑",
            SortOption.LastModifiedDateDesc to "Date Modified ↓"
        )
        
        for ((option, expectedName) in expectedNames) {
            assertEquals(expectedName, option.displayName)
        }
    }
}