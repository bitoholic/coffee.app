package coffee.app.origin

import coffee.app.data.database.Origin
import coffee.app.data.repository.OriginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State for the Origin Picker ViewModel
 */
data class OriginPickerState(
    val origins: List<Origin> = emptyList(),
    val customOriginInput: String = "",
    val errorMessage: String? = null,
    val isCreating: Boolean = false
)

/**
 * ViewModel for managing the origin picker UI state
 */
class OriginPickerViewModel(
    private val originRepository: OriginRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _state = MutableStateFlow(OriginPickerState())
    val state: StateFlow<OriginPickerState> = _state.asStateFlow()

    init {
        loadOrigins()
    }

    private fun loadOrigins() {
        coroutineScope.launch {
            originRepository.getAll().collect { origins ->
                _state.update { 
                    it.copy(origins = origins) 
                }
            }
        }
    }

    fun onCustomOriginInputChanged(input: String) {
        _state.update {
            it.copy(customOriginInput = input, errorMessage = null)
        }
    }

    fun createCustomOrigin(name: String) {
        if (name.isBlank()) {
            _state.update {
                it.copy(errorMessage = "Origin name cannot be blank")
            }
            return
        }

        // Check for case-insensitive duplicate
        val existingOrigins = _state.value.origins
        val isDuplicate = existingOrigins.any { 
            it.name.equals(name, ignoreCase = true) 
        }

        if (isDuplicate) {
            _state.update {
                it.copy(errorMessage = "An origin with this name already exists")
            }
            return
        }

        // Create new custom origin 
        val newOrigin = Origin(name.trim(), isCustom = true)
        
        _state.update {
            it.copy(
                isCreating = true,
                errorMessage = null,
                customOriginInput = ""
            )
        }

        // Add to repository
        coroutineScope.launch {
            try {
                originRepository.add(newOrigin)
                
                // Refresh the list to include newly added origin
                originRepository.getAll().collect { origins ->
                    _state.update { 
                        it.copy(origins = origins, isCreating = false) 
                    }
                }
            } catch (e: Exception) {
                // Handle error appropriately
                _state.update {
                    it.copy(isCreating = false, errorMessage = "Failed to create origin")
                }
            }
        }
    }

    /**
     * Returns a sorted list of origins with predefined first, then custom, then alphabetically.
     */
    fun getSortedOrigins(): List<Origin> {
        return _state.value.origins.sortedWith(
            compareByDescending<Origin> { !it.isCustom } // Predefined first, then custom
                .thenBy(String.CASE_INSENSITIVE_ORDER) { it.name }
        )
    }

    fun selectOrigin(origin: Origin) {
        // In a real implementation this would communicate back to the caller
    }
}