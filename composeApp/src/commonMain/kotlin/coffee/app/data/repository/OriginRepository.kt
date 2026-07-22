package coffee.app.data.repository

import coffee.app.data.database.Origin
import coffee.app.data.database.OriginDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class OriginRepository(
    private val originDao: OriginDao
) {
    suspend fun add(origin: Origin) = withContext(Dispatchers.IO) {
        originDao.insert(origin)
    }

    suspend fun delete(name: String) = withContext(Dispatchers.IO) {
        originDao.deleteByName(name)
    }

    fun getAll(): Flow<List<Origin>> = originDao.observeAll()

    suspend fun existsIgnoreCase(name: String): Boolean = withContext(Dispatchers.IO) {
        originDao.existsIgnoreCase(name)
    }
}
