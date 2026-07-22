package coffee.app.core

import coffee.app.data.database.BrewEntry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidationUtilTest {

    @Test
    fun validateBeanName_valid_returnsNull() {
        assertNull(ValidationUtil.validateBeanName("Ethiopian Yirgacheffe"))
    }

    @Test
    fun validateBeanName_empty_returnsError() {
        assertNotNull(ValidationUtil.validateBeanName(""))
        assertNotNull(ValidationUtil.validateBeanName(" "))
    }

    @Test
    fun validateGrinderSetting_valid_returnsNull() {
        assertNull(ValidationUtil.validateGrinderSetting(1))
        assertNull(ValidationUtil.validateGrinderSetting(24))
        assertNull(ValidationUtil.validateGrinderSetting(48))
    }

    @Test
    fun validateGrinderSetting_invalid_returnsError() {
        assertNotNull(ValidationUtil.validateGrinderSetting(0))
        assertNotNull(ValidationUtil.validateGrinderSetting(49))
        assertNotNull(ValidationUtil.validateGrinderSetting(-1))
    }

    @Test
    fun validateRoastType_valid_returnsNull() {
        assertNull(ValidationUtil.validateRoastType("Light"))
        assertNull(ValidationUtil.validateRoastType("Medium"))
        assertNull(ValidationUtil.validateRoastType("Dark"))
    }

    @Test
    fun validateRoastType_invalid_returnsError() {
        assertNotNull(ValidationUtil.validateRoastType("light"))
        assertNotNull(ValidationUtil.validateRoastType(""))
        assertNotNull(ValidationUtil.validateRoastType("Espresso"))
    }

    @Test
    fun validatePortionWeight_valid_returnsNull() {
        assertNull(ValidationUtil.validatePortionWeight(0.1))
        assertNull(ValidationUtil.validatePortionWeight(18.0))
        assertNull(ValidationUtil.validatePortionWeight(100.0))
    }

    @Test
    fun validatePortionWeight_invalid_returnsError() {
        assertNotNull(ValidationUtil.validatePortionWeight(0.0))
        assertNotNull(ValidationUtil.validatePortionWeight(-5.0))
    }

    @Test
    fun validateDescription_null_returnsNull() {
        assertNull(ValidationUtil.validateDescription(null))
    }

    @Test
    fun validateDescription_empty_returnsNull() {
        assertNull(ValidationUtil.validateDescription(""))
    }

    @Test
    fun validateDescription_underLimit_returnsNull() {
        val text = "A".repeat(500)
        assertNull(ValidationUtil.validateDescription(text))
    }

    @Test
    fun validateDescription_overLimit_returnsError() {
        val text = "A".repeat(501)
        assertNotNull(ValidationUtil.validateDescription(text))
    }

    @Test
    fun validateBrewEntry_allValid_returnsEmpty() {
        val entry = BrewEntry(
            beanName = "Colombia Supremo",
            roastType = "Medium",
            grinderSetting = 15,
            portionWeight = 18.0,
            createdDate = 1000L,
            lastModifiedDate = 1000L
        )
        val errors = ValidationUtil.validateBrewEntry(entry)
        assertTrue(errors.isEmpty(), "Expected no validation errors, got: $errors")
    }

    @Test
    fun validateBrewEntry_returnsFieldErrors() {
        val entry = BrewEntry(
            beanName = "   ",
            roastType = "InvalidRoast",
            grinderSetting = 99,
            portionWeight = -1.0,
            description = "A".repeat(501),
            createdDate = 1000L,
            lastModifiedDate = 1000L
        )
        val errors = ValidationUtil.validateBrewEntry(entry)
        assertEquals(5, errors.size)
        assertNotNull(errors["beanName"])
        assertNotNull(errors["roastType"])
        assertNotNull(errors["grinderSetting"])
        assertNotNull(errors["portionWeight"])
        assertNotNull(errors["description"])
    }
}
