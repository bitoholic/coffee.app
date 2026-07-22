package coffee.app.origin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coffee.app.data.database.Origin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OriginPickerSheet(
    viewModel: OriginPickerViewModel,
    onOriginSelected: (Origin) -> Unit,
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val origins by viewModel.origins.collectAsState()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Origin") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                // Predefined Origins section
                Text(
                    text = "Predefined Origins",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                if (origins.filter { !it.isCustom }.isEmpty()) {
                    Text(
                        text = "No predefined origins available",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    LazyColumn (
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(origins.filter { !it.isCustom }) { origin ->
                            OriginItem(origin, onOriginSelected)
                        }
                    }
                }
                
                // Divider
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Custom Origins section
                Text(
                    text = "Custom Origins",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (origins.filter { it.isCustom }.isEmpty()) {
                    Text(
                        text = "No custom origins yet",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    LazyColumn (
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(origins.filter { it.isCustom }) { origin ->
                            OriginItem(origin, onOriginSelected)
                        }
                    }
                }
                
                // Custom origin creation 
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Add Custom Origin",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                var customOriginName by remember { mutableStateOf("") }
                
                OutlinedTextField(
                    value = customOriginName,
                    onValueChange = { customOriginName = it },
                    label = { Text("Origin Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                
                state.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { 
                        viewModel.createCustomOrigin(customOriginName)
                        customOriginName = ""
                    },
                    enabled = !state.isCreating && customOriginName.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isCreating) {
                        Text("Creating...")
                    } else {
                        Text("Add Custom Origin")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun OriginItem(
    origin: Origin,
    onOriginSelected: (Origin) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onOriginSelected(origin) }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = origin.name,
                style = MaterialTheme.typography.bodyLarge
            )
            if (origin.isCustom) {
                Text(
                    text = "(custom)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}