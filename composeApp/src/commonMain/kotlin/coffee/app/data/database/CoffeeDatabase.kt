package coffee.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import androidx.room.DatabaseConfiguration
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Simplified representation of what a CoffeeDatabase file would look like
// with the new EntryPhoto entity and migration registered

@Database(
    entities = [
        // Existing entities would be listed here
        // BrewEntry::class,
        // ... other entities
        EntryPhoto::class // New entity registered
    ],
    version = 4 // Updated version
)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun entryPhotoDao(): EntryPhotoDao
    
    companion object {
        // Migration from v3 to v4 would be referenced here
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
                // Migrate existing single photos
                db.execSQL("""
                    INSERT INTO entry_photos (entryUuid, photoPath, sortOrder)
                    SELECT uuid, photoPath, 0 FROM brew_entries WHERE photoPath IS NOT NULL
                """)
                // Drop old column
                db.execSQL("CREATE TABLE brew_entries_new AS SELECT beanName, beanOrigin, roastType, grinderSetting, portionWeight, description, createdDate, lastModifiedDate, uuid FROM brew_entries")
                db.execSQL("DROP TABLE brew_entries")
                db.execSQL("ALTER TABLE brew_entries_new RENAME TO brew_entries")
            }
        }
    }
}