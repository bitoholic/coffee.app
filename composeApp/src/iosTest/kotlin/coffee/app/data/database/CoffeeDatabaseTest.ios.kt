package coffee.app.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual fun getTestDatabaseBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
    return Room.inMemoryDatabaseBuilder<CoffeeDatabase>()
        .setDriver(BundledSQLiteDriver())
}