package coffee.app.settings

import coffee.app.data.database.AppPreferencesDao

class SettingsViewModel(private val prefsDao: AppPreferencesDao) {
    // Will expose current theme mode, save/load from DAO
    // For now: just a shell that compiles
}