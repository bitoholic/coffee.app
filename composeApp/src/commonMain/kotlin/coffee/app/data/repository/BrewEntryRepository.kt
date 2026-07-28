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
            SortOption.CreatedDateAsc -> brewEntryDao.observeAllCreatedDate()
            SortOption.BeanNameAsc -> brewEntryDao.observeAllBeanNameAZ()
            SortOption.BeanNameDesc -> brewEntryDao.observeAllBeanNameDesc()
            SortOption.OriginAsc -> brewEntryDao.observeAllOriginAZ()
            SortOption.OriginDesc -> brewEntryDao.observeAllOriginDesc()
            SortOption.LastModifiedDateAsc -> brewEntryDao.observeAllLastModifiedDateAsc()
            SortOption.LastModifiedDateDesc -> brewEntryDao.observeAllLastModifiedDate()
            SortOption.STARRED -> brewEntryDao.observeFavourites()
        }
    }

    open fun observeFavourites(): Flow<List<BrewEntry>> = brewEntryDao.observeFavourites()

    open suspend fun updateFavourite(uuid: String, isFavourite: Boolean) {
        withContext(Dispatchers.IO) {
            brewEntryDao.updateFavourite(uuid, isFavourite)
        }
    }

    open suspend fun deleteByUuids(uuids: List<String>) = withContext(Dispatchers.IO) {
        brewEntryDao.deleteByUuids(uuids)
    }
}
