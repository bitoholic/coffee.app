package coffee.app.data.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BrewEntryDaoTest {
    
    private lateinit var db: CoffeeDatabase
    private lateinit var dao: BrewEntryDao

    @BeforeTest
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            androidx.test.core.app.ApplicationProvider.getApplicationContext(),
            CoffeeDatabase::class.java
        ).build()
        dao = db.brewEntryDao()
    }

    @AfterTest
    fun tearDown() {
        db.close()
    }

    @Test
    fun `observeFavourites returns only favourite entries`() = runBlocking {
        // Insert test entries
        val entry1 = BrewEntry(
            uuid = "1",
            beanName = "Bean 1",
            beanOrigin = "Origin 1",
            roastType = "Light",
            grinderSetting = 15,
            portionWeight = 50.0,
            description = "Description 1",
            createdDate = 1000,
            lastModifiedDate = 1000,
            isFavourite = 1
        )
        
        val entry2 = BrewEntry(
            uuid = "2",
            beanName = "Bean 2", 
            beanOrigin = "Origin 2",
            roastType = "Medium",
            grinderSetting = 12,
            portionWeight = 60.0,
            description = "Description 2",
            createdDate = 2000,
            lastModifiedDate = 2000,
            isFavourite = 0
        )
        
        dao.upsert(entry1)
        dao.upsert(entry2)
        
        // Test observeFavourites
        val favourites = dao.observeFavourites().first()
        assertEquals(1, favourites.size)
        assertEquals("1", favourites[0].uuid)
        assertEquals("Bean 1", favourites[0].beanName)
    }

    @Test
    fun `deleteByUuids deletes multiple entries`() = runBlocking {
        // Insert test entries
        val entry1 = BrewEntry(
            uuid = "1",
            beanName = "Bean 1",
            beanOrigin = "Origin 1",
            roastType = "Light",
            grinderSetting = 15,
            portionWeight = 50.0,
            description = "Description 1",
            createdDate = 1000,
            lastModifiedDate = 1000,
            isFavourite = 1
        )
        
        val entry2 = BrewEntry(
            uuid = "2",
            beanName = "Bean 2", 
            beanOrigin = "Origin 2",
            roastType = "Medium",
            grinderSetting = 12,
            portionWeight = 60.0,
            description = "Description 2",
            createdDate = 2000,
            lastModifiedDate = 2000,
            isFavourite = 0
        )
        
        dao.upsert(entry1)
        dao.upsert(entry2)
        
        // Delete both entries
        dao.deleteByUuids(listOf("1", "2"))
        
        // Verify they're deleted
        val allEntries = dao.getAll()
        assertEquals(0, allEntries.size)
    }

    @Test
    fun `updateFavourite updates favourite status`() = runBlocking {
        // Insert test entry
        val entry = BrewEntry(
            uuid = "1",
            beanName = "Bean 1",
            beanOrigin = "Origin 1",
            roastType = "Light",
            grinderSetting = 15,
            portionWeight = 50.0,
            description = "Description 1",
            createdDate = 1000,
            lastModifiedDate = 1000,
            isFavourite = 0
        )
        
        dao.upsert(entry)
        
        // Update favourite status to true
        dao.updateFavourite("1", true)
        
        // Verify update
        val updatedEntry = dao.getById("1")!!
        assertEquals(1, updatedEntry.isFavourite)
        
        // Update favourite status to false
        dao.updateFavourite("1", false)
        
        // Verify update
        val updatedEntry2 = dao.getById("1")!!
        assertEquals(0, updatedEntry2.isFavourite)
    }
}