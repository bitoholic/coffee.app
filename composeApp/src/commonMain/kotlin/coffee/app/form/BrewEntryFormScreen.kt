package coffee.app.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coffee.app.domain.RoastType

/**
 * Full-screen brew entry form with all fields per spec.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewEntryFormScreen(
    viewModel: BrewEntryFormViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "New Brew Entry",
            style = MaterialTheme.typography.headlineSmall
        )

        // Bean Name
        OutlinedTextField(
            value = state.beanName,
            onValueChange = viewModel::onBeanNameChanged,
            label = { Text("Bean Name *") },
            isError = state.validationErrors.containsKey("beanName"),
            supportingText = state.validationErrors["beanName"]?.let {
                { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Bean Origin
        OriginDropdown(
            origins = state.origins.map { it.name },
            selectedOrigin = state.beanOrigin,
            onOriginSelected = viewModel::onBeanOriginChanged,
            modifier = Modifier.fillMaxWidth()
        )

        // Roast Type selector
        Text("Roast Type *", style = MaterialTheme.typography.labelLarge)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoastType.entries.forEach { type ->
                FilterChip(
                    selected = state.roastType == type,
                    onClick = { viewModel.onRoastTypeChanged(type) },
                    label = { Text(type.name) }
                )
            }
        }
        state.validationErrors["roastType"]?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Grinder Setting
        OutlinedTextField(
            value = state.grinderSetting,
            onValueChange = viewModel::onGrinderSettingChanged,
            label = { Text("Grinder Setting (1–48) *") },
            isError = state.validationErrors.containsKey("grinderSetting"),
            supportingText = state.validationErrors["grinderSetting"]?.let {
                { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Portion Weight
        OutlinedTextField(
            value = state.portionWeight,
            onValueChange = viewModel::onPortionWeightChanged,
            label = { Text("Portion Weight (g) *") },
            isError = state.validationErrors.containsKey("portionWeight"),
            supportingText = state.validationErrors["portionWeight"]?.let {
                { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Description
        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::onDescriptionChanged,
            label = { Text("Description (optional, max 500 chars)") },
            isError = state.validationErrors.containsKey("description"),
            supportingText = state.validationErrors["description"]?.let {
                { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Save button
        Button(
            onClick = { viewModel.save() },
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Save Brew Entry")
        }

        // Success feedback
        if (state.saveSuccess) {
            Text(
                text = "Brew entry saved successfully!",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OriginDropdown(
    origins: List<String>,
    selectedOrigin: String,
    onOriginSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOrigin,
            onValueChange = {
                onOriginSelected(it)
                expanded = true
            },
            label = { Text("Bean Origin") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth(),
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            origins.forEach { origin ->
                DropdownMenuItem(
                    text = { Text(origin) },
                    onClick = {
                        onOriginSelected(origin)
                        expanded = false
                    }
                )
            }
        }
    }
}
