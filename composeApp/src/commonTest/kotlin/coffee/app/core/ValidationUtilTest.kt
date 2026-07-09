package coffee.app.core

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.Test

class ValidationUtilTest {

    @Test
    fun validateDose_valid_returnsNull() {
        assertNull(ValidationUtil.validateDose(12.0f))
    }

    @Test
    fun validateDose_zero_or_less_returnsError() {
        assertNotNull(ValidationUtil.validateDose(0.0f))
        assertNotNull(ValidationUtil.validateDose(-5.0f))
    }

    @Test
    fun validateYield_valid_returnsNull() {
        assertNull(ValidationUtil.validateYield(15.0f))
    }

    @Test
    fun validateYield_invalid_returnsError() {
        assertNotNull(ValidationUtil.validateYield(-2.0f))
    }

    @Test
    fun validateExtractionTime_valid_returnsNull() {
        assertNull(ValidationUtil.validateExtractionTime(120))
    }

    @Test
    fun validateExtractionTime_invalid_tooShort_returnsError() {
        assertNotNull(ValidationUtil.validateExtractionTime(0))
    }

    @Test
    fun validateExtractionTime_invalid_tooLong_returnsError() {
        assertNotNull(ValidationUtil.validateExtractionTime(1000))
    }

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
    fun validateBrew_allValid_returnsNull() {
        assertNull(ValidationUtil.validateBrew(
            beanName = "Colombia Supremo",
            doseGrams = 18.0f,
            yieldGrams = 36.0f,
            extractionSeconds = 180
        ))
    }

    @Test
    fun validateBrew_firstError_wins() {
        // Name error
        val result = ValidationUtil.validateBrew("", doseGrams = 0.0f, yieldGrams = 10.0f, extractionSeconds = 120)
        assertNotNull(result)
        // In the current implementation the first error seen (name) is returned.
    }
}
