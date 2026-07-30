package coffee.app.list

import coffee.app.data.database.BrewEntry
import coffee.app.data.database.BrewEntryDao
import coffee.app.data.repository.BrewEntryRepository
import coffee.app.domain.SortOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BrewEntryListViewModelTest {

    class FakeBrewEntryDao : BrewEntryDao {
        private val entries = mutableListOf<BrewEntry>()

        override suspend fun upsert(entry: BrewEntry) {
            val index = entries.indexOfFirst { it.uuid == entry.uuid }
            if (index >= 0) entries[index] = entry else entries.add(entry)
        }
        override suspend fun deleteByUuid(uuid: String) { entries.removeIf { it.uuid == uuid } }
        override suspend fun deleteByUuids(uuids: List<String>) { entries.removeIf { it.uuid in uuids } }
        override suspend fun getById(uuid: String): BrewEntry? = entries.find { it.uuid == uuid }
        override suspend fun getAll(): List<BrewEntry> = entries.toList()
        override suspend fun updateFavourite(uuid: String, isFavourite: Boolean) {
            entries.indexOfFirst { it.uuid == uuid }.let { i ->
                if (i >= 0) entries[i] = entries[i].copy(isFavourite = if (isFavourite) 1 else 0)
            }
        }
        override fun observeFavourites(): Flow<List<BrewEntry>> = MutableStateFlow(entries.filter { it.isFavourite == 1 })
        override fun observeAllCreatedDateDesc(): Flow<List<BrewEntry>> = MutableStateFlow(entries.sortedByDescending { it.createdDate })
        override fun observeAllCreatedDate(): Flow<List<BrewEntry>> = MutableStateFlow(entries.sortedBy { it.createdDate })
        override fun observeAllBeanNameAZ(): Flow<List<BrewEntry>> = MutableStateFlow(entries.sortedBy { it.beanName })
        override fun observeAllBeanNameDesc(): Flow<List<BrewEntry>> = MutableStateFlow(entries.sortedByDescending { it.beanName })
        override fun observeAllOriginAZ(): Flow<List<BrewEntry>> = MutableStateFlow(entries.sortedBy { it.beanOrigin })
        override fun observeAllOriginDesc(): Flow<List<BrewEntry>> = MutableStateFlow(entries.sortedByDescending { it.beanOrigin })
        override fun observeAllLastModifiedDate(): Flow<List<BrewEntry>> = MutableStateFlow(entries.sortedByDescending { it.lastModifiedDate })
        override fun observeAllLastModifiedDateAsc(): Flow<List<BrewEntry>> = MutableStateFlow(entries.sortedBy { it.lastModifiedDate })
    }

    @Test
    fun `toggleFavourite toggles favourite status`() {
        runBlocking {
            val dao = FakeBrewEntryDao()
            val repo = object : BrewEntryRepository(dao) {}
            val vm = BrewEntryListViewModel(repo, Dispatchers.Unconfined)

            dao.upsert(BrewEntry(uuid = "1", beanName = "Test", beanOrigin = "O", roastType = "L", grinderSetting = 15, portionWeight = 50.0, description = "D", createdDate = 1, lastModifiedDate = 1))
            vm.toggleFavourite("1")
            kotlinx.coroutines.delay(50)
            val entry = dao.getById("1")
            assertEquals(1, entry?.isFavourite)
        }
    }

    @Test
    fun `setSortOption updates sort option`() {
        runBlocking {
            val dao = FakeBrewEntryDao()
            val repo = object : BrewEntryRepository(dao) {}
            val vm = BrewEntryListViewModel(repo, Dispatchers.Unconfined)

            vm.setSortOption(SortOption.BeanNameAsc)
            assertEquals(SortOption.BeanNameAsc, vm.currentSortOption.value)
        }
    }

    @Test
    fun `toggleSelection adds and removes items`() {
        runBlocking {
            val repo = object : BrewEntryRepository(FakeBrewEntryDao()) {}
            val vm = BrewEntryListViewModel(repo, Dispatchers.Unconfined)

            vm.toggleSelection("1")
            assertTrue(vm.selectedIds.value.contains("1"))
            vm.toggleSelection("1")
            assertFalse(vm.selectedIds.value.contains("1"))
        }
    }

    @Test
    fun `clearSelection clears all selected items`() {
        runBlocking {
            val repo = object : BrewEntryRepository(FakeBrewEntryDao()) {}
            val vm = BrewEntryListViewModel(repo, Dispatchers.Unconfined)

            vm.toggleSelection("1")
            vm.toggleSelection("2")
            vm.clearSelection()
            assertTrue(vm.selectedIds.value.isEmpty())
        }
    }

    @Test
    fun `deleteSelected deletes selected items`() {
        runBlocking {
            val dao = FakeBrewEntryDao()
            dao.upsert(BrewEntry(uuid = "1", beanName = "T", beanOrigin = "O", roastType = "L", grinderSetting = 15, portionWeight = 50.0, description = "D", createdDate = 1, lastModifiedDate = 1))
            val repo = object : BrewEntryRepository(dao) {}
            val vm = BrewEntryListViewModel(repo, Dispatchers.Unconfined)

            vm.toggleSelection("1")
            vm.deleteSelected()
            kotlinx.coroutines.delay(50)
            assertTrue(vm.selectedIds.value.isEmpty())
            assertEquals(0, dao.getAll().size)
        }
    }

    @Test
    fun `toggleStarredFilter toggles starred filter`() {
        runBlocking {
            val dao = FakeBrewEntryDao()
            val repo = object : BrewEntryRepository(dao) {}
            val vm = BrewEntryListViewModel(repo, Dispatchers.Unconfined)

            vm.toggleStarredFilter()
            assertTrue(vm.isStarredFilterActive.value)
            vm.toggleStarredFilter()
            assertFalse(vm.isStarredFilterActive.value)
        }
    }

    @Test
    fun `setSearchQuery updates search query`() {
        runBlocking {
            val repo = object : BrewEntryRepository(FakeBrewEntryDao()) {}
            val vm = BrewEntryListViewModel(repo, Dispatchers.Unconfined)

            vm.setSearchQuery("test")
            assertEquals("test", vm.searchQuery.value)
        }
    }
}
