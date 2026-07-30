package coffee.app.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coffee.app.core.BitoholicTopBar
import coffee.app.core.BrandRed
import coffee.app.core.DateFormatUtil
import coffee.app.core.PhotoManager
import coffee.app.data.database.BrewEntry
import coffee.app.data.database.EntryPhoto
import coffee.app.data.database.EntryPhotoDao
import coffee.app.domain.SortOption
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewEntryListScreen(
    viewModel: BrewEntryListViewModel,
    entryPhotoDao: EntryPhotoDao,
    onNavigateToDetail: (BrewEntry) -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToEdit: (BrewEntry) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var isSortExpanded by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val entries by viewModel.entries.collectAsState()
    val currentSort by viewModel.currentSortOption.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val isSelectionMode = selectedIds.isNotEmpty()
    val isStarredFilterActive by viewModel.isStarredFilterActive.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // System back exits selection mode
    BackHandler(enabled = isSelectionMode) {
        viewModel.clearSelection()
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete entries") },
            text = { Text("Delete ${selectedIds.size} entries?") },
            confirmButton = {
                Button(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteSelected()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Snackbar
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { /* snackbar shown via state reset */ }
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                BitoholicTopBar(
                    title = "${selectedIds.size} selected",
                    showBack = false,
                    onBackClick = { viewModel.clearSelection() },
                    showSettings = false,
                    actions = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = "Exit selection")
                        }
                    }
                )
            } else {
                BitoholicTopBar(
                    title = "Brews",
                    showSettings = true,
                    onSettingsClick = onNavigateToSettings
                )
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(
                    onClick = onNavigateToForm,
                    containerColor = BrandRed,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Brew Entry")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Sort + Search bar — always reserves space to prevent list shift
                Box(modifier = Modifier.fillMaxWidth().height(if (isSelectionMode) 0.dp else 48.dp)) {
                    if (!isSelectionMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                TextButton(
                                    onClick = {
                                        focusManager.clearFocus()
                                        isSortExpanded = true
                                    }
                                ) {
                                    Text(
                                        text = currentSort.displayName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                DropdownMenu(
                                    expanded = isSortExpanded,
                                    onDismissRequest = { isSortExpanded = false }
                                ) {
                                    // Starred toggle as first item
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("Starred")
                                                Spacer(Modifier.width(8.dp))
                                                Icon(
                                                    Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (isStarredFilterActive) BrandRed else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.toggleStarredFilter()
                                            isSortExpanded = false
                                        },
                                        modifier = if (isStarredFilterActive) {
                                            Modifier.background(BrandRed.copy(alpha = 0.1f))
                                        } else {
                                            Modifier
                                        }
                                    )
                                    // Divider
                                    SortOption.values().forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option.displayName) },
                                            onClick = {
                                                viewModel.setSortOption(option)
                                                isSortExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            if (isSearchExpanded) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f, fill = false)
                                            .widthIn(max = 140.dp)
                                            .height(32.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .border(1.dp, BrandRed, RoundedCornerShape(16.dp))
                                            .padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        BasicTextField(
                                            value = searchQuery,
                                            onValueChange = { viewModel.setSearchQuery(it) },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            isSearchExpanded = false
                                            viewModel.setSearchQuery("")
                                        }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Close search")
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = {
                                        focusManager.clearFocus()
                                        isSearchExpanded = true
                                    }
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                            }
                        }
                    }
                }

                // No results message
                if (searchQuery.isNotEmpty() && entries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No entries found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Brew entries list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(entries) { entry ->
                            val isSelected = selectedIds.contains(entry.uuid)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            if (isSelectionMode) {
                                                viewModel.toggleSelection(entry.uuid)
                                            } else {
                                                onNavigateToDetail(entry)
                                            }
                                        },
                                        onLongClick = {
                                            if (!isSelectionMode) {
                                                viewModel.toggleSelection(entry.uuid)
                                            }
                                        }
                                    )
                                    .then(
                                        if (isSelected) {
                                            Modifier.background(
                                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Checkbox in selection mode
                                if (isSelectionMode) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { viewModel.toggleSelection(entry.uuid) },
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                // Photo thumbnail with favourite badge (star outside clip)
                                Box(modifier = Modifier.size(48.dp)) {
                                    // The photo circle (clipped)
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                    ) {
                                        var firstPhotoPath by remember { mutableStateOf<String?>(null) }
                                        LaunchedEffect(entry.uuid) {
                                            entryPhotoDao.getPhotosForEntry(entry.uuid).first().let { entryPhotos ->
                                                firstPhotoPath = entryPhotos.firstOrNull()?.photoPath
                                            }
                                        }
                                        // Photo content (same as before)
                                        firstPhotoPath?.let { path ->
                                            val photoManager = remember { PhotoManager(context) }
                                            val bitmap = photoManager.loadPhoto(path)
                                            bitmap?.let {
                                                Image(
                                                    bitmap = it.asImageBitmap(),
                                                    contentDescription = "Entry photo thumbnail",
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } ?: run { placeholderIcon() }
                                        } ?: run { placeholderIcon() }
                                    }

                                    // Star badge — positioned outside the circle, not clipped
                                    if (entry.isFavourite == 1) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = "Favourite",
                                            tint = BrandRed,
                                            modifier = Modifier
                                                .size(18.dp)
                                                .align(Alignment.TopEnd)
                                                .offset(x = 4.dp, y = (-4).dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = entry.beanName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${entry.beanOrigin ?: ""} / ${entry.roastType ?: ""}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = DateFormatUtil.toShortDate(entry.createdDate),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${entry.grinderSetting} / ${entry.portionWeight}g",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom bar in selection mode
            if (isSelectionMode) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedIds.size} selected",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = { showDeleteDialog = true },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun placeholderIcon() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "No photo",
            tint = BrandRed,
            modifier = Modifier.size(24.dp)
        )
    }
}
