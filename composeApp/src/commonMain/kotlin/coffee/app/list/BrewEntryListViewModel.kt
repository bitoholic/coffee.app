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
    
    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds.asStateFlow()
    val isSelectionMode: Boolean
        get() = _selectedIds.value.isNotEmpty()
    
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    
    private val _isStarredFilterActive = MutableStateFlow(false)
    val isStarredFilterActive: StateFlow<Boolean> = _isStarredFilterActive.asStateFlow()
    
    fun toggleSelection(uuid: String) {
        _selectedIds.value = if (_selectedIds.value.contains(uuid)) {
            _selectedIds.value - uuid
        } else {
            _selectedIds.value + uuid
        }
    }
    
    fun clearSelection() {
        _selectedIds.value = emptySet()
    }
    
    fun toggleStarredFilter() {
        _isStarredFilterActive.value = !_isStarredFilterActive.value
        collectEntries(_currentSortOption.value)
    }
    
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var collectionJob: Job? = null
    
    init {
        collectEntries(SortOption.CreatedDateDesc)
    }
    
    private fun collectEntries(sort: SortOption) {
        collectionJob?.cancel()
        collectionJob = viewModelScope.launch {
            _isLoading.value = true
            val sourceFlow = if (_isStarredFilterActive.value) {
                brewEntryRepository.observeFavourites()
            } else {
                brewEntryRepository.getAll(sort)
            }
            sourceFlow.collectLatest { entries ->
                var sorted = if (_isStarredFilterActive.value) {
                    sortEntries(entries, sort)
                } else {
                    entries
                }
                val filteredEntries = if (_searchQuery.value.isNotBlank()) {
                    sorted.filter { entry ->
                        entry.beanName.contains(_searchQuery.value, true)
                    }
                } else {
                    sorted
                }
                _entries.value = filteredEntries
                _isLoading.value = false
            }
        }
    }

    private fun sortEntries(entries: List<BrewEntry>, sort: SortOption): List<BrewEntry> {
        return when (sort) {
            SortOption.CreatedDateDesc -> entries.sortedByDescending { it.createdDate }
            SortOption.CreatedDateAsc -> entries.sortedBy { it.createdDate }
            SortOption.BeanNameAsc -> entries.sortedBy { it.beanName.lowercase() }
            SortOption.BeanNameDesc -> entries.sortedByDescending { it.beanName.lowercase() }
            SortOption.OriginAsc -> entries.sortedBy { it.beanOrigin?.lowercase() ?: "" }
            SortOption.OriginDesc -> entries.sortedByDescending { it.beanOrigin?.lowercase() ?: "" }
            SortOption.LastModifiedDateAsc -> entries.sortedBy { it.lastModifiedDate }
            SortOption.LastModifiedDateDesc -> entries.sortedByDescending { it.lastModifiedDate }
        }
    }
    
    fun setSortOption(sortOption: SortOption) {
        _currentSortOption.value = sortOption
        collectEntries(sortOption)
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
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
    
    fun deleteSelected() {
        val selectedIdsList = selectedIds.value.toList()
        if (selectedIdsList.isNotEmpty()) {
            viewModelScope.launch { 
                brewEntryRepository.deleteByUuids(selectedIdsList) 
                _snackbarMessage.value = "Deleted ${selectedIdsList.size} entries"
            }
            clearSelection()
        }
    }
}
