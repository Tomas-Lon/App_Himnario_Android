package com.example.himnariobeta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de filtros
 */
class FilterViewModel(
    private val repository: HymnRepository
) : ViewModel() {
    
    // Filtros seleccionados
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    private val _selectedKey = MutableStateFlow<String?>(null)
    val selectedKey: StateFlow<String?> = _selectedKey.asStateFlow()
    
    // Himnos filtrados
    val filteredHymns: StateFlow<List<HymnEntity>> = combine(
        _selectedCategory,
        _selectedKey
    ) { category, key ->
        Pair(category, key)
    }.flatMapLatest { (category, key) ->
        repository.filterHymns(category, key)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Himno expandido
    private val _expandedHymnId = MutableStateFlow<Int?>(null)
    val expandedHymnId: StateFlow<Int?> = _expandedHymnId.asStateFlow()
    
    // Menús desplegables
    private val _categoryMenuExpanded = MutableStateFlow(false)
    val categoryMenuExpanded: StateFlow<Boolean> = _categoryMenuExpanded.asStateFlow()
    
    private val _keyMenuExpanded = MutableStateFlow(false)
    val keyMenuExpanded: StateFlow<Boolean> = _keyMenuExpanded.asStateFlow()
    
    // Opciones disponibles
    val categories = listOf("Adoración", "Alabanza", "Himno", "Cántico")
    val musicalKeys = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    
    // ==================== ACCIONES ====================
    
    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }
    
    fun clearCategory() {
        _selectedCategory.value = null
    }
    
    fun setKey(key: String?) {
        _selectedKey.value = key
    }
    
    fun clearKey() {
        _selectedKey.value = null
    }
    
    fun toggleCategoryMenu() {
        _categoryMenuExpanded.value = !_categoryMenuExpanded.value
    }
    
    fun closeCategoryMenu() {
        _categoryMenuExpanded.value = false
    }
    
    fun toggleKeyMenu() {
        _keyMenuExpanded.value = !_keyMenuExpanded.value
    }
    
    fun closeKeyMenu() {
        _keyMenuExpanded.value = false
    }
    
    fun toggleHymnExpansion(hymnId: Int) {
        _expandedHymnId.value = if (_expandedHymnId.value == hymnId) null else hymnId
    }
    
    fun updateHymnNote(hymnId: Int, note: String) {
        viewModelScope.launch {
            repository.updateNote(hymnId, note)
        }
    }
}
