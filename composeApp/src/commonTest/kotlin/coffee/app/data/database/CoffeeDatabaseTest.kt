package coffee.app.data.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

expect fun getTestDatabaseBuilder(): RoomDatabase.Builder<CoffeeDatabase>

abstract class CoffeeDatabaseTest {

    private lateinit var db: CoffeeDatabase
    private lateinit var brewDao: BrewEntryDao
    private lateinit var originDao: OriginDao

    @BeforeTest
    fun setUp() {
        db = getTestDatabaseBuilder().build()
        brewDao = db.brewEntryDao()
        originDao = db.originDao()
    }

    @AfterTest
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertAndReadBrewEntry() = runBlocking {
        val origin = OriginEntity(
            country = "Ethiopia",
            region = "Yirgacheffe",
            roaster = "Local Roaster",
            roastDate = 1700000000000L,
            roastProfile = "Light"
        )
        val originId = originDao.insertOrigin(origin)
        assertTrue(originId > 0, "Origin ID should be positive")

        val brew = BrewEntryEntity(
            timestamp = 1700000001000L,
            grindSize = 15.0f,
            doseWeightGrams = 18.0f,
            yieldWeightGrams = 36.0f,
            extractionTimeSeconds = 30,
            notes = "Great fruity notes",
            originId = originId
        )
        val brewId = brewDao.insertBrew(brew)
        assertTrue(brewId > 0, "Brew ID should be positive")

        val allBrews = brewDao.observeAllBrews().first()
        assertEquals(1, allBrews.size, "Should have exactly one brew")
        with(allBrews[0]) {
            assertEquals(brewId, id)
            assertEquals(15.0f, grindSize)
            assertEquals(18.0f, doseWeightGrams)
            assertEquals(36.0f, yieldWeightGrams)
            assertEquals(30, extractionTimeSeconds)
            assertEquals("Great fruity notes", notes)
            assertEquals(originId, originId)
        }
    }

    @Test
    fun testInsertAndReadOrigin() = runBlocking {
        val origin = OriginEntity(
            country = "Colombia",
            region = "Huila",
            roaster = "Test Roaster",
            roastDate = 1700000000000L,
            roastProfile = "Medium"
        )
        val originId = originDao.insertOrigin(origin)
        assertTrue(originId > 0)

        val fetched = originDao.getOriginById(originId)
        assertNotNull(fetched)
        assertEquals("Colombia", fetched.country)
        assertEquals("Huila", fetched.region)
        assertEquals("Test Roaster", fetched.roaster)
        assertEquals("Medium", fetched.roastProfile)
    }

    @Test
    fun testDeleteBrewEntry() = runBlocking {
        val brew = BrewEntryEntity(
            timestamp = 1700000001000L,
            grindSize = 12.0f,
            doseWeightGrams = 20.0f,
            yieldWeightGrams = 40.0f,
            extractionTimeSeconds = 28,
            notes = "Test delete"
        )
        val brewId = brewDao.insertBrew(brew)
        assertTrue(brewId > 0)

        val allBrewsBefore = brewDao.observeAllBrews().first()
        assertEquals(1, allBrewsBefore.size)

        brewDao.deleteBrew(allBrewsBefore[0])

        val allBrewsAfter = brewDao.observeAllBrews().first()
        assertTrue(allBrewsAfter.isEmpty(), "List should be empty after delete")
    }

    @Test
    fun testBrewsOrderedByTimestampDesc() = runBlocking {
        val brew1 = BrewEntryEntity(timestamp = 1000L, grindSize = 10f, doseWeightGrams = 18f, yieldWeightGrams = 36f, extractionTimeSeconds = 30)
        val brew2 = BrewEntryEntity(timestamp = 2000L, grindSize = 12f, doseWeightGrams = 20f, yieldWeightGrams = 40f, extractionTimeSeconds = 28)
        val brew3 = BrewEntryEntity(timestamp = 3000L, grindSize = 14f, doseWeightGrams = 22f, yieldWeightGrams = 44f, extractionTimeSeconds = 32)

        brewDao.insertBrew(brew1)
        brewDao.insertBrew(brew2)
        brewDao.insertBrew(brew3)

        val allBrews = brewDao.observeAllBrews().first()
        assertEquals(3, allBrews.size)
        assertTrue(allBrews[0].timestamp >= allBrews[1].timestamp, "Should be sorted DESC")
        assertTrue(allBrews[1].timestamp >= allBrews[2].timestamp, "Should be sorted DESC")
    }
}