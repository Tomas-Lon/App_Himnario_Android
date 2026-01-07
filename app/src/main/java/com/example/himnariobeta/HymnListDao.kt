package com.example.himnariobeta

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HymnListDao {
    // Obtener todas las listas ordenadas por fecha
    @Query("SELECT * FROM hymn_lists ORDER BY createdAt DESC")
    fun getAllLists(): Flow<List<HymnListEntity>>

    // Ordenar listas
    @Query("SELECT * FROM hymn_lists ORDER BY name ASC")
    fun getAllListsByName(): Flow<List<HymnListEntity>>

    @Query("SELECT * FROM hymn_lists ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllListsFavoritesFirst(): Flow<List<HymnListEntity>>

    // Crear una nueva lista
    @Insert
    suspend fun insertList(list: HymnListEntity): Long

    // Actualizar lista
    @Update
    suspend fun updateList(list: HymnListEntity)

    // Borrar una lista
    @Delete
    suspend fun deleteList(list: HymnListEntity)

    // Agregar un himno a una lista (en la tabla cruzada)
    // Cuando se inserta, idealmente deberíamos calcular la posición al final, 
    // pero aquí simplemente insertamos con default 0. El UI puede manejar reordenamiento luego.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addHymnToList(crossRef: ListHymnCrossRef)

    // Quitar un himno de una lista
    @Query("DELETE FROM list_hymn_cross_ref WHERE listId = :listId AND hymnId = :hymnId")
    suspend fun removeHymnFromList(listId: Int, hymnId: Int)

    // Eliminar TODOS los himnos de una lista
    @Query("DELETE FROM list_hymn_cross_ref WHERE listId = :listId")
    suspend fun removeAllHymnsFromList(listId: Int)

    // Obtener los himnos de una lista específica (soporta búsqueda interna)
    // AHORA ORDENADOS POR POSICIÓN y luego por ID si la posición es igual
    @Transaction
    @Query("""
        SELECT * FROM hymns 
        INNER JOIN list_hymn_cross_ref ON hymns.id = list_hymn_cross_ref.hymnId 
        WHERE list_hymn_cross_ref.listId = :listId 
        AND (:searchQuery IS NULL OR hymns.title LIKE '%' || :searchQuery || '%' OR hymns.lyrics LIKE '%' || :searchQuery || '%')
        ORDER BY list_hymn_cross_ref.position ASC, hymns.id ASC
    """)
    fun getHymnsForList(listId: Int, searchQuery: String? = null): Flow<List<HymnEntity>>

    // Función para obtener la referencia cruzada completa (necesaria para mover items)
    @Query("SELECT * FROM list_hymn_cross_ref WHERE listId = :listId ORDER BY position ASC")
    suspend fun getCrossRefsForList(listId: Int): List<ListHymnCrossRef>

    // Actualizar una referencia cruzada (para cambiar posición)
    @Update
    suspend fun updateCrossRef(crossRef: ListHymnCrossRef)

    // Duplicar lista
    @Query("SELECT hymnId FROM list_hymn_cross_ref WHERE listId = :listId")
    suspend fun getHymnIdsForList(listId: Int): List<Int>
}
