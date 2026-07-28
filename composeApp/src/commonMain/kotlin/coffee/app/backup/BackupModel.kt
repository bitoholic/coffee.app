package coffee.app.backup

data class Manifest(
    val version: Int,
    val createdDate: String,
    val entryCount: Int,
    val hasPhotos: Boolean,
    val schemaVersion: Int = 4
)

data class BackupEntry(
    val uuid: String,
    val beanName: String,
    val beanOrigin: String?,
    val roastType: String,
    val grinderSetting: Int,
    val portionWeight: Double,
    val description: String?,
    val createdDate: Long,
    val lastModifiedDate: Long,
    val isFavourite: Int = 0,
    val photoPaths: List<String>
)

data class BackupContents(
    val entries: List<BackupEntry>,
    val manifest: Manifest,
    val photoBytes: Map<String, ByteArray>
)
