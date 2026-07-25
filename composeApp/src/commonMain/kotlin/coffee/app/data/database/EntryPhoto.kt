package coffee.app.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "entry_photos",
    foreignKeys = [ForeignKey(
        entity = BrewEntry::class,
        parentColumns = ["uuid"],
        childColumns = ["entryUuid"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("entryUuid")]
)
data class EntryPhoto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val entryUuid: String,
    val photoPath: String,
    val sortOrder: Int
)