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
import androidx.room.Room

sealed class Screen {
    object List : Screen()
    data class Detail(val entry: BrewEntry) : Screen()
    data class Form(val entry: BrewEntry?) : Screen()
    object Settings : Screen()
}

@Composable
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
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
                        onNavigateBack = { currentScreen = Screen.List }
                    )
                }
            }
        }
    }
}
