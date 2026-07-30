package coffee.app.data.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class BrewEntryDaoTest {
    
    private lateinit var database: CoffeeDatabase
    private lateinit var dao: BrewEntryDao
    
    @Before
    fun setUp() {
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, CoffeeDatabase::class.java).build()
        dao = database.brewEntryDao()
    }
    
    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun `observeFavourites returns favourite entries`() = runBlocking {
        // Insert test entries
        val entry1 = BrewEntry(
            uuid = "uuid1",
            beanName = "Favourite Bean",
            beanOrigin = "Brazil",
            roastType = "Medium",
            grinderSetting = 15,
            portionWeight = 18.0,
            description = "Test description",
            isFavourite = 1,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )
        
        val entry2 = BrewEntry(
            uuid = "uuid2",
            beanName = "Normal Bean",
            beanOrigin = "Colombia",
            roastType = "Light",
            grinderSetting = 12,
            portionWeight = 20.0,
            description = "Test description 2",
            isFavourite = 0,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )
        
        dao.upsert(entry1)
        dao.upsert(entry2)
        
        // Collect favourites
        val favourites = dao.observeFavourites().first()
        
        // Verify only favourite entry is returned
        assertEquals(1, favourites.size)
        assertEquals("uuid1", favourites[0].uuid)
    }
    
    @Test
    fun `deleteByUuids deletes multiple entries by uuids`() = runBlocking {
        // Insert test entries
        val entry1 = BrewEntry(
            uuid = "uuid1",
            beanName = "Bean 1",
            beanOrigin = "Brazil",
            roastType = "Medium",
            grinderSetting = 15,
            portionWeight = 18.0,
            description = "Test description",
            isFavourite = 0,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )
        
        val entry2 = BrewEntry(
            uuid = "uuid2",
            beanName = "Bean 2",
            beanOrigin = "Colombia",
            roastType = "Light",
            grinderSetting = 12,
            portionWeight = 20.0,
            description = "Test description 2",
            isFavourite = 0,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )
        
        val entry3 = BrewEntry(
            uuid = "uuid3",
            beanName = "Bean 3",
            beanOrigin = "Ethiopia",
            roastType = "Dark",
            grinderSetting = 18,
            portionWeight = 15.0,
            description = "Test description 3",
            isFavourite = 0,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )
        
        dao.upsert(entry1)
        dao.upsert(entry2)
        dao.upsert(entry3)
        
        // Verify all entries exist
        assertEquals(3, dao.getAll().size)
        
        // Delete specific entries
        dao.deleteByUuids(listOf("uuid1", "uuid3"))
        
        // Verify only one entry remains
        val remainingEntries = dao.getAll()
        assertEquals(1, remainingEntries.size)
        assertEquals("uuid2", remainingEntries[0].uuid)
    }
    
    @Test
    fun `updateFavourite updates favourite status`() = runBlocking {
        // Insert test entry 
        val entry = BrewEntry(
            uuid = "uuid1",
            beanName = "Test Bean",
            beanOrigin = "Brazil",
            roastType = "Medium",
            grinderSetting = 15,
            portionWeight = 18.0,
            description = "Test description",
            isFavourite = 0,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )
        
        dao.upsert(entry)
        
        // Verify initial state
        val initialEntry = dao.getById("uuid1")!!
        assertEquals(0, initialEntry.isFavourite)
        
        // Update to favourited
        dao.updateFavourite("uuid1", true)
        
        // Verify updated state
        val favouritedEntry = dao.getById("uuid1")!!
        assertEquals(1, favouritedEntry.isFavourite)
        
        // Update back to unfavourited
        dao.updateFavourite("uuid1", false)
        
        // Verify updated back state
        val unfavouritedEntry = dao.getById("uuid1")!!
        assertEquals(0, unfavouritedEntry.isFavourite)
    }
    
    @Test
    fun `getAll returns all entries`() = runBlocking {
        // Insert test entries
        val entry1 = BrewEntry(
            uuid = "uuid1",
            beanName = "Bean 1",
            beanOrigin = "Brazil",
            roastType = "Medium",
            grinderSetting = 15,
            portionWeight = 18.0,
            description = "Test description",
            isFavourite = 0,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )
        
        val entry2 = BrewEntry(
            uuid = "uuid2",
            beanName = "Bean 2",
            beanOrigin = "Colombia",
            roastType = "Light",
            grinderSetting = 12,
            portionWeight = 20.0,
            description = "Test description 2",
            isFavourite = 1,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )
        
        dao.upsert(entry1)
        dao.upsert(entry2)
        
        // Get all entries 
        val allEntries = dao.getAll()
        
        // Verify all entries are returned
        assertEquals(2, allEntries.size)
        val uuids = allEntries.map { it.uuid }
        assertTrue(uuids.contains("uuid1"))
        assertTrue(uuids.contains("uuid2"))
    }
    
    @Test
    fun `upsert inserts new entry`() = runBlocking {
        val entry = BrewEntry(
            uuid = "new-uuid",
            beanName = "New Bean",
            beanOrigin = "Brazil",
            roastType = "Medium",
            grinderSetting = 15,
            portionWeight = 18.0,
            description = "Test description",
            isFavourite = 0,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )
        
        // Upsert the entry
        dao.upsert(entry)
        
        // Verify the entry is inserted
        val retrievedEntry = dao.getById("new-uuid")
        assertNotNull(retrievedEntry)
        assertEquals("New Bean", retrievedEntry!!.beanName)
    }
    
    @Test
    fun `upsert updates existing entry`() = runBlocking {
        // Insert an initial entry
        val entry1 = BrewEntry(
            uuid = "existing-uuid",
            beanName = "Old Bean",
            beanOrigin = "Brazil",
            roastType = "Medium",
            grinderSetting = 15,
            portionWeight = 18.0,
            description = "Test description",
            isFavourite = 0,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )
        
        dao.upsert(entry1)
        
        // Retrieve and verify
        val retrievedEntry1 = dao.getById("existing-uuid")
        assertEquals("Old Bean", retrievedEntry1!!.beanName)
        
        // Update the entry with same UUID
        val entry2 = BrewEntry(
            uuid = "existing-uuid",
            beanName = "Updated Bean",
            beanOrigin = "Colombia",
            roastType = "Light",
            grinderSetting = 12,
            portionWeight = 20.0,
            description = "Test description 2",
            isFavourite = 1,
            createdDate = System.currentTimeMillis(), // Different date
            lastModifiedDate = System.currentTimeMillis() // Different date
        )
        
        // Upsert the updated entry
        dao.upsert(entry2)
        
        // Verify the entry was updated
        val retrievedEntry2 = dao.getById("existing-uuid")
        assertEquals("Updated Bean", retrievedEntry2!!.beanName)
        assertEquals(1, retrievedEntry2.isFavourite)
    }
    
    @Test
    fun `getById returns entry by uuid`() = runBlocking {
        // Insert test entry
        val entry = BrewEntry(
            uuid = "test-uuid",
            beanName = "Test Bean",
            beanOrigin = "Brazil",
            roastType = "Medium",
            grinderSetting = 15,
            portionWeight = 18.0,
            description = "Test description",
            isFavourite = 0,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )
        
        dao.upsert(entry)
        
        // Get the entry by UUID
        val retrievedEntry = dao.getById("test-uuid")
        
        // Verify it's the right entry
        assertNotNull(retrievedEntry)
        assertEquals("Test Bean", retrievedEntry!!.beanName)
        assertEquals("test-uuid", retrievedEntry.uuid)
    }
}