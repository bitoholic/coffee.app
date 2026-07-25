package coffee.app.form

import coffee.app.core.ValidationUtil
import coffee.app.data.database.BrewEntry
import coffee.app.data.database.BrewEntryDao
import coffee.app.data.database.EntryPhoto
import coffee.app.data.database.EntryPhotoDao
import coffee.app.data.database.Origin
import coffee.app.data.database.OriginDao
import coffee.app.data.repository.BrewEntryRepository
import coffee.app.data.repository.OriginRepository
import coffee.app.domain.RoastType
import coffee.app.domain.SortOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for BrewEntryFormViewModel state management and validation wiring.
 *
 * These tests use in-memory fake repositories to verify ViewModel behavior
 * without any UI or Compose dependencies — pure TDD at the ViewModel layer.
 */
class BrewEntryFormViewModelTest {

    private lateinit var viewModel: BrewEntryFormViewModel
    private lateinit var brewEntryRepository: FakeBrewEntryRepository
    private lateinit var originRepository: FakeOriginRepository

    @BeforeTest
    fun setUp() {
        brewEntryRepository = FakeBrewEntryRepository()
        originRepository = FakeOriginRepository()
        // Pre-populate with predefined origins
        runBlocking {
            originRepository.add(Origin("Brazil", isCustom = false))
            originRepository.add(Origin("Colombia", isCustom = false))
            originRepository.add(Origin("Ethiopia", isCustom = false))
        }

        viewModel = BrewEntryFormViewModel(
            brewEntryRepository = brewEntryRepository,
            originRepository = originRepository,
            entryPhotoDao = FakeEntryPhotoDao(),
            coroutineScope = CoroutineScope(Dispatchers.Default)
        )
    }

    @AfterTest
    fun tearDown() {
        // No cleanup needed for fake repos
    }

    // --- Initial state ---

    @Test
    fun `form state initializes with empty fields no errors and not saving`() {
        val state = viewModel.state.value
        assertEquals("", state.beanName)
        assertEquals("", state.beanOrigin)
        assertNull(state.roastType)
        assertEquals("", state.grinderSetting)
        assertEquals("", state.portionWeight)
        assertEquals("", state.description)
        assertTrue(state.validationErrors.isEmpty())
        assertFalse(state.isSaving)
        assertFalse(state.saveSuccess)
    }

    @Test
    fun `form state loads origins from repository on init`() = runBlocking {
        val state = viewModel.state.first { it.origins.isNotEmpty() }
        assertEquals(3, state.origins.size)
        val names = state.origins.map { it.name }
        assertTrue("Brazil" in names)
        assertTrue("Colombia" in names)
        assertTrue("Ethiopia" in names)
    }

    // --- Field updates ---

    @Test
    fun `bean name change updates state and clears errors`() {
        viewModel.onBeanNameChanged("Ethiopian Yirgacheffe")
        val state = viewModel.state.value
        assertEquals("Ethiopian Yirgacheffe", state.beanName)
        assertTrue(state.validationErrors.isEmpty())
    }

    @Test
    fun `bean origin change updates state and clears errors`() {
        viewModel.onBeanOriginChanged("Colombia")
        val state = viewModel.state.value
        assertEquals("Colombia", state.beanOrigin)
        assertTrue(state.validationErrors.isEmpty())
    }

    @Test
    fun `roast type change updates state and clears errors`() {
        viewModel.onRoastTypeChanged(RoastType.Medium)
        val state = viewModel.state.value
        assertEquals(RoastType.Medium, state.roastType)
        assertTrue(state.validationErrors.isEmpty())
    }

    @Test
    fun `grinder setting change accepts digits only`() {
        viewModel.onGrinderSettingChanged("15")
        assertEquals("15", viewModel.state.value.grinderSetting)
    }

    @Test
    fun `grinder setting change rejects non-digit input`() {
        viewModel.onGrinderSettingChanged("abc")
        assertEquals("", viewModel.state.value.grinderSetting)
    }

    @Test
    fun `grinder setting change rejects negative sign`() {
        viewModel.onGrinderSettingChanged("-")
        assertEquals("", viewModel.state.value.grinderSetting)
    }

