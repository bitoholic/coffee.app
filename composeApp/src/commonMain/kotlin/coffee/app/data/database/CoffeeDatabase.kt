package coffee.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking

@Database(
    entities = [BrewEntry::class, Origin::class, AppPreference::class, EntryPhoto::class],
    version = 5,
    exportSchema = false
)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun brewEntryDao(): BrewEntryDao
    abstract fun originDao(): OriginDao
    abstract fun appPreferencesDao(): AppPreferencesDao
    abstract fun entryPhotoDao(): EntryPhotoDao

    companion object {
        private var instance: CoffeeDatabase? = null

        fun getInstance(builder: androidx.room.RoomDatabase.Builder<CoffeeDatabase>): CoffeeDatabase {
            return instance ?: synchronized(this) {
                instance ?: builder
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `entry_photos` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `entryUuid` TEXT NOT NULL,
                        `photoPath` TEXT NOT NULL,
                        `sortOrder` INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (`entryUuid`) REFERENCES `brew_entries`(`uuid`) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_entry_photos_entryUuid` ON `entry_photos` (`entryUuid`)")
                db.execSQL("""
                    INSERT INTO entry_photos (entryUuid, photoPath, sortOrder)
                    SELECT uuid, photoPath, 0 FROM brew_entries WHERE photoPath IS NOT NULL
                """)
                db.execSQL("CREATE TABLE brew_entries_new AS SELECT beanName, beanOrigin, roastType, grinderSetting, portionWeight, description, createdDate, lastModifiedDate, uuid FROM brew_entries")
                db.execSQL("DROP TABLE brew_entries")
                db.execSQL("ALTER TABLE brew_entries_new RENAME TO brew_entries")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE brew_entries ADD COLUMN isFavourite INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
