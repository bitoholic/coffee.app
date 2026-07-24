package coffee.app.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coffee.app.core.BitoholicTopBar

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        BitoholicTopBar(
            title = "Settings",
            showSettings = false,
            onNavigateBack = onNavigateBack
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            val themeMode by viewModel.themeMode.collectAsState()
            val themeModes = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)
            
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                themeModes.forEach { mode ->
                    FilterChip(
                        selected = themeMode == mode,
                        onClick = { viewModel.setThemeMode(mode) },
                        label = { Text(getThemeModeLabel(mode)) }
                    )
                }
            }
        }
    }
}

private fun getThemeModeLabel(themeMode: ThemeMode): String {
    return when (themeMode) {
        ThemeMode.SYSTEM -> "System"
        ThemeMode.LIGHT -> "Light"
        ThemeMode.DARK -> "Dark"
    }
}