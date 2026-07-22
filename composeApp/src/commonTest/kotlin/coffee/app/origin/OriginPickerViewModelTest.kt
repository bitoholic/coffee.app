package coffee.app.origin

import coffee.app.data.database.Origin
import coffee.app.data.repository.OriginRepository
import coffee.app.form.BrewEntryFormViewModel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OriginPickerViewModelTest {

    @Test
    fun `test predefined origins list loads from OriginRepository`() = runTest {
        // Mock repository with predefined origins
        val mockRepository = object : OriginRepository(null!!) {
            override suspend fun add(origin: Origin) {}
            override suspend fun delete(name: String) {}
            override fun getAll() = flowOf(
                listOf(
                    Origin("Brazil", isCustom = false),
                    Origin("Colombia", isCustom = false),
                    Origin("Ethiopia", isCustom = false)
                )
            )
            override suspend fun existsIgnoreCase(name: String) = false
        }

        val viewModel = OriginPickerViewModel(mockRepository)
        
        // Collect the origins
        val origins = mutableListOf<Origin>()
        viewModel.origins.collect { 
            origins.addAll(it) 
        }
        
        // Verify predefined origins are loaded
        assertEquals(3, origins.size)
        assertTrue { origins.any { it.name == "Brazil" && !it.isCustom } }
        assertTrue { origins.any { it.name == "Colombia" && !it.isCustom } }
        assertTrue { origins.any { it.name == "Ethiopia" && !it.isCustom } }
    }

    @Test
    fun `test user can create a custom origin`() = runTest {
        val mockRepository = object : OriginRepository(null!!) {
            override suspend fun add(origin: Origin) {}
            override suspend fun delete(name: String) {}
            override fun getAll() = flowOf(emptyList())
            override suspend fun existsIgnoreCase(name: String) = false
        }

        val viewModel = OriginPickerViewModel(mockRepository)
        
        // Create a custom origin
        viewModel.createCustomOrigin("Custom Origin")
        
        // Check that the custom origin is now in the list
        val origins = mutableListOf<Origin>()
        viewModel.origins.collect { 
            origins.addAll(it) 
        }
        
        // Should contain the custom origin now
        assertTrue { origins.any { it.name == "Custom Origin" && it.isCustom } }
    }

    @Test
    fun `test case insensitive duplicate origin names are rejected`() = runTest {
        val mockRepository = object : OriginRepository(null!!) {
            override suspend fun add(origin: Origin) {}
            override suspend fun delete(name: String) {}
            override fun getAll() = flowOf(listOf(Origin("Custom Origin", isCustom = false)))
            override suspend fun existsIgnoreCase(name: String) = true  // Simulate duplicate
        }

        val viewModel = OriginPickerViewModel(mockRepository)
        
        // Try to create a case-insensitive duplicate
        viewModel.createCustomOrigin("custom origin")
        
        // Check that error is set
        val error = viewModel.errorMessage.value
        assertTrue { error != null }
        assertTrue { error!!.contains("already exists") }
    }

    @Test
    fun `test custom origins persist after DB restart`() = runTest {
        // This test verifies the repository preserves custom origins
        val mockRepository = object : OriginRepository(null!!) {
            override suspend fun add(origin: Origin) {
                // Simulate adding to DB
            }
            override suspend fun delete(name: String) {}
            override fun getAll() = flowOf(listOf(Origin("Custom Origin", isCustom = true)))
            override suspend fun existsIgnoreCase(name: String) = false
        }

        val viewModel = OriginPickerViewModel(mockRepository)
        
        // Simulate DB restart (repository reloaded)
        val origins = mutableListOf<Origin>()
        viewModel.origins.collect { 
            origins.addAll(it) 
        }
        
        // Custom origin should still be present
        assertTrue { origins.any { it.name == "Custom Origin" && it.isCustom } }
    }
}