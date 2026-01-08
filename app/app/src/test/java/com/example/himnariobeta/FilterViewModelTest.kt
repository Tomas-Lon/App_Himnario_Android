package com.example.himnariobeta

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Tests unitarios para FilterViewModel
 */
@ExperimentalCoroutinesApi
class FilterViewModelTest {
    
    @Mock
    private lateinit var mockRepository: HymnRepository
    
    private lateinit var viewModel: FilterViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = FilterViewModel(mockRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `selectedCategory initial state is null`() = runTest {
        // When: Getting initial category
        val category = viewModel.selectedCategory.value
        
        // Then: Should be null
        assertNull(category)
    }
    
    @Test
    fun `setCategory updates state correctly`() = runTest {
        // Given: A category
        val category = "Adoración"
        
        // When: Setting category
        viewModel.setCategory(category)
        advanceUntilIdle()
        
        // Then: State should be updated
        assertEquals(category, viewModel.selectedCategory.value)
    }
    
    @Test
    fun `clearCategory sets category to null`() = runTest {
        // Given: Selected category
        viewModel.setCategory("Adoración")
        advanceUntilIdle()
        
        // When: Clearing category
        viewModel.clearCategory()
        advanceUntilIdle()
        
        // Then: Category should be null
        assertNull(viewModel.selectedCategory.value)
    }
    
    @Test
    fun `setKey updates state correctly`() = runTest {
        // Given: A musical key
        val key = "C"
        
        // When: Setting key
        viewModel.setKey(key)
        advanceUntilIdle()
        
        // Then: State should be updated
        assertEquals(key, viewModel.selectedKey.value)
    }
    
    @Test
    fun `clearKey sets key to null`() = runTest {
        // Given: Selected key
        viewModel.setKey("C")
        advanceUntilIdle()
        
        // When: Clearing key
        viewModel.clearKey()
        advanceUntilIdle()
        
        // Then: Key should be null
        assertNull(viewModel.selectedKey.value)
    }
    
    @Test
    fun `toggleCategoryMenu changes menu state`() = runTest {
        // Given: Closed menu
        assertFalse(viewModel.categoryMenuExpanded.value)
        
        // When: Toggling menu
        viewModel.toggleCategoryMenu()
        advanceUntilIdle()
        
        // Then: Menu should be open
        assertTrue(viewModel.categoryMenuExpanded.value)
    }
    
    @Test
    fun `closeCategoryMenu closes the menu`() = runTest {
        // Given: Open menu
        viewModel.toggleCategoryMenu()
        advanceUntilIdle()
        
        // When: Closing menu
        viewModel.closeCategoryMenu()
        advanceUntilIdle()
        
        // Then: Menu should be closed
        assertFalse(viewModel.categoryMenuExpanded.value)
    }
    
    @Test
    fun `toggleKeyMenu changes menu state`() = runTest {
        // Given: Closed menu
        assertFalse(viewModel.keyMenuExpanded.value)
        
        // When: Toggling menu
        viewModel.toggleKeyMenu()
        advanceUntilIdle()
        
        // Then: Menu should be open
        assertTrue(viewModel.keyMenuExpanded.value)
    }
    
    @Test
    fun `categories list contains expected values`() {
        // Then: Categories should contain all expected values
        val expectedCategories = listOf("Adoración", "Alabanza", "Himno", "Cántico")
        assertEquals(expectedCategories, viewModel.categories)
    }
    
    @Test
    fun `musicalKeys list contains 12 keys`() {
        // Then: Should have 12 musical keys
        assertEquals(12, viewModel.musicalKeys.size)
        assertTrue(viewModel.musicalKeys.contains("C"))
        assertTrue(viewModel.musicalKeys.contains("C#"))
        assertTrue(viewModel.musicalKeys.contains("B"))
    }
    
    @Test
    fun `toggleHymnExpansion expands collapsed hymn`() = runTest {
        // Given: No expanded hymn
        val hymnId = 123
        
        // When: Toggling expansion
        viewModel.toggleHymnExpansion(hymnId)
        advanceUntilIdle()
        
        // Then: Hymn should be expanded
        assertEquals(hymnId, viewModel.expandedHymnId.value)
    }
    
    @Test
    fun `updateHymnNote calls repository`() = runTest {
        // Given: Hymn ID and note
        val hymnId = 123
        val note = "Test note"
        
        // When: Updating note
        viewModel.updateHymnNote(hymnId, note)
        advanceUntilIdle()
        
        // Then: Repository should be called
        verify(mockRepository).updateNote(hymnId, note)
    }
}
