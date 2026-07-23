package coffee.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.saveable.rememberSaveable
import coffee.app.data.database.BrewEntry
import coffee.app.data.database.CoffeeDatabase
import coffee.app.data.repository.BrewEntryRepository
import coffee.app.data.repository.OriginRepository
import coffee.app.list.BrewEntryListScreen
import coffee.app.list.BrewEntryListViewModel
import coffee.app.list.BrewEntryDetailScreen
import coffee.app.form.BrewEntryFormScreen
import coffee.app.form.BrewEntryFormViewModel
import androidx.room.Room

// Sealed class to represent different screens
sealed class Screen {
    object List : Screen()
    data class Detail(val entry: BrewEntry) : Screen()
    data class Form(val entry: BrewEntry?) : Screen()
}

@Composable
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Database and repository setup
            val context = androidx.compose.ui.platform.LocalContext.current
            val db = remember { 
                CoffeeDatabase.getInstance(
                    Room.databaseBuilder(context, CoffeeDatabase::class.java, "coffee-app.db")
                )
            }
            val brewRepo = remember { BrewEntryRepository(db.brewEntryDao()) }
            val originRepo = remember { OriginRepository(db.originDao()) }
            
            // ViewModel setup
            val listViewModel = remember { BrewEntryListViewModel(brewRepo) }
            val formViewModel = remember { BrewEntryFormViewModel(brewRepo, originRepo) }
            
            // State for current screen
            val currentScreen = rememberSaveable { mutableStateOf<Screen>(Screen.List) }
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (val screen = currentScreen.value) {
                    Screen.List -> {
                        BrewEntryListScreen(
                            viewModel = listViewModel,
                            onNavigateToDetail = { entry ->
                                currentScreen.value = Screen.Detail(entry)
                            },
                            onNavigateToForm = {
                                currentScreen.value = Screen.Form(null)
                            },
                            onNavigateToEdit = { entry ->
                                currentScreen.value = Screen.Form(entry)
                            }
                        )
                    }
                    
                    is Screen.Detail -> {
                        BrewEntryDetailScreen(
                            entry = screen.entry,
                            onBack = { currentScreen.value = Screen.List },
                            onEdit = {
                                currentScreen.value = Screen.Form(screen.entry)
                            },
                            onDelete = {
                                // Delete and go back to list
                                currentScreen.value = Screen.List
                            }
                        )
                    }
                    
                    is Screen.Form -> {
                        BrewEntryFormScreen(
                            viewModel = formViewModel,
                            onNavigateBack = { currentScreen.value = Screen.List },
                            entryToEdit = screen.entry
                        )
                    }
                }
            }
        }
    }
}