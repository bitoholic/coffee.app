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
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var collectionJob: Job? = null
    
    init {
        collectEntries(SortOption.CreatedDateDesc)
    }
    
    private fun collectEntries(sort: SortOption) {
        collectionJob?.cancel()
        collectionJob = viewModelScope.launch {
            _isLoading.value = true
            if (sort == SortOption.STARRED) {
                // For STARRED sort, observe favourites directly instead of all entries
                brewEntryRepository.observeFavourites().collectLatest { entries ->
                    // Apply search filter after sorting
                    val filteredEntries = if (_searchQuery.value.isNotBlank()) {
                        entries.filter { entry ->
                            entry.beanName.contains(_searchQuery.value, true)
                        }
                    } else {
                        entries
                    }
                    _entries.value = filteredEntries
                    _isLoading.value = false
                }
            } else {
                // For all other sorts, use regular getAll
                brewEntryRepository.getAll(sort).collectLatest { entries ->
                    // Apply search filter after sorting
                    val filteredEntries = if (_searchQuery.value.isNotBlank()) {
                        entries.filter { entry ->
                            entry.beanName.contains(_searchQuery.value, true)
                        }
                    } else {
                        entries
                    }
                    _entries.value = filteredEntries
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun setSortOption(sortOption: SortOption) {
        _currentSortOption.value = sortOption
        collectEntries(sortOption)
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        // Re-collect entries to apply filtering
        collectEntries(_currentSortOption.value)
    }
    
    fun onCleared() {
        collectionJob?.cancel()
        viewModelScope.cancel()
    }
    
    fun deleteEntry(uuid: String) {
        viewModelScope.launch {
            brewEntryRepository.delete(uuid)
        }
    }

    fun toggleFavourite(uuid: String) {
        viewModelScope.launch {
            val entry = brewEntryRepository.getById(uuid)
            if (entry != null) {
                val newState = if (entry.isFavourite == 0) 1 else 0
                brewEntryRepository.updateFavourite(uuid, newState == 1)
            }
        }
    }
}