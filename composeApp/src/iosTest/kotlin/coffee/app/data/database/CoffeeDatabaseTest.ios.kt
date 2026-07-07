package coffee.app.data.database

import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getTestDatabaseBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
    return Room.inMemoryDatabaseBuilder<CoffeeDatabase>()
}