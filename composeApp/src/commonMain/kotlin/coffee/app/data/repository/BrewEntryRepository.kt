package coffee.app.data.repository

import coffee.app.data.database.BrewEntry
import coffee.app.data.database.BrewEntryDao
import coffee.app.domain.SortOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

open class BrewEntryRepository(
    protected val brewEntryDao: BrewEntryDao
) {
    open suspend fun add(entry: BrewEntry) = withContext(Dispatchers.IO) {
        brewEntryDao.upsert(entry)
    }

    open suspend fun update(entry: BrewEntry) = withContext(Dispatchers.IO) {
        brewEntryDao.upsert(entry)
    }

    open suspend fun delete(uuid: String) = withContext(Dispatchers.IO) {
        brewEntryDao.deleteByUuid(uuid)
    }

    open suspend fun getById(uuid: String): BrewEntry? = withContext(Dispatchers.IO) {
        brewEntryDao.getById(uuid)
    }

    open fun getAll(sort: SortOption = SortOption.CreatedDateDesc): Flow<List<BrewEntry>> {
        return when (sort) {
            SortOption.CreatedDateDesc -> brewEntryDao.observeAllCreatedDateDesc()
            SortOption.BeanNameAZ -> brewEntryDao.observeAllBeanNameAZ()
            SortOption.OriginAZ -> brewEntryDao.observeAllOriginAZ()
            SortOption.CreatedDate -> brewEntryDao.observeAllCreatedDate()
            SortOption.LastModifiedDate -> brewEntryDao.observeAllLastModifiedDate()
        }
    }
}
