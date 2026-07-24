package coffee.app.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coffee.app.core.BitoholicTopBar

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        BitoholicTopBar(
            title = "Settings",
            showSettings = false
        )
        
        Text(
            text = "Theme settings coming soon",
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}