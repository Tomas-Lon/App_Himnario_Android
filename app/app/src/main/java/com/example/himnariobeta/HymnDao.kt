package com.example.himnariobeta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HymnDao {
    // Obtener todos los himnos ordenados por ID
    @Query("SELECT * FROM hymns ORDER BY id ASC")
    fun getAllHymns(): Flow<List<HymnEntity>>

    // Buscar por título, letra o ID (ignora tildes y diacríticos)
    @Query("""
        SELECT * FROM hymns 
        WHERE LOWER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
            title, 'á','a'), 'é','e'), 'í','i'), 'ó','o'), 'ú','u'), 'Á','A'), 'É','E'), 'Í','I'), 'Ó','O'), 'Ú','U'))
            LIKE '%' || LOWER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
            :query, 'á','a'), 'é','e'), 'í','i'), 'ó','o'), 'ú','u'), 'Á','A'), 'É','E'), 'Í','I'), 'Ó','O'), 'Ú','U')) || '%'
        OR LOWER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
            lyrics, 'á','a'), 'é','e'), 'í','i'), 'ó','o'), 'ú','u'), 'Á','A'), 'É','E'), 'Í','I'), 'Ó','O'), 'Ú','U'))
            LIKE '%' || LOWER(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
            :query, 'á','a'), 'é','e'), 'í','i'), 'ó','o'), 'ú','u'), 'Á','A'), 'É','E'), 'Í','I'), 'Ó','O'), 'Ú','U')) || '%'
        OR CAST(id AS TEXT) LIKE '%' || :query || '%' 
        ORDER BY id ASC
    """)
    fun searchHymns(query: String): Flow<List<HymnEntity>>

    // Insertar un himno (útil si necesitaras restaurar datos)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHymn(hymn: HymnEntity)

    // Actualizar un himno completo (para favoritos, notas, etc.)
    @Update
    suspend fun updateHymn(hymn: HymnEntity)

    // Actualizar solo la nota personal de un himno
    @Query("UPDATE hymns SET note = :note WHERE id = :hymnId")
    suspend fun updateNote(hymnId: Int, note: String)

    // Filtro dinámico por Categoría y Nota Musical
    // Convierte tonalidades españolas a americanas antes de comparar
    @Query("""
        SELECT * FROM hymns 
        WHERE (:category IS NULL OR category = :category) 
        AND (:key IS NULL OR 
            musical_key = :key OR
            (musical_key = 'DO' AND :key = 'C') OR
            (musical_key = 'RE' AND :key = 'D') OR
            (musical_key = 'MI' AND :key = 'E') OR
            (musical_key = 'FA' AND :key = 'F') OR
            (musical_key = 'SOL' AND :key = 'G') OR
            (musical_key = 'LA' AND :key = 'A') OR
            (musical_key = 'SI' AND :key = 'B')
        )
        ORDER BY id ASC
    """)
    fun filterHymns(category: String?, key: String?): Flow<List<HymnEntity>>
    
    // Buscar himno por título exacto
    @Query("SELECT * FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM(:title)) LIMIT 1")
    suspend fun getHymnByTitle(title: String): HymnEntity?
}
