package com.example.himnariobeta.utils

/**
 * Utilidad para transponer acordes desde grados romanos a notas específicas
 * Preserva el espaciado exacto del texto original
 */
object ChordTransposer {
    
    // Mapeo de grados romanos (mayúsculas = mayor, minúsculas = menor)
    private val romanNumerals = listOf(
        "I", "i", "II", "ii", "III", "iii", "IV", "iv", 
        "V", "v", "VI", "vi", "VII", "vii"
    )
    
    // Escalas completas para cada tonalidad (I, II, III, IV, V, VI, VII)
    // Formato: [I mayor, i menor, II mayor, ii menor, III mayor, iii menor, ...]
    private val scales = mapOf(
        // Tonalidad DO
        "C" to listOf("C", "Cm", "D", "Dm", "E", "Em", "F", "Fm", "G", "Gm", "A", "Am", "B", "Bm"),
        "DO" to listOf("C", "Cm", "D", "Dm", "E", "Em", "F", "Fm", "G", "Gm", "A", "Am", "B", "Bm"),
        
        // Tonalidad DO#/REb
        "C#" to listOf("C#", "C#m", "D#", "D#m", "F", "Fm", "F#", "F#m", "G#", "G#m", "A#", "A#m", "C", "Cm"),
        "Db" to listOf("Db", "Dbm", "Eb", "Ebm", "F", "Fm", "Gb", "Gbm", "Ab", "Abm", "Bb", "Bbm", "C", "Cm"),
        
        // Tonalidad RE
        "D" to listOf("D", "Dm", "E", "Em", "F#", "F#m", "G", "Gm", "A", "Am", "B", "Bm", "C#", "C#m"),
        "RE" to listOf("D", "Dm", "E", "Em", "F#", "F#m", "G", "Gm", "A", "Am", "B", "Bm", "C#", "C#m"),
        
        // Tonalidad RE#/MIb
        "D#" to listOf("D#", "D#m", "F", "Fm", "G", "Gm", "G#", "G#m", "A#", "A#m", "C", "Cm", "D", "Dm"),
        "Eb" to listOf("Eb", "Ebm", "F", "Fm", "G", "Gm", "Ab", "Abm", "Bb", "Bbm", "C", "Cm", "D", "Dm"),
        
        // Tonalidad MI
        "E" to listOf("E", "Em", "F#", "F#m", "G#", "G#m", "A", "Am", "B", "Bm", "C#", "C#m", "D#", "D#m"),
        "MI" to listOf("E", "Em", "F#", "F#m", "G#", "G#m", "A", "Am", "B", "Bm", "C#", "C#m", "D#", "D#m"),
        
        // Tonalidad FA
        "F" to listOf("F", "Fm", "G", "Gm", "A", "Am", "Bb", "Bbm", "C", "Cm", "D", "Dm", "E", "Em"),
        "FA" to listOf("F", "Fm", "G", "Gm", "A", "Am", "Bb", "Bbm", "C", "Cm", "D", "Dm", "E", "Em"),
        
        // Tonalidad FA#/SOLb
        "F#" to listOf("F#", "F#m", "G#", "G#m", "A#", "A#m", "B", "Bm", "C#", "C#m", "D#", "D#m", "F", "Fm"),
        "Gb" to listOf("Gb", "Gbm", "Ab", "Abm", "Bb", "Bbm", "B", "Bm", "Db", "Dbm", "Eb", "Ebm", "F", "Fm"),
        
        // Tonalidad SOL
        "G" to listOf("G", "Gm", "A", "Am", "B", "Bm", "C", "Cm", "D", "Dm", "E", "Em", "F#", "F#m"),
        "SOL" to listOf("G", "Gm", "A", "Am", "B", "Bm", "C", "Cm", "D", "Dm", "E", "Em", "F#", "F#m"),
        
        // Tonalidad SOL#/LAb
        "G#" to listOf("G#", "G#m", "A#", "A#m", "C", "Cm", "C#", "C#m", "D#", "D#m", "F", "Fm", "G", "Gm"),
        "Ab" to listOf("Ab", "Abm", "Bb", "Bbm", "C", "Cm", "Db", "Dbm", "Eb", "Ebm", "F", "Fm", "G", "Gm"),
        
        // Tonalidad LA
        "A" to listOf("A", "Am", "B", "Bm", "C#", "C#m", "D", "Dm", "E", "Em", "F#", "F#m", "G#", "G#m"),
        "LA" to listOf("A", "Am", "B", "Bm", "C#", "C#m", "D", "Dm", "E", "Em", "F#", "F#m", "G#", "G#m"),
        
        // Tonalidad LA#/SIb
        "A#" to listOf("A#", "A#m", "C", "Cm", "D", "Dm", "D#", "D#m", "F", "Fm", "G", "Gm", "A", "Am"),
        "Bb" to listOf("Bb", "Bbm", "C", "Cm", "D", "Dm", "Eb", "Ebm", "F", "Fm", "G", "Gm", "A", "Am"),
        
        // Tonalidad SI
        "B" to listOf("B", "Bm", "C#", "C#m", "D#", "D#m", "E", "Em", "F#", "F#m", "G#", "G#m", "A#", "A#m"),
        "SI" to listOf("B", "Bm", "C#", "C#m", "D#", "D#m", "E", "Em", "F#", "F#m", "G#", "G#m", "A#", "A#m")
    )
    
    /**
     * Transpone un texto con grados romanos a una tonalidad específica
     * Preserva el espaciado exacto del texto original
     * 
     * @param notation Texto con grados romanos (ej: "I    IV    V")
     * @param key Tonalidad destino (ej: "C", "D", "MI")
     * @return Texto con acordes transpuestos manteniendo espaciado
     */
    fun transpose(notation: String?, key: String?): String {
        if (notation.isNullOrBlank() || key.isNullOrBlank()) {
            return notation ?: ""
        }
        
        val scale = scales[key.uppercase()] ?: scales["C"]
        if (scale == null) return notation
        
        var result = notation
        
        // Reemplazar grados romanos por acordes
        // Procesamos de mayor a menor longitud para evitar reemplazos parciales
        val sortedNumerals = romanNumerals.sortedByDescending { it.length }
        
        sortedNumerals.forEach { numeral ->
            val index = getRomanNumeralIndex(numeral)
            if (index >= 0 && index < scale.size) {
                val chord = scale[index]
                // Usar word boundaries para evitar reemplazos parciales
                result = result?.replace(Regex("\\b$numeral\\b"), chord)
            }
        }
        
        return result ?: ""
    }
    
    /**
     * Obtiene el índice en la escala para un grado romano
     */
    private fun getRomanNumeralIndex(numeral: String): Int {
        return when (numeral) {
            "I" -> 0
            "i" -> 1
            "II" -> 2
            "ii" -> 3
            "III" -> 4
            "iii" -> 5
            "IV" -> 6
            "iv" -> 7
            "V" -> 8
            "v" -> 9
            "VI" -> 10
            "vi" -> 11
            "VII" -> 12
            "vii" -> 13
            else -> -1
        }
    }
    
    /**
     * Lista de tonalidades disponibles
     */
    val availableKeys = listOf(
        "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B",
        "DO", "RE", "MI", "FA", "SOL", "LA", "SI"
    )
    
    /**
     * Lista simplificada de tonalidades (solo notación anglosajona)
     */
    val standardKeys = listOf(
        "C", "C#/Db", "D", "D#/Eb", "E", "F", "F#/Gb", "G", "G#/Ab", "A", "A#/Bb", "B"
    )
}
