package coffee.app.core

import coffee.app.data.database.BrewEntry
import coffee.app.domain.RoastType

object ValidationUtil {

    fun validateBeanName(name: String): String? {
        return if (name.isBlank()) "Bean name is required" else null
    }

    fun validateGrinderSetting(setting: Int): String? {
        return if (setting < 1 || setting > 48) {
            "Grinder setting must be between 1 and 48"
        } else null
    }

    fun validateRoastType(type: String): String? {
        return try {
            RoastType.valueOf(type)
            null
        } catch (_: IllegalArgumentException) {
            "Roast type must be one of Light, Medium, or Dark"
        }
    }

    fun validatePortionWeight(weight: Double): String? {
        return if (weight <= 0) "Portion weight must be greater than 0" else null
    }

    fun validateDescription(desc: String?): String? {
        if (desc == null) return null
        return if (desc.length > 500) "Description must be 500 characters or fewer" else null
    }

    fun validateBrewEntry(entry: BrewEntry): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        validateBeanName(entry.beanName)?.let { errors["beanName"] = it }
        validateRoastType(entry.roastType)?.let { errors["roastType"] = it }
        validateGrinderSetting(entry.grinderSetting)?.let { errors["grinderSetting"] = it }
        validatePortionWeight(entry.portionWeight)?.let { errors["portionWeight"] = it }
        validateDescription(entry.description)?.let { errors["description"] = it }
        return errors
    }
}
