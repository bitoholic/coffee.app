package coffee.app.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coffee.app.data.database.BrewEntry
import coffee.app.domain.SortOption
import coffee.app.core.DateFormatUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewEntryListScreen(
    viewModel: BrewEntryListViewModel,
    onNavigateToDetail: (BrewEntry) -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToEdit: (BrewEntry) -> Unit
) {
    var isSortExpanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Brew Entries") },
                actions = {
                    IconButton(onClick = { isSortExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Sort")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToForm) {
                Icon(Icons.Default.Add, contentDescription = "Add Brew Entry")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sort dropdown menu
            DropdownMenu(
                expanded = isSortExpanded,
                onDismissRequest = { isSortExpanded = false }
            ) {
                SortOption.values().forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.name.replaceFirstChar { it.uppercaseChar() }) },
                        onClick = {
                            viewModel.setSortOption(option)
                            isSortExpanded = false
                        }
                    )
                }
            }
            
            // Brew entries list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.entries.value) { entry ->
                    BrewEntryRow(
                        entry = entry,
                        onClick = { onNavigateToDetail(entry) },
                        onEditClick = { onNavigateToEdit(entry) }
                    )
                }
            }
        }
    }
}

@Composable
fun BrewEntryRow(
    entry: BrewEntry,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        headlineContent = {
            Text(
                text = entry.beanName,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = {
            Column {
                Text("${entry.beanOrigin ?: "Unknown"} · ${entry.roastType ?: "Unknown"}")
                Text("${entry.grinderSetting} · ${entry.portionWeight}g")
                Text("Created: ${DateFormatUtil.formatDate(entry.createdDate)}")
            }
        },
        trailingContent = {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
        }
    )
}