    @Test
    fun `portion weight change accepts valid decimal`() {
        viewModel.onPortionWeightChanged("18")
        assertEquals("18", viewModel.state.value.portionWeight)
    }

    @Test
    fun `portion weight change accepts decimal point`() {
        viewModel.onPortionWeightChanged("18.5")
        assertEquals("18.5", viewModel.state.value.portionWeight)
    }

    @Test
    fun `portion weight change rejects letters`() {
        viewModel.onPortionWeightChanged("abc")
        assertEquals("", viewModel.state.value.portionWeight)
    }

    @Test
    fun `description change updates state and clears errors`() {
        viewModel.onDescriptionChanged("Great fruity notes with chocolate finish")
        val state = viewModel.state.value
        assertEquals("Great fruity notes with chocolate finish", state.description)
        assertTrue(state.validationErrors.isEmpty())
    }

    // --- Save validation ---

    @Test
    fun `save with empty bean name shows validation error`() {
        viewModel.save()
        val state = viewModel.state.value
        assertNotNull(state.validationErrors["beanName"])
        assertFalse(state.saveSuccess)
    }

    @Test
    fun `save with whitespace bean name shows validation error`() {
        viewModel.onBeanNameChanged("   ")
        viewModel.save()
        val state = viewModel.state.value
        assertNotNull(state.validationErrors["beanName"])
        assertFalse(state.saveSuccess)
    }

    @Test
    fun `save with invalid grinder setting shows validation error`() {
        fillValidFields()
        viewModel.onGrinderSettingChanged("0")
        viewModel.save()
        val state = viewModel.state.value
        assertNotNull(state.validationErrors["grinderSetting"])
    }

    @Test
    fun `save with grinder setting above 48 shows validation error`() {
        fillValidFields()
        viewModel.onGrinderSettingChanged("49")
        viewModel.save()
        val state = viewModel.state.value
        assertNotNull(state.validationErrors["grinderSetting"])
    }

    @Test
    fun `save with non-numeric grinder setting shows validation error`() {
        fillValidFields()
        viewModel.onGrinderSettingChanged("")
        viewModel.save()
        val state = viewModel.state.value
        assertNotNull(state.validationErrors["grinderSetting"])
    }

    @Test
    fun `save with zero portion weight shows validation error`() {
        fillValidFields()
        viewModel.onPortionWeightChanged("0")
        viewModel.save()
        val state = viewModel.state.value
        assertNotNull(state.validationErrors["portionWeight"])
    }

    @Test
    fun `save with invalid roast type shows validation error`() {
        fillValidFields()
        viewModel.onRoastTypeChanged(null)
        viewModel.save()
        val state = viewModel.state.value
        assertNotNull(state.validationErrors["roastType"])
    }

    @Test
    fun `save with description over 500 chars shows validation error`() {
        fillValidFields()
        viewModel.onDescriptionChanged("A".repeat(501))
        viewModel.save()
        val state = viewModel.state.value
        assertNotNull(state.validationErrors["description"])
    }

    @Test
    fun `save with description exactly 500 chars succeeds`() = runBlocking {
        fillValidFields()
        viewModel.onDescriptionChanged("A".repeat(500))
        viewModel.save()
        val state = viewModel.state.first { it.saveSuccess || it.validationErrors.isNotEmpty() }
        assertTrue(state.saveSuccess, "Expected save to succeed, got errors: ${state.validationErrors}")
    }

    @Test
    fun `save with null description succeeds`() = runBlocking {
        fillValidFields()
        viewModel.save()
        val state = viewModel.state.first { it.saveSuccess || it.validationErrors.isNotEmpty() }
        assertTrue(state.saveSuccess, "Expected save to succeed, got errors: ${state.validationErrors}")
    }

    @Test
    fun `save with all valid fields persists entry and sets saveSuccess`() = runBlocking {
        fillValidFields()
        viewModel.save()

        val state = viewModel.state.first { it.saveSuccess || it.validationErrors.isNotEmpty() }
        assertTrue(state.saveSuccess, "Expected save success, got errors: ${state.validationErrors}")

        val entries = brewEntryRepository.getAll().first()
        assertEquals(1, entries.size)
        with(entries[0]) {
            assertEquals("Test Bean", beanName)
            assertEquals("Colombia", beanOrigin)
            assertEquals("Medium", roastType)
            assertEquals(15, grinderSetting)
            assertEquals(18.0, portionWeight)
            assertEquals("Nice brew", description)
        }
    }

