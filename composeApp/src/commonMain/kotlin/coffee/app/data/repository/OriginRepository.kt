package coffee.app.data.repository

import coffee.app.data.database.Origin
import coffee.app.data.database.OriginDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

open class OriginRepository(
    protected val originDao: OriginDao
) {
    open suspend fun add(origin: Origin) = withContext(Dispatchers.IO) {
        originDao.insert(origin)
    }

    open suspend fun delete(name: String) = withContext(Dispatchers.IO) {
        originDao.deleteByName(name)
    }

    open fun getAll(): Flow<List<Origin>> = originDao.observeAll()

    open suspend fun existsIgnoreCase(name: String): Boolean = withContext(Dispatchers.IO) {
        originDao.existsIgnoreCase(name)
    }
}
