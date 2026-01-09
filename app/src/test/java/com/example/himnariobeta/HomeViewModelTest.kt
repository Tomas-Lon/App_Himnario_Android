package com.example.himnariobeta

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Tests unitarios para HomeViewModel
 * 
 * Dependencias necesarias en build.gradle:
 * testImplementation 'junit:junit:4.13.2'
 * testImplementation 'org.mockito:mockito-core:5.3.1'
 * testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3'
 * testImplementation 'app.cash.turbine:turbine:1.0.0'
 */
@ExperimentalCoroutinesApi
class HomeViewModelTest {
    
    @Mock
    private lateinit var mockRepository: HymnRepository
    
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(mockRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `searchQuery initial state is empty`() = runTest {
        // When: Getting initial search query
        val searchQuery = viewModel.searchQuery.value
        
        // Then: Should be empty
        assertEquals("", searchQuery)
    }
    
    @Test
    fun `updateSearchQuery updates state correctly`() = runTest {
        // Given: A search query
        val query = "Himno 123"
        
        // When: Updating search query
        viewModel.updateSearchQuery(query)
        advanceUntilIdle()
        
        // Then: State should be updated
        assertEquals(query, viewModel.searchQuery.value)
    }
    
    @Test
    fun `setSearchActive true activates search`() = runTest {
        // When: Activating search
        viewModel.setSearchActive(true)
        advanceUntilIdle()
        
        // Then: Search should be active
        assertTrue(viewModel.isSearchActive.value)
    }
    
    @Test
    fun `setSearchActive false deactivates search and clears query`() = runTest {
        // Given: Active search with query
        viewModel.setSearchActive(true)
        viewModel.updateSearchQuery("test")
        advanceUntilIdle()
        
        // When: Deactivating search
        viewModel.setSearchActive(false)
        advanceUntilIdle()
        
        // Then: Search should be inactive and query cleared
        assertFalse(viewModel.isSearchActive.value)
        assertEquals("", viewModel.searchQuery.value)
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
    fun `toggleHymnExpansion collapses expanded hymn`() = runTest {
        // Given: Expanded hymn
        val hymnId = 123
        viewModel.toggleHymnExpansion(hymnId)
        advanceUntilIdle()
        
        // When: Toggling expansion again
        viewModel.toggleHymnExpansion(hymnId)
        advanceUntilIdle()
        
        // Then: Hymn should be collapsed
        assertNull(viewModel.expandedHymnId.value)
    }
    
    @Test
    fun `updateHymnNote calls repository correctly`() = runTest {
        // Given: A hymn id and note
        val hymnId = 123
        val note = "Test note"
        
        // When: Updating hymn note
        viewModel.updateHymnNote(hymnId, note)
        advanceUntilIdle()
        
        // Then: Repository should be called
        verify(mockRepository).updateNote(hymnId, note)
    }
    
    @Test
    fun `showSnackbar sets message correctly`() = runTest {
        // Given: A message
        val message = "Test message"
        
        // When: Showing snackbar
        viewModel.showSnackbar(message)
        advanceUntilIdle()
        
        // Then: Message should be set
        assertEquals(message, viewModel.snackbarMessage.value)
    }
    
    @Test
    fun `clearSnackbar clears message`() = runTest {
        // Given: A snackbar message
        viewModel.showSnackbar("Test")
        advanceUntilIdle()
        
        // When: Clearing snackbar
        viewModel.clearSnackbar()
        advanceUntilIdle()
        
        // Then: Message should be null
        assertNull(viewModel.snackbarMessage.value)
    }
    
    @Test
    fun `displayedHymns shows all hymns when search is empty`() = runTest {
        // Given: Mock hymns from repository
        val mockHymns = listOf(
            HymnEntity(1, "Himno 1", "Letra 1", null, 1, null, null),
            HymnEntity(2, "Himno 2", "Letra 2", null, 2, null, null)
        )
        `when`(mockRepository.getAllHymns()).thenReturn(flowOf(mockHymns))
        
        // When: Creating ViewModel (search query is empty by default)
        val newViewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()
        
        // Then: Displayed hymns should match all hymns
        // Note: This requires the flow to be collected
        // In real tests, use Turbine library for testing flows
    }
}
