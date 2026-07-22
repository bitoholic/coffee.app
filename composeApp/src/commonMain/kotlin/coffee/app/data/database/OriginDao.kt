package coffee.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OriginDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(origin: Origin)

    @Query("DELETE FROM origins WHERE name = :name")
    suspend fun deleteByName(name: String)

    @Query("SELECT * FROM origins ORDER BY name ASC")
    fun observeAll(): Flow<List<Origin>>

    @Query("SELECT EXISTS(SELECT 1 FROM origins WHERE LOWER(name) = LOWER(:name))")
    suspend fun existsIgnoreCase(name: String): Boolean
}
