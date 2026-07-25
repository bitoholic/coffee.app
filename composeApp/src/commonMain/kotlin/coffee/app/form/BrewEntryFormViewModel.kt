package coffee.app.form

import coffee.app.core.DateFormatUtil
import coffee.app.core.ValidationUtil
import coffee.app.data.database.BrewEntry
import coffee.app.data.database.EntryPhoto
import coffee.app.data.database.EntryPhotoDao
import coffee.app.data.database.Origin
import coffee.app.data.repository.BrewEntryRepository
import coffee.app.data.repository.OriginRepository
import coffee.app.domain.RoastType
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormState(
    val beanName: String = "",
    val beanOrigin: String = "",
    val roastType: RoastType? = null,
    val grinderSetting: String = "",
    val portionWeight: String = "",
    val description: String = "",
    val photos: List<String> = emptyList(),
    val validationErrors: Map<String, String> = emptyMap(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val origins: List<Origin> = emptyList(),
    val isEditing: Boolean = false,
    val originalValues: BrewEntry? = null,
    val originalPhotos: List<String> = emptyList()
)

class BrewEntryFormViewModel(
    private val brewEntryRepository: BrewEntryRepository,
    private val originRepository: OriginRepository,
    private val entryPhotoDao: EntryPhotoDao,
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
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _state.update {
                it.copy(grinderSetting = value, validationErrors = emptyMap(), saveSuccess = false)
            }
        }
    }

    fun onPortionWeightChanged(value: String) {
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

    fun addPhoto(path: String) {
        val current = _state.value.photos
        if (current.size < 10) {
            _state.update { it.copy(photos = current + path) }
        }
    }

    fun removePhoto(index: Int) {
        val current = _state.value.photos.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _state.update { it.copy(photos = current) }
        }
    }

    fun enterEditMode(entry: BrewEntry) {
        var existingPhotos = emptyList<String>()
        coroutineScope.launch {
            entryPhotoDao.getPhotosForEntry(entry.uuid).collect { entryPhotos ->
                existingPhotos = entryPhotos.map { it.photoPath }
            }
        }
        _state.update {
            it.copy(
                beanName = entry.beanName,
                beanOrigin = entry.beanOrigin ?: "",
                roastType = RoastType.valueOf(entry.roastType),
                grinderSetting = entry.grinderSetting.toString(),
                portionWeight = entry.portionWeight.toString(),
                description = entry.description ?: "",
                photos = existingPhotos,
                originalPhotos = existingPhotos,
                isEditing = true,
                originalValues = entry,
                validationErrors = emptyMap(),
                saveSuccess = false
            )
        }
    }

    fun isDirty(): Boolean {
        val state = _state.value
        if (state.isEditing && state.originalValues != null) {
            val original = state.originalValues
            return state.beanName != original.beanName ||
                    state.beanOrigin != (original.beanOrigin ?: "") ||
                    state.roastType != RoastType.valueOf(original.roastType) ||
                    state.grinderSetting != original.grinderSetting.toString() ||
                    state.portionWeight != original.portionWeight.toString() ||
                    state.description != (original.description ?: "") ||
                    state.photos != state.originalPhotos
        }
        return state.beanName.isNotBlank() ||
                state.beanOrigin.isNotBlank() ||
                state.roastType != null ||
                state.grinderSetting.isNotBlank() ||
                state.portionWeight.isNotBlank() ||
                state.description.isNotBlank() ||
                state.photos.isNotEmpty()
    }

    fun save() {
        val currentState = _state.value

        val grinderSetting = currentState.grinderSetting.toIntOrNull()
        val portionWeight = currentState.portionWeight.toDoubleOrNull()
        val roastTypeStr = currentState.roastType?.name

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
            val entryUuid = if (currentState.isEditing) currentState.originalValues!!.uuid else UUID.randomUUID().toString()
            val entry = BrewEntry(
                beanName = currentState.beanName.trim(),
                beanOrigin = currentState.beanOrigin.ifBlank { null },
                roastType = roastTypeStr!!,
                grinderSetting = grinderSetting!!,
                portionWeight = portionWeight!!,
                description = currentState.description.ifBlank { null },
                createdDate = if (currentState.isEditing) currentState.originalValues!!.createdDate else now,
                lastModifiedDate = now,
                uuid = entryUuid
            )
            brewEntryRepository.add(entry)

            // Replace all photos for this entry
            entryPhotoDao.deleteByEntryUuid(entryUuid)
            currentState.photos.forEachIndexed { index, path ->
                entryPhotoDao.insert(EntryPhoto(entryUuid = entryUuid, photoPath = path, sortOrder = index))
            }

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
                originalPhotos = emptyList(),
                beanName = "",
                beanOrigin = "",
                roastType = null,
                grinderSetting = "",
                portionWeight = "",
                description = "",
                photos = emptyList(),
                validationErrors = emptyMap(),
                saveSuccess = false
            ) 
        }
    }
}
