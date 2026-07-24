package coffee.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppPreferencesDao {
    @Query("SELECT value FROM app_preferences WHERE `key` = :key")
    suspend fun getValue(key: String): String?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setValue(preference: AppPreference)
    
    @Query("DELETE FROM app_preferences WHERE `key` = :key")
    suspend fun delete(key: String)
}