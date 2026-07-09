package coffee.app.core

/**
 * Thread-safe validation engine for brew parameters.
 *
 * All functions are pure and operate on primitive inputs, so they are inherently
 * safe to call from multiple coroutines / threads.
 */
object ValidationUtil {

    // Reasonable physical bounds for a single brew.
    const val MIN_DOSE_GRAMS = 0.0f
    const val MIN_YIELD_GRAMS = 0.0f
    const val MIN_EXTRACTION_SECONDS = 1
    const val MAX_EXTRACTION_SECONDS = 600 // 10 minutes – beyond this is unrealistic

    /**
     * Validates the dose weight in grams.
     * @return null if valid, otherwise a human-readable error.
     */
    fun validateDose(doseGrams: Float): String? = when {
        doseGrams.isNaN() -> "Dose must be a number"
        doseGrams <= MIN_DOSE_GRAMS -> "Dose must be greater than 0 g"
        else -> null
    }

    /**
     * Validates the yield weight in grams.
     */
    fun validateYield(yieldGrams: Float): String? = when {
        yieldGrams.isNaN() -> "Yield must be a number"
        yieldGrams <= MIN_YIELD_GRAMS -> "Yield must be greater than 0 g"
        else -> null
    }

    /**
     * Validates extraction time in seconds (reasonable brewing window).
     */
    fun validateExtractionTime(seconds: Int): String? = when {
        seconds < MIN_EXTRACTION_SECONDS -> "Extraction time must be at least 1 second"
        seconds > MAX_EXTRACTION_SECONDS -> "Extraction time seems too long (>$MAX_EXTRACTION_SECONDS s)"
        else -> null
    }

    /**
     * Validates a non-empty bean/coffee name.
     */
    fun validateBeanName(name: String): String? =
        if (name.isBlank()) "Bean name cannot be empty" else null

    /**
     * Runs all validations and returns the first error found, or null if all pass.
     * Iteration order is deterministic for stable UX messaging.
     */
    fun validateBrew(
        beanName: String,
        doseGrams: Float,
        yieldGrams: Float,
        extractionSeconds: Int
    ): String? {
        return validateBeanName(beanName)
            ?: validateDose(doseGrams)
            ?: validateYield(yieldGrams)
            ?: validateExtractionTime(extractionSeconds)
    }
}
