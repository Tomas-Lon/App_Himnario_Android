package com.example.himnariobeta

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Tests unitarios para HymnRepository
 */
@ExperimentalCoroutinesApi
class HymnRepositoryTest {
    
    @Mock
    private lateinit var mockHymnDao: HymnDao
    
    @Mock
    private lateinit var mockListDao: HymnListDao
    
    @Mock
    private lateinit var mockFolderDao: FolderDao
    
    private lateinit var repository: HymnRepository
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = HymnRepository(mockHymnDao, mockListDao, mockFolderDao)
    }
    
    // ==================== HYMNS TESTS ====================
    
    @Test
    fun `getAllHymns returns flow from DAO`() = runTest {
        // Given: Mock hymns from DAO
        val mockHymns = listOf(
            HymnEntity(1, "Himno 1", "Letra 1", null, 1, null, null),
            HymnEntity(2, "Himno 2", "Letra 2", null, 2, null, null)
        )
        `when`(mockHymnDao.getAllHymns()).thenReturn(flowOf(mockHymns))
        
        // When: Getting all hymns
        val result = repository.getAllHymns().first()
        
        // Then: Should return mock hymns
        assertEquals(mockHymns, result)
    }
    
    @Test
    fun `searchHymns returns filtered hymns`() = runTest {
        // Given: Search query and mock results
        val query = "test"
        val mockResults = listOf(HymnEntity(1, "Test Hymn", "Letra", null, 1, null, null))
        `when`(mockHymnDao.searchHymns(query)).thenReturn(flowOf(mockResults))
        
        // When: Searching hymns
        val result = repository.searchHymns(query).first()
        
        // Then: Should return filtered results
        assertEquals(mockResults, result)
    }
    
    @Test
    fun `filterHymns calls DAO with correct parameters`() = runTest {
        // Given: Filter parameters
        val category = "Adoración"
        val key = "C"
        `when`(mockHymnDao.filterHymns(category, key)).thenReturn(flowOf(emptyList()))
        
        // When: Filtering hymns
        repository.filterHymns(category, key).first()
        
        // Then: DAO should be called with correct parameters
        verify(mockHymnDao).filterHymns(category, key)
    }
    
    @Test
    fun `updateNote calls DAO`() = runTest {
        // Given: Hymn ID and note
        val hymnId = 123
        val note = "Test note"
        
        // When: Updating note
        repository.updateNote(hymnId, note)
        
        // Then: DAO should be called
        verify(mockHymnDao).updateNote(hymnId, note)
    }
    
    // ==================== LISTS TESTS ====================
    
    @Test
    fun `getAllLists returns flow from DAO`() = runTest {
        // Given: Mock lists
        val mockLists = listOf(HymnListEntity(1, "List 1"))
        `when`(mockListDao.getAllLists()).thenReturn(flowOf(mockLists))
        
        // When: Getting all lists
        val result = repository.getAllLists().first()
        
        // Then: Should return mock lists
        assertEquals(mockLists, result)
    }
    
    @Test
    fun `insertList returns ID from DAO`() = runTest {
        // Given: A list entity
        val list = HymnListEntity(0, "New List")
        val expectedId = 5L
        `when`(mockListDao.insertList(list)).thenReturn(expectedId)
        
        // When: Inserting list
        val result = repository.insertList(list)
        
        // Then: Should return the ID
        assertEquals(expectedId, result)
    }
    
    @Test
    fun `deleteList calls DAO`() = runTest {
        // Given: A list to delete
        val list = HymnListEntity(1, "Test List")
        
        // When: Deleting list
        repository.deleteList(list)
        
        // Then: DAO should be called
        verify(mockListDao).deleteList(list)
    }
    
    @Test
    fun `addHymnToList creates correct CrossRef`() = runTest {
        // Given: List and hymn IDs
        val listId = 1
        val hymnId = 123
        
        // When: Adding hymn to list
        repository.addHymnToList(listId, hymnId)
        
        // Then: DAO should be called with correct CrossRef
        verify(mockListDao).addHymnToList(argThat { crossRef ->
            crossRef.listId == listId && crossRef.hymnId == hymnId
        })
    }
    
    @Test
    fun `removeHymnFromList calls DAO with correct parameters`() = runTest {
        // Given: List and hymn IDs
        val listId = 1
        val hymnId = 123
        
        // When: Removing hymn from list
        repository.removeHymnFromList(listId, hymnId)
        
        // Then: DAO should be called
        verify(mockListDao).removeHymnFromList(listId, hymnId)
    }
    
    @Test
    fun `duplicateList creates copy with correct name`() = runTest {
        // Given: Original list
        val originalList = HymnListEntity(1, "Original List", "Description", false, 0L, null)
        val hymnIds = listOf(10, 20, 30)
        val newListId = 2L
        
        `when`(mockListDao.getHymnIdsForList(originalList.listId)).thenReturn(hymnIds)
        `when`(mockListDao.insertList(any())).thenReturn(newListId)
        
        // When: Duplicating list
        val result = repository.duplicateList(originalList)
        
        // Then: Should create list with " (Copia)" suffix
        verify(mockListDao).insertList(argThat { list ->
            list.name == "${originalList.name} (Copia)" &&
            list.description == originalList.description &&
            list.folderId == originalList.folderId
        })
        
        // And: Should add all hymns to new list
        hymnIds.forEach { hymnId ->
            verify(mockListDao).addHymnToList(argThat { crossRef ->
                crossRef.listId == newListId.toInt() && crossRef.hymnId == hymnId
            })
        }
        
        assertEquals(newListId, result)
    }
    
    // ==================== FOLDERS TESTS ====================
    
    @Test
    fun `getAllFolders returns flow from DAO`() = runTest {
        // Given: Mock folders
        val mockFolders = listOf(FolderEntity(1, "Folder 1"))
        `when`(mockFolderDao.getAllFolders()).thenReturn(flowOf(mockFolders))
        
        // When: Getting all folders
        val result = repository.getAllFolders().first()
        
        // Then: Should return mock folders
        assertEquals(mockFolders, result)
    }
    
    @Test
    fun `insertFolder calls DAO and returns ID`() = runTest {
        // Given: A folder
        val folder = FolderEntity(0, "New Folder")
        val expectedId = 3L
        `when`(mockFolderDao.insertFolder(folder)).thenReturn(expectedId)
        
        // When: Inserting folder
        val result = repository.insertFolder(folder)
        
        // Then: Should return the ID
        assertEquals(expectedId, result)
    }
    
    @Test
    fun `updateFolder calls DAO`() = runTest {
        // Given: A folder to update
        val folder = FolderEntity(1, "Updated Folder")
        
        // When: Updating folder
        repository.updateFolder(folder)
        
        // Then: DAO should be called
        verify(mockFolderDao).updateFolder(folder)
    }
    
    @Test
    fun `deleteFolder calls DAO`() = runTest {
        // Given: A folder to delete
        val folder = FolderEntity(1, "Test Folder")
        
        // When: Deleting folder
        repository.deleteFolder(folder)
        
        // Then: DAO should be called
        verify(mockFolderDao).deleteFolder(folder)
    }
}
