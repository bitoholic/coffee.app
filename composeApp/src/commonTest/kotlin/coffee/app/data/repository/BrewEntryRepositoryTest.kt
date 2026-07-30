package coffee.app.data.repository

import coffee.app.data.database.BrewEntry
import coffee.app.data.database.BrewEntryDao
import coffee.app.domain.SortOption
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class BrewEntryRepositoryTest {
    
    @Test
    fun `getAll returns correct flows for SortOptions`() = runBlocking {
        val fakeDao = FakeBrewEntryDao()
        val repo = BrewEntryRepository(fakeDao)
        
        // Test all SortOptions return flows
        val sortOptions = SortOption.values()
        for (sortOption in sortOptions) {
            val flow = repo.getAll(sortOption)
            val result = flow.first()
            assertTrue(result.isEmpty())
        }
    }
}

class FakeBrewEntryDao : BrewEntryDao {
    override suspend fun upsert(entry: BrewEntry) {}
    override suspend fun deleteByUuid(uuid: String) {}
    override suspend fun deleteByUuids(uuids: List<String>) {}
    override fun observeFavourites(): kotlinx.coroutines.flow.Flow<List<BrewEntry>> = kotlinx.coroutines.flow.MutableStateFlow(emptyList())
    override suspend fun updateFavourite(uuid: String, isFavourite: Boolean) {}
    override fun observeAllCreatedDateDesc(): kotlinx.coroutines.flow.Flow<List<BrewEntry>> = kotlinx.coroutines.flow.MutableStateFlow(emptyList())
    override fun observeAllBeanNameAZ(): kotlinx.coroutines.flow.Flow<List<BrewEntry>> = kotlinx.coroutines.flow.MutableStateFlow(emptyList())
    override fun observeAllOriginAZ(): kotlinx.coroutines.flow.Flow<List<BrewEntry>> = kotlinx.coroutines.flow.MutableStateFlow(emptyList())
    override fun observeAllBeanNameDesc(): kotlinx.coroutines.flow.Flow<List<BrewEntry>> = kotlinx.coroutines.flow.MutableStateFlow(emptyList())
    override fun observeAllOriginDesc(): kotlinx.coroutines.flow.Flow<List<BrewEntry>> = kotlinx.coroutines.flow.MutableStateFlow(emptyList())
    override fun observeAllCreatedDate(): kotlinx.coroutines.flow.Flow<List<BrewEntry>> = kotlinx.coroutines.flow.MutableStateFlow(emptyList())
    override fun observeAllLastModifiedDate(): kotlinx.coroutines.flow.Flow<List<BrewEntry>> = kotlinx.coroutines.flow.MutableStateFlow(emptyList())
    override fun observeAllLastModifiedDateAsc(): kotlinx.coroutines.flow.Flow<List<BrewEntry>> = kotlinx.coroutines.flow.MutableStateFlow(emptyList())
    override suspend fun getById(uuid: String): BrewEntry? = null
    override suspend fun getAll(): List<BrewEntry> = emptyList()
}