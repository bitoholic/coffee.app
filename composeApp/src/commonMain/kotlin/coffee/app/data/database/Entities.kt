package coffee.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BrewEntryEntity")
data class BrewEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long
)

@Entity(tableName = "OriginEntity")
data class OriginEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val country: String
)