package coffee.app.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coffee.app.backup.BackupContents
import coffee.app.backup.BackupException
import coffee.app.core.BitoholicTopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    var includePhotos by remember { mutableStateOf(true) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var pendingRestore by remember { mutableStateOf<BackupContents?>(null) }
    val backupFileName = "BitoCoffee-${System.currentTimeMillis()}.zip"
    var pendingZipBytes by remember { mutableStateOf<ByteArray?>(null) }
    var navigateAfterRestore by remember { mutableStateOf(false) }

    // Navigate back after restore
    LaunchedEffect(navigateAfterRestore) {
        if (navigateAfterRestore) { navigateAfterRestore = false; onNavigateBack() }
    }

    // Snackbar for messages
    LaunchedEffect(message) {
        message?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    // Restore overwrite/merge dialog
    if (showRestoreDialog && pendingRestore != null) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false; pendingRestore = null },
            title = { Text("Restore Backup") },
            text = { Text("How would you like to restore the backup data?") },
            confirmButton = {
                Button(onClick = {
                    val contents = pendingRestore!!
                    showRestoreDialog = false
                    pendingRestore = null
                    scope.launch {
                        performRestore(viewModel, contents, RestoreMode.OVERWRITE, context.filesDir.absolutePath, onNavigateBack = { navigateAfterRestore = true })
                    }
                }) {
                    Text("Overwrite")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    val contents = pendingRestore!!
                    showRestoreDialog = false
                    pendingRestore = null
                    scope.launch {
                        performRestore(viewModel, contents, RestoreMode.MERGE, context.filesDir.absolutePath, onNavigateBack = { navigateAfterRestore = true })
                    }
                }) {
                    Text("Merge")
                }
            }
        )
    }

    // SAF file picker for restore
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    viewModel.setLoading(true)
                    val bytes = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.readBytes()
                            ?: throw BackupException("Could not read file")
                    }
                    val contents = viewModel.parseBackup(bytes)
                    pendingRestore = contents
                    showRestoreDialog = true
                } catch (e: Exception) {
                    viewModel.setMessage("Restore failed: ${e.message}")
                } finally {
                    viewModel.setLoading(false)
                }
            }
        }
    }

    // SAF save file picker for backup
    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->
        if (uri != null && pendingZipBytes != null) {
            scope.launch {
                try {
                    var actualName = backupFileName
                    withContext(Dispatchers.IO) {
                        // Query the display name from the returned URI
                        val cursor = context.contentResolver.query(uri, null, null, null, null)
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val nameIdx = it.getColumnIndex(android.provider.DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                                if (nameIdx >= 0) actualName = it.getString(nameIdx)
                            }
                        }
                        context.contentResolver.openOutputStream(uri)?.use { os ->
                            os.write(pendingZipBytes)
                        }
                    }
                    viewModel.setMessage("Backup saved as $actualName")
                } catch (e: Exception) {
                    viewModel.setMessage("Save failed: ${e.message}")
                } finally {
                    pendingZipBytes = null
                    viewModel.setLoading(false)
                }
            }
        } else {
            pendingZipBytes = null
            viewModel.setLoading(false)
        }
    }

    // Save backup — creates ZIP then opens file picker for location/name
    val saveBackup: () -> Unit = {
        scope.launch {
            try {
                viewModel.setLoading(true)
                val zipBytes = withContext(Dispatchers.IO) { viewModel.createBackup(includePhotos) }
                pendingZipBytes = zipBytes
                saveLauncher.launch(backupFileName)
            } catch (e: Exception) {
                viewModel.setMessage("Backup failed: ${e.message}")
                viewModel.setLoading(false)
            }
        }
    }

    // Share backup
    val shareBackup: () -> Unit = {
        scope.launch {
            try {
                viewModel.setLoading(true)
                val zipBytes = withContext(Dispatchers.IO) { viewModel.createBackup(includePhotos) }
                val tempDir = File(context.cacheDir, "backups").also { it.mkdirs() }
                val tempFile = File(tempDir, backupFileName).also { it.writeBytes(zipBytes) }
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
                context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                    type = "application/zip"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }, "Share backup"))
                viewModel.setMessage("Backup created")
            } catch (e: Exception) {
                viewModel.setMessage("Backup failed: ${e.message}")
            } finally {
                viewModel.setLoading(false)
            }
        }
    }

    Scaffold(
        topBar = {
            BitoholicTopBar(
                title = "Settings",
                showBack = false,
                onBackClick = onNavigateBack,
                showSettings = false
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                val themeMode by viewModel.themeMode.collectAsState()
                val themeModes = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    themeModes.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = themeModes.size
                            )
                        ) {
                            Text(getThemeModeLabel(mode))
                        }
                    }
                }

                HorizontalDivider()

                Text(
                    text = "Backup & Restore",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = includePhotos,
                        onCheckedChange = { includePhotos = it }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Include Photos",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Button(
                    onClick = shareBackup,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Share Backup")
                }

                Button(
                    onClick = saveBackup,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Save Backup")
                }

                OutlinedButton(
                    onClick = { restoreLauncher.launch(arrayOf("application/zip", "*/*")) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Restore Data")
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

private suspend fun performRestore(
    viewModel: SettingsViewModel,
    contents: BackupContents,
    mode: RestoreMode,
    photoDir: String,
    onNavigateBack: () -> Unit
) {
    try {
        viewModel.setLoading(true)
        withContext(Dispatchers.IO) {
            viewModel.restoreBackup(contents, mode, photoDir)
        }
        viewModel.setMessage("Restore completed (${contents.entries.size} entries)")
        onNavigateBack()
    } catch (e: Exception) {
        viewModel.setMessage("Restore failed: ${e.message}")
    } finally {
        viewModel.setLoading(false)
    }
}

private fun getThemeModeLabel(themeMode: ThemeMode): String {
    return when (themeMode) {
        ThemeMode.SYSTEM -> "System"
        ThemeMode.LIGHT -> "Light"
        ThemeMode.DARK -> "Dark"
    }
}
