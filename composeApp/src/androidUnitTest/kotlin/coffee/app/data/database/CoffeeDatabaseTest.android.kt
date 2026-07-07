package coffee.app.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import android.content.Context

actual fun getTestDatabaseBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
    val context = ApplicationProvider.getApplicationContext<Context>()
    return Room.inMemoryDatabaseBuilder<CoffeeDatabase>(
        context = context,
        klass = CoffeeDatabase::class.java
    )
}