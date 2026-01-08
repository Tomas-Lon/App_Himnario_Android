package com.example.himnariobeta

/**
 * Funciones de utilidad compartidas en la aplicación
 */

/**
 * Limpia el texto de caracteres no válidos y espacios en blanco
 */
fun String?.cleanHymnText(): String {
    if (this.isNullOrBlank()) return ""
    return this.replace("\uFFFD", "")
        .replace(Regex("[^\\p{L}\\p{N}\\p{P}\\p{Z}\\n\\r]"), "")
        .trim()
}

/**
 * Obtiene un título seguro para un himno
 */
fun HymnEntity.getSafeTitle(): String {
    val cleanTitle = title.cleanHymnText()
    return cleanTitle.ifBlank { "Himno ${id ?: 0}" }
}

/**
 * Obtiene la letra segura de un himno
 */
fun HymnEntity.getSafeLyrics(): String {
    return if (lyrics.isNullOrBlank()) {
        "No se encontró la letra."
    } else {
        lyrics.cleanHymnText()
    }
}
