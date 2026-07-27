package coffee.app.backup

data class Manifest(
    val version: Int,
    val createdDate: String,
    val entryCount: Int,
    val hasPhotos: Boolean
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
    val photoPaths: List<String>
)

data class BackupContents(
    val entries: List<BackupEntry>,
    val manifest: Manifest,
    val photoBytes: Map<String, ByteArray>
)