    @Test
    fun `save with valid fields no origin no description persists correctly`() = runBlocking {
        viewModel.onBeanNameChanged("Single Origin")
        viewModel.onRoastTypeChanged(RoastType.Light)
        viewModel.onGrinderSettingChanged("12")
        viewModel.onPortionWeightChanged("20")
        viewModel.save()

        val state = viewModel.state.first { it.saveSuccess || it.validationErrors.isNotEmpty() }
        assertTrue(state.saveSuccess, "Expected save success, got errors: ${state.validationErrors}")

        val entries = brewEntryRepository.getAll().first()
        assertEquals(1, entries.size)
        assertNull(entries[0].beanOrigin)
        assertNull(entries[0].description)
    }

    @Test
    fun `save trims bean name whitespace`() = runBlocking {
        viewModel.onBeanNameChanged("  Test Bean  ")
        viewModel.onRoastTypeChanged(RoastType.Dark)
        viewModel.onGrinderSettingChanged("20")
        viewModel.onPortionWeightChanged("15")
        viewModel.save()

        val state = viewModel.state.first { it.saveSuccess || it.validationErrors.isNotEmpty() }
        assertTrue(state.saveSuccess, "Expected save success, got errors: ${state.validationErrors}")

        val entry = brewEntryRepository.getAll().first().first()
        assertEquals("Test Bean", entry.beanName)
    }

    @Test
    fun `save sets createdDate and lastModifiedDate`() = runBlocking {
        fillValidFields()
        viewModel.save()

        val state = viewModel.state.first { it.saveSuccess }
        assertTrue(state.saveSuccess)

        val entry = brewEntryRepository.getAll().first().first()
        assertTrue(entry.createdDate > 0, "createdDate should be set")
        assertEquals(entry.createdDate, entry.lastModifiedDate)
    }

    @Test
    fun `resetSaveSuccess clears the save success flag`() {
        viewModel.resetSaveSuccess()
        assertFalse(viewModel.state.value.saveSuccess)
    }

    @Test
    fun `multiple validation errors shown simultaneously when all fields empty`() {
        viewModel.save()
        val state = viewModel.state.value
        assertNotNull(state.validationErrors["beanName"])
        assertNotNull(state.validationErrors["roastType"])
        assertNotNull(state.validationErrors["grinderSetting"])
        assertNotNull(state.validationErrors["portionWeight"])
        assertEquals(4, state.validationErrors.size)
    }

    @Test
    fun `changing any field clears validation errors`() {
        viewModel.save()
        assertTrue(viewModel.state.value.validationErrors.isNotEmpty())

        viewModel.onBeanNameChanged("New Bean")
        assertTrue(viewModel.state.value.validationErrors.isEmpty())
    }

    @Test
    fun `saveSuccess resets when field changes`() = runBlocking {
        fillValidFields()
        viewModel.save()
        val state = viewModel.state.first { it.saveSuccess }
        assertTrue(state.saveSuccess)

        viewModel.onBeanNameChanged("Changed")
        assertFalse(viewModel.state.value.saveSuccess)
    }

    @Test
    fun `description over 500 chars is rejected by validation util`() {
        val longDesc = "A".repeat(501)
        val error = ValidationUtil.validateDescription(longDesc)
        assertNotNull(error)
    }

    @Test
    fun `grinder setting 1 is valid`() {
        assertNull(ValidationUtil.validateGrinderSetting(1))
    }

    @Test
    fun `grinder setting 48 is valid`() {
        assertNull(ValidationUtil.validateGrinderSetting(48))
    }

    // --- Helper ---

    private fun fillValidFields() {
        viewModel.onBeanNameChanged("Test Bean")
        viewModel.onBeanOriginChanged("Colombia")
        viewModel.onRoastTypeChanged(RoastType.Medium)
        viewModel.onGrinderSettingChanged("15")
        viewModel.onPortionWeightChanged("18.0")
        viewModel.onDescriptionChanged("Nice brew")
    }
}

