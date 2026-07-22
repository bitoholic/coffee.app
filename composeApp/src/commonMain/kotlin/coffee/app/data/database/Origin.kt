package coffee.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "origins")
data class Origin(
    @PrimaryKey val name: String,
    val isCustom: Boolean = false
)
