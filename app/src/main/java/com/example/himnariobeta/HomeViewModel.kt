package com.example.himnariobeta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla principal de himnos
 */
class HomeViewModel(
    private val repository: HymnRepository
) : ViewModel() {
    
    // Estado de búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()
    
    // Himno expandido
    private val _expandedHymnId = MutableStateFlow<Int?>(null)
    val expandedHymnId: StateFlow<Int?> = _expandedHymnId.asStateFlow()
    
    // Listas de himnos
    val allHymns: StateFlow<List<HymnEntity>> = repository.getAllHymns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val displayedHymns: StateFlow<List<HymnEntity>> = combine(
        searchQuery,
        allHymns
    ) { query, hymns ->
        if (query.isEmpty()) hymns
        else hymns.filter { hymn ->
            hymn.title?.contains(query, ignoreCase = true) == true ||
            hymn.lyrics?.contains(query, ignoreCase = true) == true ||
            hymn.id.toString().contains(query)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Mensajes Snackbar
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    
    // ==================== ACCIONES ====================
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }
    
    fun toggleHymnExpansion(hymnId: Int) {
        _expandedHymnId.value = if (_expandedHymnId.value == hymnId) null else hymnId
    }
    
    fun updateHymnNote(hymnId: Int, note: String) {
        viewModelScope.launch {
            try {
                repository.updateNote(hymnId, note)
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al guardar nota: ${e.message}"
            }
        }
    }
    
    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
    
    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }
}
