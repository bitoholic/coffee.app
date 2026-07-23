package coffee.app.list

import coffee.app.data.database.BrewEntry
import coffee.app.data.repository.BrewEntryRepository
import coffee.app.domain.SortOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BrewEntryListViewModel(
    private val brewEntryRepository: BrewEntryRepository
) {
    
    private val _entries = MutableStateFlow<List<BrewEntry>>(emptyList())
    val entries: StateFlow<List<BrewEntry>> = _entries.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _currentSortOption = MutableStateFlow(SortOption.CreatedDateDesc)
    val currentSortOption: StateFlow<SortOption> = _currentSortOption.asStateFlow()
    
    private val _selectedEntry = MutableStateFlow<BrewEntry?>(null)
    val selectedEntry: StateFlow<BrewEntry?> = _selectedEntry.asStateFlow()
    
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    init {
        loadEntries()
    }
    
    fun loadEntries(sortOption: SortOption? = null) {
        val sort = sortOption ?: _currentSortOption.value
        _currentSortOption.value = sort
        _isLoading.value = true
        
        // Collect data from repository based on current sort option
        // This will automatically re-subscribe when sort changes
        viewModelScope.launch {
            brewEntryRepository.getAll(sort).collectLatest { entries ->
                _entries.value = entries
                _isLoading.value = false
            }
        }
    }
    
    fun setSortOption(sortOption: SortOption) {
        _currentSortOption.value = sortOption
        loadEntries(sortOption)
    }
    
    fun selectEntry(entry: BrewEntry) {
        _selectedEntry.value = entry
    }
    
    fun clearSelection() {
        _selectedEntry.value = null
    }
    
    fun getEntryById(uuid: String): BrewEntry? {
        return _entries.value.find { it.uuid == uuid }
    }
    
    fun onCleared() {
        viewModelScope.cancel()
    }
}