package com.example.himnariobeta

import java.text.Normalizer

/**
 * Funciones de utilidad compartidas en la aplicación
 */

/**
 * Normaliza texto eliminando tildes y diacríticos para búsquedas
 * "José María" -> "jose maria"
 */
fun String.normalizeForSearch(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{M}".toRegex(), "")
        .lowercase()
}

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

/**
 * Convierte tonalidad española a cifrado americano
 * SOL -> G, DO -> C, RE -> D, MI -> E, FA -> F, LA -> A, SI -> B
 */
fun String?.toAmericanKey(): String? {
    if (this == null) return null
    return when (this.uppercase().trim()) {
        "DO" -> "C"
        "RE" -> "D"
        "MI" -> "E"
        "FA" -> "F"
        "SOL" -> "G"
        "LA" -> "A"
        "SI" -> "B"
        // Si ya está en formato americano, retornar tal cual
        else -> this.uppercase().firstOrNull()?.toString()?.let { 
            if (it in listOf("C", "D", "E", "F", "G", "A", "B")) this else null 
        }
    }
}

/**
 * Obtiene la tonalidad en formato americano para mostrar
 */
fun HymnEntity.getAmericanKey(): String? {
    return this.musical_key?.toAmericanKey()
}
