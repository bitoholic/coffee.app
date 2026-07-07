package coffee.app.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider

actual fun getTestDatabaseBuilder(): RoomDatabase.Builder<CoffeeDatabase> {
    val context = ApplicationProvider.getApplicationContext<Context>()
    
    // For unit testing with Robolectric, use Room's in-memory builder
    // This internally creates an SQLite implementation compatible with Robolectric
    return Room.inMemoryDatabaseBuilder<CoffeeDatabase>(
        context = context,
        klass = CoffeeDatabase::class.java
    )
}
