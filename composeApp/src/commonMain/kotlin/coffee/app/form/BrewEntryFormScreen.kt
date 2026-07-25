package coffee.app.form

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coffee.app.data.database.BrewEntry
import coffee.app.domain.RoastType
import coffee.app.core.BitoholicTopBar
import coffee.app.core.BrandRed
import coffee.app.core.PhotoManager
import androidx.compose.ui.graphics.Color
import java.util.UUID
import androidx.core.content.FileProvider
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewEntryFormScreen(
    viewModel: BrewEntryFormViewModel,
    onNavigateBack: () -> Unit,
    entryToEdit: BrewEntry? = null
) {
    val state by viewModel.state.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Enter edit mode if an entry is provided
    if (entryToEdit != null && !state.isEditing) {
        viewModel.enterEditMode(entryToEdit)
    }
    
    var showDiscardDialog by remember { mutableStateOf(false) }
    
    // Intercept Android system back button
    BackHandler(enabled = viewModel.isDirty()) {
        showDiscardDialog = true
    }
    
    // Discard changes confirmation dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to discard them?") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    viewModel.clearEditState()
                    onNavigateBack()
                }) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Keep editing")
                }
            }
        )
    }
    
    // Navigate back on successful save
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            viewModel.resetSaveSuccess()
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            BitoholicTopBar(
                title = if (state.isEditing) "Edit Brew Entry" else "New Brew Entry",
                showBack = true,
                onBackClick = { if (viewModel.isDirty()) showDiscardDialog = true else onNavigateBack() },
                showSettings = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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

            // Photo section (moved to top of form)
            // Photo picker with Camera option
            var showPhotoPicker by remember { mutableStateOf(false) }
            val photoManager = remember { coffee.app.core.PhotoManager(context) }

            // Camera launcher
            var currentCameraPath by remember { mutableStateOf<String?>(null) }
            val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    currentCameraPath?.let { path ->
                        viewModel.onPhotoPathChanged(path)
                    }
                }
            }

            // Gallery launcher
            val photoLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let {
                    val path = photoManager.savePhoto(it)
                    viewModel.onPhotoPathChanged(path)
                }
            }

            // Camera URI - needed for camera launcher
            val photoUri = remember {
                val file = File(context.filesDir, "photos/camera_${UUID.randomUUID()}.jpg")
                file.parentFile?.mkdirs()
                currentCameraPath = file.absolutePath
                androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            }

            // Dialog for photo selection
            if (showPhotoPicker) {
                AlertDialog(
                    onDismissRequest = { showPhotoPicker = false },
                    title = { Text("Add Photo") },
                    text = {
                        Column {
                            TextButton(onClick = { 
                                showPhotoPicker = false
                                photoLauncher.launch("image/*")
                            }) { Text("Choose from Gallery") }
                            TextButton(onClick = { 
                                showPhotoPicker = false
                                cameraLauncher.launch(photoUri)
                            }) { Text("Take Photo") }
                        }
                    },
                    confirmButton = {}
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.photoPath != null) {
                    // Show photo thumbnail
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        // Load and display the photo
                        val bitmap = remember(state.photoPath) {
                            photoManager.loadPhoto(state.photoPath!!)
                        }
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Entry photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    // Remove button
                    IconButton(
                        onClick = { viewModel.onPhotoPathChanged(null) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, "Remove photo")
                    }
                } else {
                    // Add photo button
                    OutlinedButton(onClick = { showPhotoPicker = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Photo")
                    }
                }
            }

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
                label = { Text("Grinder Setting (1-48) *") },
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
                label = { Text("Portion Weight (grams) *") },
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
                label = { Text("Description (optional)") },
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
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandRed,
                    contentColor = Color.White
                )
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

            // Validation errors summary
            if (state.validationErrors.isNotEmpty()) {
                Text(
                    text = "Please fix the errors above before saving.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
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