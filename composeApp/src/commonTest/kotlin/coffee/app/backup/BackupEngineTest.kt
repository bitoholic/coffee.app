package coffee.app.backup

import coffee.app.data.database.BrewEntry
import coffee.app.data.database.EntryPhoto
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BackupEngineTest {

    private val testEntry = BrewEntry(
        uuid = "test-uuid",
        beanName = "Test Bean",
        beanOrigin = "Test Origin",
        roastType = "Light",
        grinderSetting = 15,
        portionWeight = 50.0,
        description = "Test description",
        createdDate = 1234567890L,
        lastModifiedDate = 1234567890L,
        isFavourite = 1
    )

    @Test
    fun `createBackup returns valid ZIP bytes`() {
        runBlocking {
            val result = BackupEngine.createBackup(
                entries = listOf(testEntry),
                entryPhotos = emptyList(),
                includePhotos = false
            )
            assertTrue(result.isNotEmpty())
            val zipInputStream = ZipInputStream(ByteArrayInputStream(result))
            val entryNames = mutableListOf<String>()
            var ze = zipInputStream.nextEntry
            while (ze != null) {
                entryNames.add(ze.name)
                zipInputStream.closeEntry()
                ze = zipInputStream.nextEntry
            }
            assertTrue(entryNames.contains("manifest.json"))
            assertTrue(entryNames.contains("entries.json"))
        }
    }

    @Test
    fun `createBackup to parseBackup roundtrip preserves all fields`() {
        runBlocking {
            val zipBytes = BackupEngine.createBackup(
                entries = listOf(testEntry),
                entryPhotos = emptyList(),
                includePhotos = false
            )
            val contents = BackupEngine.parseBackup(zipBytes)
            assertEquals(1, contents.entries.size)
            assertEquals("test-uuid", contents.entries[0].uuid)
            assertEquals("Test Bean", contents.entries[0].beanName)
            assertEquals("Test Origin", contents.entries[0].beanOrigin)
            assertEquals(1, contents.entries[0].isFavourite)
        }
    }

    @Test
    fun `parseBackup with includePhotos mode`() {
        runBlocking {
            val zipBytes = BackupEngine.createBackup(
                entries = listOf(testEntry),
                entryPhotos = emptyList(),
                includePhotos = false
            )
            val contents = BackupEngine.parseBackup(zipBytes)
            assertTrue(contents.manifest.hasPhotos == false)
        }
    }

    @Test
    fun `empty entries throws BackupException`() {
        runBlocking {
            assertFailsWith<BackupException> {
                BackupEngine.createBackup(
                    entries = emptyList(),
                    entryPhotos = emptyList(),
                    includePhotos = false
                )
            }
        }
    }

    @Test
    fun `corrupt ZIP throws BackupException`() {
        runBlocking {
            assertFailsWith<BackupException> {
                BackupEngine.parseBackup(ByteArray(10) { 0x42 })
            }
        }
    }
}
