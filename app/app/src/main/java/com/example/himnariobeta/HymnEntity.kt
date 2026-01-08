package com.example.himnariobeta

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hymns")
data class HymnEntity(
    @PrimaryKey
    val id: Int?,
    val title: String?,
    val lyrics: String?,
    val note: String? = null,
    val numero: Int? = null,
    val category: String? = null,
    val musical_key: String? = null,
    val musical_notation: String? = null, // Acordes con grados romanos (I, IV, V, vi, etc.)
    // val isFavorite: Boolean = false
)
