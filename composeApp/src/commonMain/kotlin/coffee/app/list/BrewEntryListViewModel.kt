package coffee.app.list

import coffee.app.data.database.BrewEntry
import coffee.app.data.repository.BrewEntryRepository
import coffee.app.domain.SortOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var collectionJob: Job? = null
    
    init {
        collectEntries(SortOption.CreatedDateDesc)
    }
    
    private fun collectEntries(sort: SortOption) {
        collectionJob?.cancel()
        collectionJob = viewModelScope.launch {
            _isLoading.value = true
            brewEntryRepository.getAll(sort).collectLatest { entries ->
                _entries.value = entries
                _isLoading.value = false
            }
        }
    }
    
    fun setSortOption(sortOption: SortOption) {
        _currentSortOption.value = sortOption
        collectEntries(sortOption)
    }
    
    fun onCleared() {
        collectionJob?.cancel()
        viewModelScope.cancel()
    }
}
