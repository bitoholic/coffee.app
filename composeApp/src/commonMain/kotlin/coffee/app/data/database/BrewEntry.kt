package coffee.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "brew_entries")
data class BrewEntry(
    @PrimaryKey val uuid: String = UUID.randomUUID().toString(),
    val beanName: String,
    val beanOrigin: String? = null,
    val roastType: String,
    val grinderSetting: Int,
    val portionWeight: Double,
    val description: String? = null,
    val createdDate: Long,
    val lastModifiedDate: Long
)
