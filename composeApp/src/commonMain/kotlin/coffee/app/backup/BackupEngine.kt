package coffee.app.backup

import coffee.app.data.database.BrewEntry
import coffee.app.data.database.EntryPhoto
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object BackupEngine {

    private const val BACKUP_VERSION = 1
    private const val ENTRIES_FILE = "entries.json"
    private const val MANIFEST_FILE = "manifest.json"
    private const val PHOTOS_DIR = "photos/"

    fun createBackup(
        entries: List<BrewEntry>,
        entryPhotos: List<EntryPhoto>,
        includePhotos: Boolean
    ): ByteArray {
        if (entries.isEmpty()) {
            throw BackupException("No entries to back up")
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.US)
        val timestamp = dateFormat.format(Date())
        val photoPaths = if (includePhotos) {
            entryPhotos.map { it.photoPath }.distinct()
        } else {
            emptyList()
        }

        val backupEntries = entries.map { entry ->
            val paths = if (includePhotos) {
                entryPhotos.filter { it.entryUuid == entry.uuid }.map { it.photoPath }
            } else {
                emptyList()
            }
            BackupEntry(
                uuid = entry.uuid,
                beanName = entry.beanName,
                beanOrigin = entry.beanOrigin,
                roastType = entry.roastType,
                grinderSetting = entry.grinderSetting,
                portionWeight = entry.portionWeight,
                description = entry.description,
                createdDate = entry.createdDate,
                lastModifiedDate = entry.lastModifiedDate,
                photoPaths = paths
            )
        }

        val manifest = Manifest(
            version = BACKUP_VERSION,
            createdDate = timestamp,
            entryCount = entries.size,
            hasPhotos = includePhotos
        )

        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zos ->
            zos.putNextEntry(ZipEntry(MANIFEST_FILE))
            zos.write(toJson(manifest).toByteArray())
            zos.closeEntry()

            zos.putNextEntry(ZipEntry(ENTRIES_FILE))
            zos.write(toJson(backupEntries).toByteArray())
            zos.closeEntry()

            if (includePhotos) {
                val seenPaths = mutableSetOf<String>()
                for (photoPath in photoPaths) {
                    if (photoPath in seenPaths) continue
                    seenPaths.add(photoPath)
                    val photoFile = File(photoPath)
                    if (photoFile.exists()) {
                        val fileName = photoFile.name
                        zos.putNextEntry(ZipEntry("$PHOTOS_DIR$fileName"))
                        photoFile.inputStream().use { it.copyTo(zos) }
                        zos.closeEntry()
                    }
                }
            }
        }

        return baos.toByteArray()
    }

    fun parseBackup(zipBytes: ByteArray): BackupContents {
        var manifest: Manifest? = null
        var backupEntries: List<BackupEntry>? = null
        val photoBytes = mutableMapOf<String, ByteArray>()

        ZipInputStream(ByteArrayInputStream(zipBytes)).use { zis ->
            var entry: ZipEntry? = zis.nextEntry
            while (entry != null) {
                val content = zis.readBytes()
                when (entry.name) {
                    MANIFEST_FILE -> manifest = parseManifest(String(content))
                    ENTRIES_FILE -> backupEntries = parseBackupEntries(String(content))
                    else -> {
                        if (entry.name.startsWith(PHOTOS_DIR) && !entry.isDirectory) {
                            photoBytes[entry.name.removePrefix(PHOTOS_DIR)] = content
                        }
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }

        val m = manifest ?: throw BackupException("Missing manifest")
        val be = backupEntries ?: throw BackupException("Missing entries data")
        return BackupContents(entries = be, manifest = m, photoBytes = photoBytes)
    }

    private fun toJson(manifest: Manifest): String = """{
  "version": ${manifest.version},
  "createdDate": "${escapeJson(manifest.createdDate)}",
  "entryCount": ${manifest.entryCount},
  "hasPhotos": ${manifest.hasPhotos}
}"""

    private fun toJson(entries: List<BackupEntry>): String {
        return "[\n  ${entries.joinToString(",\n  ") { toJsonEntry(it) }}\n]"
    }

    private fun toJsonEntry(e: BackupEntry): String = """{
    "uuid": "${escapeJson(e.uuid)}",
    "beanName": "${escapeJson(e.beanName)}",
    "beanOrigin": ${nullableString(e.beanOrigin)},
    "roastType": "${escapeJson(e.roastType)}",
    "grinderSetting": ${e.grinderSetting},
    "portionWeight": ${e.portionWeight},
    "description": ${nullableString(e.description)},
    "createdDate": ${e.createdDate},
    "lastModifiedDate": ${e.lastModifiedDate},
    "photoPaths": [${e.photoPaths.joinToString(", ") { "\"${escapeJson(it)}\"" }}]
  }"""

    private fun parseManifest(json: String): Manifest = Manifest(
        version = extractInt(json, "version"),
        createdDate = extractString(json, "createdDate"),
        entryCount = extractInt(json, "entryCount"),
        hasPhotos = extractBoolean(json, "hasPhotos")
    )

    private fun parseBackupEntries(json: String): List<BackupEntry> {
        val trimmed = json.trim()
        if (trimmed.length < 2) return emptyList()
        val inner = trimmed.substring(1, trimmed.length - 1).trim()
        if (inner.isEmpty()) return emptyList()
        val entries = mutableListOf<BackupEntry>()
        var depth = 0; var start = -1
        for (i in inner.indices) {
            when (inner[i]) {
                '{' -> { if (depth == 0) start = i; depth++ }
                '}' -> { depth--; if (depth == 0 && start >= 0) { entries.add(parseBackupEntry(inner.substring(start, i + 1))); start = -1 } }
            }
        }
        return entries
    }

    private fun parseBackupEntry(json: String) = BackupEntry(
        uuid = extractString(json, "uuid"),
        beanName = extractString(json, "beanName"),
        beanOrigin = extractNullableString(json, "beanOrigin"),
        roastType = extractString(json, "roastType"),
        grinderSetting = extractInt(json, "grinderSetting"),
        portionWeight = extractDouble(json, "portionWeight"),
        description = extractNullableString(json, "description"),
        createdDate = extractLong(json, "createdDate"),
        lastModifiedDate = extractLong(json, "lastModifiedDate"),
        photoPaths = extractStringArray(json, "photoPaths")
    )

    private fun extractString(json: String, key: String): String {
        val pattern = "\"$key\":\\s*\"([^\"]*)\"".toRegex()
        return pattern.find(json)?.groupValues?.getOrNull(1) ?: throw BackupException("Missing field: $key")
    }
    private fun extractNullableString(json: String, key: String): String? {
        val match = Regex("\"$key\":\\s*([^,}]+)").find(json)?.groupValues?.getOrNull(1)?.trim() ?: return null
        if (match == "null") return null
        return match.removeSurrounding("\"")
    }
    private fun extractInt(json: String, key: String): Int {
        return Regex("\"$key\":\\s*(\\d+)").find(json)?.groupValues?.getOrNull(1)?.toIntOrNull()
            ?: throw BackupException("Missing or invalid: $key")
    }
    private fun extractLong(json: String, key: String): Long {
        return Regex("\"$key\":\\s*(\\d+)").find(json)?.groupValues?.getOrNull(1)?.toLongOrNull()
            ?: throw BackupException("Missing or invalid: $key")
    }
    private fun extractDouble(json: String, key: String): Double {
        return Regex("\"$key\":\\s*([\\d.]+)").find(json)?.groupValues?.getOrNull(1)?.toDoubleOrNull()
            ?: throw BackupException("Missing or invalid: $key")
    }
    private fun extractBoolean(json: String, key: String): Boolean {
        return Regex("\"$key\":\\s*(true|false)").find(json)?.groupValues?.getOrNull(1)?.toBooleanStrictOrNull()
            ?: throw BackupException("Missing or invalid: $key")
    }
    private fun extractStringArray(json: String, key: String): List<String> {
        val match = Regex("\"$key\":\\s*\\[([^\\]]*)\\]").find(json)?.groupValues?.getOrNull(1) ?: return emptyList()
        if (match.isBlank()) return emptyList()
        return match.split(",").map { it.trim().removeSurrounding("\"") }
    }
    private fun nullableString(value: String?) = value?.let { "\"${escapeJson(it)}\"" } ?: "null"
    private fun escapeJson(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")
}
