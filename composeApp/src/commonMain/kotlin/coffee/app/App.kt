package coffee.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import coffee.app.core.DarkColorScheme
import coffee.app.core.LightColorScheme
import coffee.app.data.database.CoffeeDatabase
import coffee.app.data.repository.BrewEntryRepository
import coffee.app.data.repository.OriginRepository
import coffee.app.navigation.AppNavHost
import coffee.app.list.BrewEntryListViewModel
import coffee.app.form.BrewEntryFormViewModel
import coffee.app.settings.SettingsViewModel
import androidx.room.Room
import coffee.app.settings.ThemeMode

@Composable
fun App() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { 
        CoffeeDatabase.getInstance(
            Room.databaseBuilder(
                context.applicationContext,
                CoffeeDatabase::class.java,
                "coffee-app.db"
            )
        )
    }
    
    // Create SettingsViewModel in App.kt
    val settingsViewModel = remember { SettingsViewModel(db.appPreferencesDao(), db.brewEntryDao(), db.entryPhotoDao()) }
    val themeMode by settingsViewModel.themeMode.collectAsState()

    // Determine if dark mode should be applied
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val brewRepo = remember { BrewEntryRepository(db.brewEntryDao()) }
            val originRepo = remember { OriginRepository(db.originDao()) }
            val entryPhotoDao = remember { db.entryPhotoDao() }
            
            val listViewModel = remember { BrewEntryListViewModel(brewRepo) }
            val formViewModel = remember { BrewEntryFormViewModel(brewRepo, originRepo, entryPhotoDao) }
            
            AppNavHost(
                brewEntryDao = db.brewEntryDao(),
                entryPhotoDao = entryPhotoDao,
                listViewModel = listViewModel,
                formViewModel = formViewModel,
                settingsViewModel = settingsViewModel
            )
        }
    }
}