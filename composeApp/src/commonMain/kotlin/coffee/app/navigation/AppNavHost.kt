package coffee.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coffee.app.data.database.BrewEntryDao
import coffee.app.data.database.EntryPhotoDao
import coffee.app.form.BrewEntryFormScreen
import coffee.app.form.BrewEntryFormViewModel
import coffee.app.list.BrewEntryDetailScreen
import coffee.app.list.BrewEntryListScreen
import coffee.app.list.BrewEntryListViewModel
import coffee.app.settings.SettingsScreen
import coffee.app.settings.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    brewEntryDao: BrewEntryDao,
    entryPhotoDao: EntryPhotoDao,
    listViewModel: BrewEntryListViewModel,
    formViewModel: BrewEntryFormViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LIST
    ) {
        composable(route = Routes.LIST) {
            BrewEntryListScreen(
                viewModel = listViewModel,
                entryPhotoDao = entryPhotoDao,
                onNavigateToDetail = { entry ->
                    navController.navigate(Routes.detail(entry.uuid))
                },
                onNavigateToForm = {
                    formViewModel.clearEditState()
                    navController.navigate(Routes.FORM)
                },
                onNavigateToEdit = { entry ->
                    formViewModel.enterEditMode(entry)
                    navController.navigate(Routes.form(entry.uuid))
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("entryUuid") { type = NavType.StringType })
        ) { backStackEntry ->
            val entryUuid = backStackEntry.arguments?.getString("entryUuid") ?: return@composable
            val entries by listViewModel.entries.collectAsState()
            val entry = entries.find { it.uuid == entryUuid }

            if (entry != null) {
                BrewEntryDetailScreen(
                    entry = entry,
                    entryPhotoDao = entryPhotoDao,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Routes.form(entry.uuid)) },
                    onDelete = { listViewModel.deleteEntry(entry.uuid); navController.popBackStack() }
                )
            }
        }

        composable(
            route = Routes.FORM,
            arguments = listOf(navArgument("entryUuid") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val entryUuid = backStackEntry.arguments?.getString("entryUuid")
            val entries by listViewModel.entries.collectAsState()
            val entryToEdit = entryUuid?.let { uuid -> entries.find { it.uuid == uuid } }

            BrewEntryFormScreen(
                viewModel = formViewModel,
                onNavigateBack = { navController.popBackStack() },
                entryToEdit = entryToEdit
            )
        }

        composable(route = Routes.SETTINGS) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
