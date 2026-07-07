package coffee.app.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "brews")
data class BrewEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val grindSize: Float,
    val doseWeightGrams: Float,
    val yieldWeightGrams: Float,
    val extractionTimeSeconds: Int,
    val notes: String = "",
    val originId: Long? = null
)

@Entity(
    tableName = "origins",
    indices = [Index(value = ["country", "region", "roaster"], unique = true)]
)
data class OriginEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val country: String,
    val region: String = "",
    val roaster: String = "",
    val roastDate: Long = 0,
    val roastProfile: String = ""
)