package com.example.himnariobeta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.himnariobeta.utils.ChordTransposer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de músicos
 * Maneja la transposición de acordes y selección de tonalidades
 */
class MusicianViewModel(
    private val repository: HymnRepository
) : ViewModel() {
    
    // Tonalidad seleccionada (null = usar tonalidad original del himno)
    private val _selectedKey = MutableStateFlow<String?>(null)
    val selectedKey: StateFlow<String?> = _selectedKey.asStateFlow()
    
    // Estado de búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()
    
    // Himno expandido
    private val _expandedHymnId = MutableStateFlow<Int?>(null)
    val expandedHymnId: StateFlow<Int?> = _expandedHymnId.asStateFlow()
    
    // Todos los himnos
    val allHymns: StateFlow<List<HymnEntity>> = repository.getAllHymns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Himnos filtrados por búsqueda
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
    
    // Himnos que tienen acordes/notación musical
    val hymnsWithChords: StateFlow<List<HymnEntity>> = displayedHymns
        .map { hymns -> hymns.filter { !it.musical_notation.isNullOrBlank() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Mensajes Snackbar
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    
    // Lista de tonalidades disponibles
    val availableKeys = ChordTransposer.standardKeys
    
    // ==================== ACCIONES ====================
    
    /**
     * Cambia la tonalidad seleccionada
     * Si es null, usa la tonalidad original del himno
     */
    fun setSelectedKey(key: String?) {
        _selectedKey.value = key
    }
    
    /**
     * Obtiene la tonalidad efectiva (personalizada o original del himno)
     */
    fun getEffectiveKey(hymn: HymnEntity): String {
        return _selectedKey.value ?: hymn.musical_key ?: "C"
    }
    
    /**
     * Transpone la notación musical de un himno a la tonalidad seleccionada
     * Si no hay tonalidad seleccionada, usa la original del himno
     */
    fun transposeHymn(hymn: HymnEntity): String {
        val targetKey = getEffectiveKey(hymn)
        return ChordTransposer.transpose(
            hymn.musical_notation,
            targetKey
        )
    }
    
    /**
     * Resetea a la tonalidad original del himno
     */
    fun resetToOriginalKey() {
        _selectedKey.value = null
    }
    
    /**
     * Obtiene el texto completo del himno con acordes transpuestos intercalados
     */
    fun getHymnWithChords(hymn: HymnEntity): String {
        val transposedChords = transposeHymn(hymn)
        val lyrics = hymn.lyrics ?: ""
        
        if (transposedChords.isBlank()) {
            return lyrics
        }
        
        // Combinar acordes y letra
        return "$transposedChords\n$lyrics"
    }
    
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
