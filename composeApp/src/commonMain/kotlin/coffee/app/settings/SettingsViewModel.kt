package coffee.app.settings

import coffee.app.data.database.AppPreferencesDao
import coffee.app.data.database.AppPreference
import coffee.app.data.database.BrewEntryDao
import coffee.app.data.database.EntryPhotoDao
import coffee.app.data.repository.BrewEntryRepository
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
}