package coffee.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking

@Database(
    entities = [BrewEntry::class, Origin::class, AppPreference::class],
    version = 2,
    exportSchema = false
)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun brewEntryDao(): BrewEntryDao
    abstract fun originDao(): OriginDao
    abstract fun appPreferencesDao(): AppPreferencesDao

    companion object {
        private var instance: CoffeeDatabase? = null

        fun getInstance(builder: androidx.room.RoomDatabase.Builder<CoffeeDatabase>): CoffeeDatabase {
            return instance ?: synchronized(this) {
                instance ?: builder
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .build()
                    .also { db ->
                        instance = db
                        seedOrigins(db)
                    }
            }
        }

        private fun seedOrigins(db: CoffeeDatabase) {
            runBlocking(Dispatchers.IO) {
                val dao = db.originDao()
                val predefinedOrigins = listOf(
                    "Brazil", "Colombia", "Ethiopia", "Kenya", "Guatemala",
                    "Costa Rica", "Honduras", "Peru", "El Salvador", "Panama",
                    "Indonesia", "India", "Vietnam", "Yemen"
                )
                for (name in predefinedOrigins) {
                    if (!dao.existsIgnoreCase(name)) {
                        dao.insert(Origin(name = name, isCustom = false))
                    }
                }
            }
        }
    }
}
