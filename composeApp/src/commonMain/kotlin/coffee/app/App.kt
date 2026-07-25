package coffee.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import coffee.app.core.DarkColorScheme
import coffee.app.core.LightColorScheme
import coffee.app.data.database.BrewEntry
import coffee.app.data.database.CoffeeDatabase
import coffee.app.data.repository.BrewEntryRepository
import coffee.app.data.repository.OriginRepository
import coffee.app.list.BrewEntryListScreen
import coffee.app.list.BrewEntryListViewModel
import coffee.app.list.BrewEntryDetailScreen
import coffee.app.form.BrewEntryFormScreen
import coffee.app.form.BrewEntryFormViewModel
import coffee.app.settings.SettingsScreen
import coffee.app.settings.SettingsViewModel
import androidx.room.Room
import coffee.app.settings.ThemeMode

sealed class Screen {
    object List : Screen()
    data class Detail(val entry: BrewEntry) : Screen()
    data class Form(val entry: BrewEntry?) : Screen()
    object Settings : Screen()
}

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
    val settingsViewModel = remember { SettingsViewModel(db.appPreferencesDao()) }
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
            
            val listViewModel = remember { BrewEntryListViewModel(brewRepo) }
            val formViewModel = remember { BrewEntryFormViewModel(brewRepo, originRepo) }
            
            var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }
            
            when (currentScreen) {
                Screen.List -> {
                    BrewEntryListScreen(
                        viewModel = listViewModel,
                        onNavigateToDetail = { entry ->
                            currentScreen = Screen.Detail(entry)
                        },
                        onNavigateToForm = {
                            formViewModel.clearEditState()
                            currentScreen = Screen.Form(null)
                        },
                        onNavigateToEdit = { entry ->
                            currentScreen = Screen.Form(entry)
                        },
                        onNavigateToSettings = {
                            currentScreen = Screen.Settings
                        }
                    )
                }
                
                is Screen.Detail -> {
                    val screen = currentScreen as Screen.Detail
                    BrewEntryDetailScreen(
                        entry = screen.entry,
                        onBack = { currentScreen = Screen.List },
                        onEdit = {
                            currentScreen = Screen.Form(screen.entry)
                        },
                        onDelete = {
                            listViewModel.deleteEntry(screen.entry.uuid)
                            currentScreen = Screen.List
                        }
                    )
                }
                
                is Screen.Form -> {
                    val screen = currentScreen as Screen.Form
                    BrewEntryFormScreen(
                        viewModel = formViewModel,
                        onNavigateBack = {
                            formViewModel.clearEditState()
                            currentScreen = Screen.List
                        },
                        entryToEdit = screen.entry
                    )
                }
                
                Screen.Settings -> {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onNavigateBack = { currentScreen = Screen.List }
                    )
                }
            }
        }
    }
}
