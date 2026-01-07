package com.example.himnariobeta

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla para la lista (Carpeta)
@Entity(tableName = "hymn_lists")
data class HymnListEntity(
    @PrimaryKey(autoGenerate = true)
    val listId: Int = 0,
    val name: String,
    val description: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val folderId: Int? = null // Nuevo campo para asociar la lista a una carpeta
)

// Tabla intermedia para guardar qué himno va en qué lista
@Entity(
    tableName = "list_hymn_cross_ref",
    primaryKeys = ["listId", "hymnId"]
)
data class ListHymnCrossRef(
    val listId: Int,
    val hymnId: Int,
    val position: Int = 0 // Nuevo campo para ordenar manualmente
)
