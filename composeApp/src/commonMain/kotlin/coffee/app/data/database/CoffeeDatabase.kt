package coffee.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [], version = 1, exportSchema = false)
abstract class CoffeeDatabase : RoomDatabase() {
    abstract fun brewEntryDao(): Any // placeholder — defined in Feature #2
    abstract fun originDao(): Any    // placeholder — defined in Feature #2

    companion object {
        @Volatile
        private var INSTANCE: CoffeeDatabase? = null

        fun getInstance(context: Context): CoffeeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoffeeDatabase::class.java,
                    "coffee_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}