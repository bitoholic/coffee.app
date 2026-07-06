package coffee.app.data.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BrewEntryDao {
    @Query("SELECT * FROM BrewEntryEntity")
    suspend fun getAllEntries(): List<BrewEntryEntity>
}

@Dao
interface OriginDao {
    @Query("SELECT * FROM OriginEntity")
    suspend fun getAllOrigins(): List<OriginEntity>
}