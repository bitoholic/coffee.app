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
        val brew = BrewEntry(
            beanName = "Ethiopian Yirgacheffe",
            beanOrigin = "Ethiopia",
            roastType = "Light",
            grinderSetting = 15,
            portionWeight = 18.0,
            description = "Great fruity notes",
            createdDate = 1700000001000L,
            lastModifiedDate = 1700000001000L
        )
        brewDao.upsert(brew)

        val allBrews = brewDao.observeAllCreatedDateDesc().first()
        assertEquals(1, allBrews.size, "Should have exactly one brew")
        with(allBrews[0]) {
            assertEquals(brew.uuid, uuid)
            assertEquals("Ethiopian Yirgacheffe", beanName)
            assertEquals("Ethiopia", beanOrigin)
            assertEquals("Light", roastType)
            assertEquals(15, grinderSetting)
            assertEquals(18.0, portionWeight)
            assertEquals("Great fruity notes", description)
        }
    }

    @Test
    fun testInsertAndReadOrigin() = runBlocking {
        val origin = Origin(name = "Colombia", isCustom = false)
        originDao.insert(origin)

        val allOrigins = originDao.observeAll().first()
        assertTrue(allOrigins.any { it.name == "Colombia" })
    }

    @Test
    fun testDeleteBrewEntry() = runBlocking {
        val brew = BrewEntry(
            beanName = "Test Bean",
            roastType = "Medium",
            grinderSetting = 12,
            portionWeight = 20.0,
            createdDate = 1700000001000L,
            lastModifiedDate = 1700000001000L
        )
        brewDao.upsert(brew)

        val allBrewsBefore = brewDao.observeAllCreatedDateDesc().first()
        assertEquals(1, allBrewsBefore.size)

        brewDao.deleteByUuid(allBrewsBefore[0].uuid)

        val allBrewsAfter = brewDao.observeAllCreatedDateDesc().first()
        assertTrue(allBrewsAfter.isEmpty(), "List should be empty after delete")
    }

    @Test
    fun testBrewsOrderedByCreatedDateDesc() = runBlocking {
        val brew1 = BrewEntry(
            beanName = "Brew A",
            roastType = "Light",
            grinderSetting = 10,
            portionWeight = 18.0,
            createdDate = 1000L,
            lastModifiedDate = 1000L
        )
        val brew2 = BrewEntry(
            beanName = "Brew B",
            roastType = "Medium",
            grinderSetting = 12,
            portionWeight = 20.0,
            createdDate = 2000L,
            lastModifiedDate = 2000L
        )
        val brew3 = BrewEntry(
            beanName = "Brew C",
            roastType = "Dark",
            grinderSetting = 14,
            portionWeight = 22.0,
            createdDate = 3000L,
            lastModifiedDate = 3000L
        )

        brewDao.upsert(brew1)
        brewDao.upsert(brew2)
        brewDao.upsert(brew3)

        val allBrews = brewDao.observeAllCreatedDateDesc().first()
        assertEquals(3, allBrews.size)
        assertTrue(allBrews[0].createdDate >= allBrews[1].createdDate, "Should be sorted DESC")
        assertTrue(allBrews[1].createdDate >= allBrews[2].createdDate, "Should be sorted DESC")
    }

    @Test
    fun testOriginExistsIgnoreCase() = runBlocking {
        originDao.insert(Origin(name = "Ethiopia", isCustom = false))

        assertTrue(originDao.existsIgnoreCase("ethiopia"))
        assertTrue(originDao.existsIgnoreCase("ETHIOPIA"))
        assertTrue(originDao.existsIgnoreCase("Ethiopia"))
    }

    @Test
    fun testGetBrewEntryById() = runBlocking {
        val brew = BrewEntry(
            beanName = "Test Bean",
            roastType = "Dark",
            grinderSetting = 20,
            portionWeight = 15.0,
            createdDate = 1000L,
            lastModifiedDate = 1000L
        )
        brewDao.upsert(brew)

        val fetched = brewDao.getById(brew.uuid)
        assertNotNull(fetched)
        assertEquals("Test Bean", fetched.beanName)
    }
}
