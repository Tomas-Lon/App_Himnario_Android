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
 * Tests unitarios para ListsViewModel
 */
@ExperimentalCoroutinesApi
class ListsViewModelTest {
    
    @Mock
    private lateinit var mockRepository: HymnRepository
    
    private lateinit var viewModel: ListsViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ListsViewModel(mockRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `sortOption initial state is DATE_DESC`() = runTest {
        // When: Getting initial sort option
        val sortOption = viewModel.sortOption.value
        
        // Then: Should be DATE_DESC
        assertEquals(SortOption.DATE_DESC, sortOption)
    }
    
    @Test
    fun `setSortOption updates state correctly`() = runTest {
        // When: Setting sort option to NAME_ASC
        viewModel.setSortOption(SortOption.NAME_ASC)
        advanceUntilIdle()
        
        // Then: State should be updated
        assertEquals(SortOption.NAME_ASC, viewModel.sortOption.value)
    }
    
    @Test
    fun `selectList sets selected list`() = runTest {
        // Given: A list entity
        val list = HymnListEntity(1, "Test List", "Description")
        
        // When: Selecting list
        viewModel.selectList(list)
        advanceUntilIdle()
        
        // Then: Selected list should be set
        assertEquals(list, viewModel.selectedList.value)
    }
    
    @Test
    fun `selectFolder sets selected folder`() = runTest {
        // Given: A folder entity
        val folder = FolderEntity(1, "Test Folder", "Description")
        
        // When: Selecting folder
        viewModel.selectFolder(folder)
        advanceUntilIdle()
        
        // Then: Selected folder should be set
        assertEquals(folder, viewModel.selectedFolder.value)
    }
    
    @Test
    fun `createList calls repository with correct parameters`() = runTest {
        // Given: List data
        val name = "New List"
        val description = "Test Description"
        `when`(mockRepository.insertList(any())).thenReturn(1L)
        
        // When: Creating list
        viewModel.createList(name, description)
        advanceUntilIdle()
        
        // Then: Repository should be called with correct list
        verify(mockRepository).insertList(argThat { list ->
            list.name == name && list.description == description
        })
    }
    
    @Test
    fun `deleteList calls repository and clears selection if deleted`() = runTest {
        // Given: Selected list
        val list = HymnListEntity(1, "Test List")
        viewModel.selectList(list)
        advanceUntilIdle()
        
        // When: Deleting the selected list
        viewModel.deleteList(list)
        advanceUntilIdle()
        
        // Then: Repository should be called and selection cleared
        verify(mockRepository).deleteList(list)
        assertNull(viewModel.selectedList.value)
    }
    
    @Test
    fun `toggleListFavorite updates favorite status`() = runTest {
        // Given: A non-favorite list
        val list = HymnListEntity(1, "Test List", isFavorite = false)
        
        // When: Toggling favorite
        viewModel.toggleListFavorite(list)
        advanceUntilIdle()
        
        // Then: Repository should be called with favorited list
        verify(mockRepository).updateList(argThat { it.isFavorite })
    }
    
    @Test
    fun `createFolder calls repository`() = runTest {
        // Given: Folder name
        val folderName = "New Folder"
        `when`(mockRepository.insertFolder(any())).thenReturn(1L)
        
        // When: Creating folder
        viewModel.createFolder(folderName)
        advanceUntilIdle()
        
        // Then: Repository should be called
        verify(mockRepository).insertFolder(argThat { it.name == folderName })
    }
    
    @Test
    fun `updateFolder calls repository with updated data`() = runTest {
        // Given: Folder and new data
        val folder = FolderEntity(1, "Old Name")
        val newName = "New Name"
        val newDescription = "New Description"
        
        // When: Updating folder
        viewModel.updateFolder(folder, newName, newDescription)
        advanceUntilIdle()
        
        // Then: Repository should be called with updated folder
        verify(mockRepository).updateFolder(argThat { 
            it.folderId == folder.folderId && it.name == newName && it.description == newDescription
        })
    }
    
    @Test
    fun `deleteFolder clears selection if deleted folder is selected`() = runTest {
        // Given: Selected folder
        val folder = FolderEntity(1, "Test Folder")
        viewModel.selectFolder(folder)
        advanceUntilIdle()
        
        // When: Deleting the selected folder
        viewModel.deleteFolder(folder)
        advanceUntilIdle()
        
        // Then: Selection should be cleared
        assertNull(viewModel.selectedFolder.value)
    }
    
    @Test
    fun `addHymnToList calls repository correctly`() = runTest {
        // Given: List and hymn IDs
        val listId = 1
        val hymnId = 123
        
        // When: Adding hymn to list
        viewModel.addHymnToList(listId, hymnId)
        advanceUntilIdle()
        
        // Then: Repository should be called
        verify(mockRepository).addHymnToList(listId, hymnId)
    }
    
    @Test
    fun `moveListToFolder updates list with folder ID`() = runTest {
        // Given: List and target folder
        val list = HymnListEntity(1, "Test List", folderId = null)
        val targetFolder = FolderEntity(2, "Target Folder")
        
        // When: Moving list to folder
        viewModel.moveListToFolder(list, targetFolder)
        advanceUntilIdle()
        
        // Then: Repository should be called with updated list
        verify(mockRepository).updateList(argThat { it.folderId == targetFolder.folderId })
    }
}
