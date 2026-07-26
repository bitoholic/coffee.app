package coffee.app.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coffee.app.core.BitoholicTopBar
import coffee.app.core.BrandRed
import coffee.app.core.DateFormatUtil
import coffee.app.core.PhotoManager
import coffee.app.data.database.BrewEntry
import coffee.app.data.database.EntryPhotoDao
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewEntryDetailScreen(
    entry: BrewEntry,
    entryPhotoDao: EntryPhotoDao,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showPhotoFullscreen by remember { mutableStateOf(false) }
    var fullscreenIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val photoManager = remember { PhotoManager(context) }
    val photos by remember(entry.uuid) { entryPhotoDao.getPhotosForEntry(entry.uuid) }.collectAsState(initial = emptyList())
    val photoBitmaps = remember(photos) { photos.mapNotNull { photoManager.loadPhoto(it.photoPath) } }

    // Photo fullscreen overlay: HorizontalPager drives swipe navigation between
    // photos, and each photo can be pinch-zoomed/panned independently. The two
    // gestures are kept from fighting each other by using a manual awaitEachGesture
    // that only CONSUMES multi-touch (zoom) events — single-finger drags are left
    // unconsumed so the pager's built-in swipe gesture handles them smoothly.
    if (showPhotoFullscreen && photoBitmaps.isNotEmpty()) {
        val pagerState = rememberPagerState(initialPage = fullscreenIndex) { photoBitmaps.size }
        val coroutineScope = rememberCoroutineScope()

        var scale by remember { mutableFloatStateOf(1f) }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }
        var imageSize by remember { mutableStateOf(IntSize.Zero) }

        LaunchedEffect(pagerState.currentPage) {
            fullscreenIndex = pagerState.currentPage
            scale = 1f
            offsetX = 0f
            offsetY = 0f
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures { showPhotoFullscreen = false }
                }
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val firstDown = awaitFirstDown()
                        var currentScale = scale
                        var previousCentroid = firstDown.position
                        var previousSpan = 0f
                        var wasMultiTouch = false

                        do {
                            val event = awaitPointerEvent()
                            val changes = event.changes.filter { it.pressed }

                            if (changes.size >= 2) {
                                // Multi-touch: handle pinch-zoom and two-finger pan
                                wasMultiTouch = true
                                val centroid = changes.fold(Offset.Zero) { acc, c ->
                                    acc + c.position
                                } / changes.size.toFloat()
                                val span = (changes[0].position - changes[1].position).getDistance()

                                if (previousSpan > 0f) {
                                    val zoomDelta = span / previousSpan
                                    val newScale = (currentScale * zoomDelta).coerceIn(1f, 5f)
                                    currentScale = newScale
                                    scale = newScale

                                    if (newScale > 1.01f) {
                                        val panDelta = centroid - previousCentroid
                                        val halfW = imageSize.width * (newScale - 1f) / 2f
                                        val halfH = imageSize.height * (newScale - 1f) / 2f
                                        offsetX = (offsetX + panDelta.x).coerceIn(-halfW, halfW)
                                        offsetY = (offsetY + panDelta.y).coerceIn(-halfH, halfH)
                                    } else {
                                        offsetX = 0f
                                        offsetY = 0f
                                    }
                                }

                                previousSpan = span
                                previousCentroid = centroid
                                changes.forEach { it.consume() }
                            }
                            // Single-finger drag at 1x zoom: NOT consumed,
                            // pager handles it as smooth page swipe.
                        } while (changes.any { it.pressed })
                    }
                }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    photoBitmaps[page]?.let { bitmap ->
                        val isCurrent = page == pagerState.currentPage
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .onSizeChanged { if (isCurrent) imageSize = it }
                                .graphicsLayer(
                                    scaleX = if (isCurrent) scale else 1f,
                                    scaleY = if (isCurrent) scale else 1f,
                                    translationX = if (isCurrent) offsetX else 0f,
                                    translationY = if (isCurrent) offsetY else 0f
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            // Navigation arrows
            if (photoBitmaps.size > 1) {
                if (pagerState.currentPage > 0) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous photo",
                            tint = Color.White
                        )
                    }
                }

                if (pagerState.currentPage < photoBitmaps.size - 1) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next photo",
                            tint = Color.White
                        )
                    }
                }

                // Position indicator
                Text(
                    text = "${pagerState.currentPage + 1}/${photoBitmaps.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
        return
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this brew entry?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandRed,
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            BitoholicTopBar(
                title = entry.beanName,
                showBack = true,
                onBackClick = onBack,
                showSettings = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Photo section
            if (photoBitmaps.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    itemsIndexed(photoBitmaps) { index, bitmap ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.3f)
                                .height(220.dp)
                                .clickable {
                                    fullscreenIndex = index
                                    showPhotoFullscreen = true
                                }
                        ) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Photo $index",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "No photo",
                            tint = BrandRed,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "No photo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = entry.beanName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

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
                        text = "Modified: ${DateFormatUtil.toShortDate(entry.lastModifiedDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("Origin", entry.beanOrigin ?: "Unknown")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("Roast", entry.roastType)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("Grinder", entry.grinderSetting.toString())
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("Weight", "${entry.portionWeight}g")

                        if (!entry.description.isNullOrBlank()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = entry.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandRed,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Edit")
                    }

                    Button(
                        onClick = { showDeleteConfirmation = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandRed,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}