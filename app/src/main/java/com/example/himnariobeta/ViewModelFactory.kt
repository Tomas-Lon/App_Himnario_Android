package com.example.himnariobeta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory para crear ViewModels con dependencias (Repository)
 * Permite que los ViewModels sobrevivan a cambios de configuración
 */
class HymnViewModelFactory(
    private val repository: HymnRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ListsViewModel::class.java) -> {
                ListsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(FilterViewModel::class.java) -> {
                FilterViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
