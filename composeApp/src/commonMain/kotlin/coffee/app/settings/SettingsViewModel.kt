package coffee.app.settings

import coffee.app.backup.BackupContents
import coffee.app.backup.BackupEngine
import coffee.app.backup.BackupException
import coffee.app.data.database.AppPreferencesDao
import coffee.app.data.database.AppPreference
import coffee.app.data.database.BrewEntry
import coffee.app.data.database.BrewEntryDao
import coffee.app.data.database.EntryPhoto
import coffee.app.data.database.EntryPhotoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class SettingsViewModel(
    private val prefsDao: AppPreferencesDao,
    private val brewEntryDao: BrewEntryDao,
    private val entryPhotoDao: EntryPhotoDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        scope.launch {
            val saved = prefsDao.getValue("theme_mode")
            _themeMode.value = when (saved) {
                "light" -> ThemeMode.LIGHT
                "dark" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        scope.launch {
            prefsDao.setValue(
                AppPreference(
                    key = "theme_mode",
                    value = when (mode) {
                        ThemeMode.SYSTEM -> "system"
                        ThemeMode.LIGHT -> "light"
                        ThemeMode.DARK -> "dark"
                    }
                )
            )
        }
    }

    suspend fun createBackup(includePhotos: Boolean): ByteArray {
        val entries = brewEntryDao.getAll()
        val entryPhotos = if (includePhotos) entryPhotoDao.getAll() else emptyList()
        return BackupEngine.createBackup(entries, entryPhotos, includePhotos)
    }

    suspend fun parseBackup(zipBytes: ByteArray): BackupContents {
        return BackupEngine.parseBackup(zipBytes)
    }

    suspend fun restoreBackup(
        contents: BackupContents,
        mode: RestoreMode,
        photoDir: String
    ) {
        if (mode == RestoreMode.OVERWRITE) {
            // Clear existing data
            val existing = brewEntryDao.getAll()
            for (entry in existing) {
                brewEntryDao.deleteByUuid(entry.uuid)
            }
        }

        // Insert entries
        for (backupEntry in contents.entries) {
            val entry = BrewEntry(
                uuid = backupEntry.uuid,
                beanName = backupEntry.beanName,
                beanOrigin = backupEntry.beanOrigin,
                roastType = backupEntry.roastType,
                grinderSetting = backupEntry.grinderSetting,
                portionWeight = backupEntry.portionWeight,
                description = backupEntry.description,
                createdDate = backupEntry.createdDate,
                lastModifiedDate = backupEntry.lastModifiedDate
            )
            brewEntryDao.upsert(entry)

            // Insert photos
            for ((index, path) in backupEntry.photoPaths.withIndex()) {
                val photoFile = java.io.File(path)
                val actualPath = if (photoFile.exists()) {
                    // Photo already exists at this path
                    path
                } else {
                    // Try to extract from backup ZIP
                    val fileName = java.io.File(path).name
                    val savedBytes = contents.photoBytes[fileName]
                    if (savedBytes != null) {
                        val destFile = java.io.File(photoDir, fileName)
                        destFile.parentFile?.mkdirs()
                        destFile.writeBytes(savedBytes)
                        destFile.absolutePath
                    } else {
                        path // keep original path even if missing
                    }
                }

                entryPhotoDao.insert(
                    EntryPhoto(
                        entryUuid = backupEntry.uuid,
                        photoPath = actualPath,
                        sortOrder = index
                    )
                )
            }
        }
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    fun setMessage(msg: String?) {
        _message.value = msg
    }

    fun clearMessage() {
        _message.value = null
    }
}

enum class RestoreMode { OVERWRITE, MERGE }
