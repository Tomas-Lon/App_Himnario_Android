package com.example.himnariobeta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Opciones de ordenamiento para listas
 */
enum class SortOption {
    DATE_DESC, NAME_ASC, FAVORITES
}

/**
 * ViewModel para la gestión de listas y carpetas
 */
class ListsViewModel(
    private val repository: HymnRepository
) : ViewModel() {
    
    // Opciones de ordenamiento
    private val _sortOption = MutableStateFlow(SortOption.DATE_DESC)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()
    
    // Estados de navegación
    private val _selectedList = MutableStateFlow<HymnListEntity?>(null)
    val selectedList: StateFlow<HymnListEntity?> = _selectedList.asStateFlow()
    
    private val _selectedFolder = MutableStateFlow<FolderEntity?>(null)
    val selectedFolder: StateFlow<FolderEntity?> = _selectedFolder.asStateFlow()
    
    // Diálogos
    private val _showCreateListDialog = MutableStateFlow(false)
    val showCreateListDialog: StateFlow<Boolean> = _showCreateListDialog.asStateFlow()
    
    // Datos
    val allFolders: StateFlow<List<FolderEntity>> = repository.getAllFolders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val allLists: StateFlow<List<HymnListEntity>> = sortOption.flatMapLatest { option ->
        when (option) {
            SortOption.DATE_DESC -> repository.getAllLists()
            SortOption.NAME_ASC -> repository.getAllListsByName()
            SortOption.FAVORITES -> repository.getAllListsFavoritesFirst()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Mensajes
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    
    // Himno expandido en detalle de lista
    private val _expandedHymnId = MutableStateFlow<Int?>(null)
    val expandedHymnId: StateFlow<Int?> = _expandedHymnId.asStateFlow()
    
    // ==================== ACCIONES - NAVEGACIÓN ====================
    
    fun selectList(list: HymnListEntity?) {
        _selectedList.value = list
    }
    
    fun selectFolder(folder: FolderEntity?) {
        _selectedFolder.value = folder
    }
    
    fun goBack() {
        when {
            _selectedList.value != null -> _selectedList.value = null
            _selectedFolder.value != null -> _selectedFolder.value = null
        }
    }
    
    fun toggleHymnExpansion(hymnId: Int) {
        _expandedHymnId.value = if (_expandedHymnId.value == hymnId) null else hymnId
    }
    
    // ==================== ACCIONES - ORDENAMIENTO ====================
    
    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }
    
    // ==================== ACCIONES - LISTAS ====================
    
    fun createList(name: String, description: String?, folderId: Int? = null) {
        viewModelScope.launch {
            try {
                repository.insertList(
                    HymnListEntity(
                        name = name,
                        description = description,
                        folderId = folderId ?: _selectedFolder.value?.folderId
                    )
                )
                _snackbarMessage.value = "Lista creada"
                _showCreateListDialog.value = false
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al crear lista: ${e.message}"
            }
        }
    }
    
    fun updateList(list: HymnListEntity) {
        viewModelScope.launch {
            try {
                repository.updateList(list)
                _snackbarMessage.value = "Lista actualizada"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al actualizar lista: ${e.message}"
            }
        }
    }
    
    fun deleteList(list: HymnListEntity) {
        viewModelScope.launch {
            try {
                repository.deleteList(list)
                _snackbarMessage.value = "Lista eliminada"
                if (_selectedList.value?.listId == list.listId) {
                    _selectedList.value = null
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al eliminar lista: ${e.message}"
            }
        }
    }
    
    fun toggleListFavorite(list: HymnListEntity) {
        viewModelScope.launch {
            try {
                repository.updateList(list.copy(isFavorite = !list.isFavorite))
                _snackbarMessage.value = if (!list.isFavorite) {
                    "Marcada como favorita"
                } else {
                    "Desmarcada como favorita"
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al actualizar favorito: ${e.message}"
            }
        }
    }
    
    fun moveListToFolder(list: HymnListEntity, targetFolder: FolderEntity?) {
        viewModelScope.launch {
            try {
                repository.updateList(list.copy(folderId = targetFolder?.folderId))
                _snackbarMessage.value = "Lista movida"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al mover lista: ${e.message}"
            }
        }
    }
    
    fun addHymnToList(listId: Int, hymnId: Int) {
        viewModelScope.launch {
            try {
                repository.addHymnToList(listId, hymnId)
                _snackbarMessage.value = "Himno agregado a la lista"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al agregar himno: ${e.message}"
            }
        }
    }
    
    fun removeHymnFromList(listId: Int, hymnId: Int) {
        viewModelScope.launch {
            try {
                repository.removeHymnFromList(listId, hymnId)
                _snackbarMessage.value = "Himno eliminado de la lista"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al eliminar himno: ${e.message}"
            }
        }
    }
    
    fun removeAllHymnsFromList(listId: Int) {
        viewModelScope.launch {
            try {
                repository.removeAllHymnsFromList(listId)
                _snackbarMessage.value = "Todos los himnos eliminados"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al eliminar himnos: ${e.message}"
            }
        }
    }
    
    fun duplicateList(list: HymnListEntity) {
        viewModelScope.launch {
            try {
                repository.duplicateList(list)
                _snackbarMessage.value = "Lista duplicada"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al duplicar lista: ${e.message}"
            }
        }
    }
    
    fun updateCrossRef(crossRef: ListHymnCrossRef) {
        viewModelScope.launch {
            try {
                repository.updateCrossRef(crossRef)
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al reordenar: ${e.message}"
            }
        }
    }
    
    // ==================== ACCIONES - CARPETAS ====================
    
    fun createFolder(name: String, description: String? = null) {
        viewModelScope.launch {
            try {
                repository.insertFolder(FolderEntity(name = name, description = description))
                _snackbarMessage.value = "Carpeta \"$name\" creada"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al crear carpeta: ${e.message}"
            }
        }
    }
    
    fun updateFolder(folder: FolderEntity, name: String, description: String?) {
        viewModelScope.launch {
            try {
                repository.updateFolder(folder.copy(name = name, description = description))
                _snackbarMessage.value = "Carpeta actualizada"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al editar carpeta: ${e.message}"
            }
        }
    }
    
    fun deleteFolder(folder: FolderEntity) {
        viewModelScope.launch {
            try {
                repository.deleteFolder(folder)
                _snackbarMessage.value = "Carpeta eliminada"
                if (_selectedFolder.value?.folderId == folder.folderId) {
                    _selectedFolder.value = null
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al eliminar carpeta: ${e.message}"
            }
        }
    }
    
    // ==================== DIÁLOGOS ====================
    
    fun setShowCreateListDialog(show: Boolean) {
        _showCreateListDialog.value = show
    }
    
    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
    
    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }
}
