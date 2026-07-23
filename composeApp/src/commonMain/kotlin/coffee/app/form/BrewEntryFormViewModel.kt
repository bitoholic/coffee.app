package coffee.app.form

import coffee.app.core.DateFormatUtil
import coffee.app.core.ValidationUtil
import coffee.app.data.database.BrewEntry
import coffee.app.data.database.Origin
import coffee.app.data.repository.BrewEntryRepository
import coffee.app.data.repository.OriginRepository
import coffee.app.domain.RoastType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Represents the mutable state of the brew entry form.
 */
data class FormState(
    val beanName: String = "",
    val beanOrigin: String = "",
    val roastType: RoastType? = null,
    val grinderSetting: String = "",
    val portionWeight: String = "",
    val description: String = "",
    val validationErrors: Map<String, String> = emptyMap(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val origins: List<Origin> = emptyList(),
    val isEditing: Boolean = false,
    val originalValues: BrewEntry? = null
)

/**
 * ViewModel managing the brew entry form's state, validation, and persistence.
 * Uses only KMP-compatible types — no Android ViewModel dependency required.
 */
class BrewEntryFormViewModel(
    private val brewEntryRepository: BrewEntryRepository,
    private val originRepository: OriginRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _state = MutableStateFlow(FormState())
    val state: StateFlow<FormState> = _state.asStateFlow()

    init {
        loadOrigins()
    }

    private fun loadOrigins() {
        coroutineScope.launch {
            originRepository.getAll().collect { origins ->
                _state.update { it.copy(origins = origins) }
            }
        }
    }

    fun onBeanNameChanged(value: String) {
        _state.update {
            it.copy(beanName = value, validationErrors = emptyMap(), saveSuccess = false)
        }
    }

    fun onBeanOriginChanged(value: String) {
        _state.update {
            it.copy(beanOrigin = value, validationErrors = emptyMap(), saveSuccess = false)
        }
    }

    fun onRoastTypeChanged(value: RoastType?) {
        _state.update {
            it.copy(roastType = value, validationErrors = emptyMap(), saveSuccess = false)
        }
    }

    fun onGrinderSettingChanged(value: String) {
        // Only allow digits and empty string for numeric input
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _state.update {
                it.copy(grinderSetting = value, validationErrors = emptyMap(), saveSuccess = false)
            }
        }
    }

    fun onPortionWeightChanged(value: String) {
        // Allow digits and a single decimal point
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            _state.update {
                it.copy(portionWeight = value, validationErrors = emptyMap(), saveSuccess = false)
            }
        }
    }

    fun onDescriptionChanged(value: String) {
        _state.update {
            it.copy(description = value, validationErrors = emptyMap(), saveSuccess = false)
        }
    }

    /**
     * Enters edit mode with the given BrewEntry
     */
    fun enterEditMode(entry: BrewEntry) {
        _state.update {
            it.copy(
                beanName = entry.beanName,
                beanOrigin = entry.beanOrigin ?: "",
                roastType = RoastType.valueOf(entry.roastType),
                grinderSetting = entry.grinderSetting.toString(),
                portionWeight = entry.portionWeight.toString(),
                description = entry.description ?: "",
                isEditing = true,
                originalValues = entry,
                validationErrors = emptyMap(),
                saveSuccess = false
            )
        }
    }

    /**
     * Checks if form state has been modified from original values
     */
    fun isDirty(): Boolean {
        val state = _state.value
        if (!state.isEditing || state.originalValues == null) return false
        
        val original = state.originalValues
        return state.beanName != original.beanName ||
                state.beanOrigin != (original.beanOrigin ?: "") ||
                state.roastType != RoastType.valueOf(original.roastType) ||
                state.grinderSetting != original.grinderSetting.toString() ||
                state.portionWeight != original.portionWeight.toString() ||
                state.description != (original.description ?: "")
    }

    /**
     * Validates the current form state and persists if valid.
     * Sets validationErrors on failure or saveSuccess on success.
     */
    fun save() {
        val currentState = _state.value

        // Parse numeric fields
        val grinderSetting = currentState.grinderSetting.toIntOrNull()
        val portionWeight = currentState.portionWeight.toDoubleOrNull()
        val roastTypeStr = currentState.roastType?.name

        // Validate all fields
        val errors = mutableMapOf<String, String>()

        ValidationUtil.validateBeanName(currentState.beanName)
            ?.let { errors["beanName"] = it }

        if (roastTypeStr == null) {
            errors["roastType"] = "Roast type must be one of Light, Medium, or Dark"
        } else {
            ValidationUtil.validateRoastType(roastTypeStr)
                ?.let { errors["roastType"] = it }
        }

        if (grinderSetting == null) {
            errors["grinderSetting"] = "Grinder setting must be a number between 1 and 48"
        } else {
            ValidationUtil.validateGrinderSetting(grinderSetting)
                ?.let { errors["grinderSetting"] = it }
        }

        if (portionWeight == null) {
            errors["portionWeight"] = "Portion weight must be a positive number"
        } else {
            ValidationUtil.validatePortionWeight(portionWeight)
                ?.let { errors["portionWeight"] = it }
        }

        val descForValidation = currentState.description.ifBlank { null }
        ValidationUtil.validateDescription(descForValidation)
            ?.let { errors["description"] = it }

        if (errors.isNotEmpty()) {
            _state.update { it.copy(validationErrors = errors) }
            return
        }

        _state.update { it.copy(isSaving = true) }

        coroutineScope.launch {
            val now = DateFormatUtil.nowMillis()
            val entry = BrewEntry(
                beanName = currentState.beanName.trim(),
                beanOrigin = currentState.beanOrigin.ifBlank { null },
                roastType = roastTypeStr!!,
                grinderSetting = grinderSetting!!,
                portionWeight = portionWeight!!,
                description = currentState.description.ifBlank { null },
                createdDate = if (currentState.isEditing) currentState.originalValues!!.createdDate else now,
                lastModifiedDate = now,
                uuid = if (currentState.isEditing) currentState.originalValues!!.uuid else ""
            )
            brewEntryRepository.add(entry)
            _state.update { it.copy(isSaving = false, saveSuccess = true, validationErrors = emptyMap()) }
        }
    }

    fun resetSaveSuccess() {
        _state.update { it.copy(saveSuccess = false) }
    }
    
    fun clearEditState() {
        _state.update { 
            it.copy(
                isEditing = false,
                originalValues = null,
                beanName = "",
                beanOrigin = "",
                roastType = null,
                grinderSetting = "",
                portionWeight = "",
                description = "",
                validationErrors = emptyMap(),
                saveSuccess = false
            ) 
        }
    }
}