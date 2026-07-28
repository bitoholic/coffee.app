package coffee.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import coffee.app.domain.SortOption
import kotlinx.coroutines.flow.Flow

@Dao
interface BrewEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: BrewEntry)

    @Query("DELETE FROM brew_entries WHERE uuid = :uuid")
    suspend fun deleteByUuid(uuid: String)

    @Query("DELETE FROM brew_entries WHERE uuid IN (:uuids)")
    suspend fun deleteByUuids(uuids: List<String>)

    @Query("SELECT * FROM brew_entries WHERE isFavourite = 1 ORDER BY createdDate DESC")
    fun observeFavourites(): Flow<List<BrewEntry>>

    @Query("UPDATE brew_entries SET isFavourite = :isFavourite WHERE uuid = :uuid")
    suspend fun updateFavourite(uuid: String, isFavourite: Boolean)

    @Query("SELECT * FROM brew_entries ORDER BY createdDate DESC")
    fun observeAllCreatedDateDesc(): Flow<List<BrewEntry>>

    @Query("SELECT * FROM brew_entries ORDER BY beanName ASC")
    fun observeAllBeanNameAZ(): Flow<List<BrewEntry>>

    @Query("SELECT * FROM brew_entries ORDER BY beanOrigin ASC")
    fun observeAllOriginAZ(): Flow<List<BrewEntry>>

    @Query("SELECT * FROM brew_entries ORDER BY beanName DESC")
    fun observeAllBeanNameDesc(): Flow<List<BrewEntry>>

    @Query("SELECT * FROM brew_entries ORDER BY beanOrigin DESC")
    fun observeAllOriginDesc(): Flow<List<BrewEntry>>

    @Query("SELECT * FROM brew_entries ORDER BY createdDate ASC")
    fun observeAllCreatedDate(): Flow<List<BrewEntry>>

    @Query("SELECT * FROM brew_entries ORDER BY lastModifiedDate DESC")
    fun observeAllLastModifiedDate(): Flow<List<BrewEntry>>

    @Query("SELECT * FROM brew_entries ORDER BY lastModifiedDate ASC")
    fun observeAllLastModifiedDateAsc(): Flow<List<BrewEntry>>

    @Query("SELECT * FROM brew_entries WHERE uuid = :uuid")
    suspend fun getById(uuid: String): BrewEntry?

    @Query("SELECT * FROM brew_entries ORDER BY createdDate DESC")
    suspend fun getAll(): List<BrewEntry>
}
