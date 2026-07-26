package coffee.app.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.flow.first
import coffee.app.core.BitoholicTopBar
import coffee.app.core.BrandRed
import coffee.app.core.DateFormatUtil
import coffee.app.core.PhotoManager
import coffee.app.data.database.BrewEntry
import coffee.app.data.database.EntryPhoto
import coffee.app.data.database.EntryPhotoDao
import coffee.app.domain.SortOption
import androidx.compose.runtime.LaunchedEffect

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
    val entries by viewModel.entries.collectAsState()
    val currentSort by viewModel.currentSortOption.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            BitoholicTopBar(
                title = "Brews",
                showSettings = true,
                onSettingsClick = onNavigateToSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToForm,
                containerColor = BrandRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Brew Entry")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sort + Search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sort control + its dropdown menu live in their own Box, so the
                // popup is anchored to the button itself rather than to the full
                // width of the row. This keeps the button's own size independent
                // of whatever else is in the row (e.g. the search field).
                Box {
                    TextButton(
                        onClick = {
                            // Dismiss the search field's focus/keyboard first. Opening
                            // the menu while a field is still focused can trigger a
                            // keyboard-dismiss + window-resize at the same moment the
                            // popup computes its position, which is what was throwing
                            // it into the corner instead of under the button.
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
                    // Search mode: this Row claims exactly the space left over
                    // after the sort control above, so the field is always
                    // confined between the sort control and the right edge and
                    // can never grow over/under the sort control.
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Compact rounded search box built from BasicTextField.
                        // Material3's TextField bakes in a much larger minimum
                        // height/padding than fits here; forcing it down to
                        // 32dp clips the actual text outside the visible box.
                        // BasicTextField has no such built-in padding, so it
                        // renders correctly at this size with an explicit,
                        // guaranteed-visible text color.
                        Box(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .widthIn(max = 140.dp)
                                .height(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
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
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close search"
                            )
                        }
                    }
                } else {
                    // Normal mode: push the search icon to the far right,
                    // matching the previous SpaceBetween layout.
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { isSearchExpanded = true }
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            }

            // No results message when search returns empty
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
                        // Compact two-line list row with photo thumbnail
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDetail(entry) }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            // Left: photo thumbnail or placeholder (48dp circle)
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            ) {
                                // Load first photo thumbnail using LaunchedEffect and a mutableStateOf
                                var firstPhotoPath by remember { mutableStateOf<String?>(null) }
                                LaunchedEffect(entry.uuid) {
                                    entryPhotoDao.getPhotosForEntry(entry.uuid).first().let { entryPhotos ->
                                        firstPhotoPath = entryPhotos.firstOrNull()?.photoPath
                                    }
                                }

                                // Load and display the photo if available
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
                                    } ?: run {
                                        // Fallback to placeholder if photo loading failed
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
                                } ?: run {
                                    // No photo yet - show placeholder
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
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                // Line 1: bold name left, origin/roast right
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
                                // Line 2: short date left, grinder/weight right
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
    }
}