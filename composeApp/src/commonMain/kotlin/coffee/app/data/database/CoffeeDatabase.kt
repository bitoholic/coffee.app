package coffee.app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BrewEntryEntity::class, OriginEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun brewEntryDao(): BrewEntryDao
    abstract fun originDao(): OriginDao
}