// ---------------------------------------------------------------------------
// Fake repository implementations for testing
// ---------------------------------------------------------------------------

class FakeBrewEntryRepository : BrewEntryRepository {
    constructor() : super(FakeBrewEntryDao())

    private val store = mutableListOf<BrewEntry>()
    private val _allEntries = MutableStateFlow<List<BrewEntry>>(emptyList())

    override suspend fun add(entry: BrewEntry) {
        store.add(entry)
        _allEntries.value = store.toList()
    }

    override suspend fun update(entry: BrewEntry) {
        val idx = store.indexOfFirst { it.uuid == entry.uuid }
        if (idx >= 0) {
            store[idx] = entry
            _allEntries.value = store.toList()
        }
    }

    override suspend fun delete(uuid: String) {
        store.removeAll { it.uuid == uuid }
        _allEntries.value = store.toList()
    }

    override suspend fun getById(uuid: String): BrewEntry? = store.find { it.uuid == uuid }

    override fun getAll(sort: SortOption): Flow<List<BrewEntry>> = _allEntries
}

class FakeOriginRepository : OriginRepository {
    constructor() : super(FakeOriginDao())

    private val store = mutableListOf<Origin>()
    private val _allOrigins = MutableStateFlow<List<Origin>>(emptyList())

    override suspend fun add(origin: Origin) {
        store.add(origin)
        _allOrigins.value = store.toList()
    }

    override suspend fun delete(name: String) {
        store.removeAll { it.name == name }
        _allOrigins.value = store.toList()
    }

    override fun getAll(): Flow<List<Origin>> = _allOrigins

    override suspend fun existsIgnoreCase(name: String): Boolean =
        store.any { it.name.equals(name, ignoreCase = true) }
}

// ---------------------------------------------------------------------------
// Minimal fake DAO stubs (never actually called by fakes, but needed for
// the super constructor)
// ---------------------------------------------------------------------------

class FakeBrewEntryDao : BrewEntryDao {
    override suspend fun upsert(entry: BrewEntry) {}
    override suspend fun deleteByUuid(uuid: String) {}
    override fun observeAllCreatedDateDesc(): Flow<List<BrewEntry>> = MutableStateFlow(emptyList())
    override fun observeAllBeanNameAZ(): Flow<List<BrewEntry>> = MutableStateFlow(emptyList())
    override fun observeAllBeanNameDesc(): Flow<List<BrewEntry>> = MutableStateFlow(emptyList())
    override fun observeAllOriginAZ(): Flow<List<BrewEntry>> = MutableStateFlow(emptyList())
    override fun observeAllOriginDesc(): Flow<List<BrewEntry>> = MutableStateFlow(emptyList())
    override fun observeAllCreatedDate(): Flow<List<BrewEntry>> = MutableStateFlow(emptyList())
    override fun observeAllLastModifiedDate(): Flow<List<BrewEntry>> = MutableStateFlow(emptyList())
    override fun observeAllLastModifiedDateAsc(): Flow<List<BrewEntry>> = MutableStateFlow(emptyList())
    override suspend fun getById(uuid: String): BrewEntry? = null
}

class FakeOriginDao : OriginDao {
    override suspend fun insert(origin: Origin) {}
    override suspend fun deleteByName(name: String) {}
    override fun observeAll(): Flow<List<Origin>> = MutableStateFlow(emptyList())
    override suspend fun existsIgnoreCase(name: String): Boolean = false
}

class FakeEntryPhotoDao : EntryPhotoDao {
    override fun getPhotosForEntry(entryUuid: String): Flow<List<EntryPhoto>> = MutableStateFlow(emptyList())
    override suspend fun insert(photo: EntryPhoto) {}
    override suspend fun insertAll(photos: List<EntryPhoto>) {}
    override suspend fun deleteById(id: Int) {}
    override suspend fun deleteByEntryUuid(entryUuid: String) {}
    override suspend fun nextSortOrder(entryUuid: String): Int = 1
}
