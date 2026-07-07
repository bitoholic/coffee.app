package coffee.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BrewEntryDao {

    @Query("SELECT * FROM brews ORDER BY timestamp DESC")
    fun observeAllBrews(): Flow<List<BrewEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrew(entry: BrewEntryEntity): Long

    @Delete
    suspend fun deleteBrew(entry: BrewEntryEntity)
}

@Dao
interface OriginDao {

    @Query("SELECT * FROM origins ORDER BY country ASC, region ASC")
    fun observeAllOrigins(): Flow<List<OriginEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrigin(origin: OriginEntity): Long

    @Delete
    suspend fun deleteOrigin(origin: OriginEntity)

    @Query("SELECT * FROM origins WHERE id = :originId")
    suspend fun getOriginById(originId: Long): OriginEntity?
}