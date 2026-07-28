package coffee.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryPhotoDao {
    @Query("SELECT * FROM entry_photos WHERE entryUuid = :entryUuid ORDER BY sortOrder ASC")
    fun getPhotosForEntry(entryUuid: String): Flow<List<EntryPhoto>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: EntryPhoto)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<EntryPhoto>)
    
    @Query("DELETE FROM entry_photos WHERE id = :id")
    suspend fun deleteById(id: Int)
    
    @Query("DELETE FROM entry_photos WHERE entryUuid = :entryUuid")
    suspend fun deleteByEntryUuid(entryUuid: String)
    
    @Query("SELECT COALESCE(MAX(sortOrder), 0) + 1 FROM entry_photos WHERE entryUuid = :entryUuid")
    suspend fun nextSortOrder(entryUuid: String): Int

    @Query("SELECT * FROM entry_photos ORDER BY entryUuid, sortOrder ASC")
    suspend fun getAll(): List<EntryPhoto>
}