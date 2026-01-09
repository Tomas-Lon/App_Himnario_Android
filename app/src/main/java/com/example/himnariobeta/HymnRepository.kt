package com.example.himnariobeta

import kotlinx.coroutines.flow.Flow

/**
 * Repository que centraliza el acceso a datos de la aplicación.
 * Actúa como capa de abstracción entre los ViewModels y los DAOs.
 */
class HymnRepository(
    private val hymnDao: HymnDao,
    private val listDao: HymnListDao,
    private val folderDao: FolderDao
) {
    
    // ==================== HYMNS ====================
    
    fun getAllHymns(): Flow<List<HymnEntity>> = hymnDao.getAllHymns()
    
    fun searchHymns(query: String): Flow<List<HymnEntity>> = hymnDao.searchHymns(query)
    
    fun filterHymns(category: String?, key: String?): Flow<List<HymnEntity>> = 
        hymnDao.filterHymns(category, key)
    
    suspend fun updateNote(hymnId: Int, note: String) = hymnDao.updateNote(hymnId, note)
    
    suspend fun updateHymn(hymn: HymnEntity) = hymnDao.updateHymn(hymn)
    
    // ==================== LISTS ====================
    
    fun getAllLists(): Flow<List<HymnListEntity>> = listDao.getAllLists()
    
    fun getAllListsByName(): Flow<List<HymnListEntity>> = listDao.getAllListsByName()
    
    fun getAllListsFavoritesFirst(): Flow<List<HymnListEntity>> = 
        listDao.getAllListsFavoritesFirst()
    
    suspend fun insertList(list: HymnListEntity): Long = listDao.insertList(list)
    
    suspend fun updateList(list: HymnListEntity) = listDao.updateList(list)
    
    suspend fun deleteList(list: HymnListEntity) = listDao.deleteList(list)
    
    suspend fun addHymnToList(listId: Int, hymnId: Int) {
        // Obtener la posición máxima actual y agregar al final
        val currentRefs = listDao.getCrossRefsForList(listId)
        val maxPosition = currentRefs.maxOfOrNull { it.position } ?: -1
        val newPosition = maxPosition + 1
        listDao.addHymnToList(ListHymnCrossRef(listId, hymnId, newPosition))
    }
    
    suspend fun removeHymnFromList(listId: Int, hymnId: Int) = 
        listDao.removeHymnFromList(listId, hymnId)
    
    suspend fun removeAllHymnsFromList(listId: Int) = 
        listDao.removeAllHymnsFromList(listId)
    
    fun getHymnsForList(listId: Int, searchQuery: String? = null): Flow<List<HymnEntity>> = 
        listDao.getHymnsForList(listId, searchQuery)
    
    suspend fun getCrossRefsForList(listId: Int): List<ListHymnCrossRef> = 
        listDao.getCrossRefsForList(listId)
    
    suspend fun updateCrossRef(crossRef: ListHymnCrossRef) = 
        listDao.updateCrossRef(crossRef)
    
    suspend fun getHymnIdsForList(listId: Int): List<Int> = 
        listDao.getHymnIdsForList(listId)
    
    suspend fun duplicateList(list: HymnListEntity): Long {
        val crossRefs = getCrossRefsForList(list.listId)
        val newId = insertList(
            HymnListEntity(
                name = "${list.name} (Copia)",
                description = list.description,
                folderId = list.folderId
            )
        )
        // Preservar las posiciones originales
        crossRefs.forEach { ref ->
            listDao.addHymnToList(
                ListHymnCrossRef(newId.toInt(), ref.hymnId, ref.position)
            )
        }
        return newId
    }
    
    // ==================== FOLDERS ====================
    
    fun getAllFolders(): Flow<List<FolderEntity>> = folderDao.getAllFolders()
    
    suspend fun insertFolder(folder: FolderEntity): Long = folderDao.insertFolder(folder)
    
    suspend fun updateFolder(folder: FolderEntity) = folderDao.updateFolder(folder)
    
    suspend fun deleteFolder(folder: FolderEntity) = folderDao.deleteFolder(folder)
}
