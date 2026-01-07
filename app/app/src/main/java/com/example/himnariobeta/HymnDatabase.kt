package com.example.himnariobeta

import android.content.Context
import android.database.Cursor
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.himnariobeta.FolderEntity
import com.example.himnariobeta.FolderDao

@Database(entities = [HymnEntity::class, HymnListEntity::class, ListHymnCrossRef::class, FolderEntity::class], version = 12, exportSchema = false)
abstract class HymnDatabase : RoomDatabase() {

    abstract fun hymnDao(): HymnDao
    abstract fun hymnListDao(): HymnListDao
    abstract fun folderDao(): FolderDao

    companion object {
        @Volatile
        private var INSTANCE: HymnDatabase? = null

           private fun ensureSchema(db: SupportSQLiteDatabase) {
               // 1. Asegurar tabla de listas (hymn_lists)
               db.execSQL("CREATE TABLE IF NOT EXISTS `hymn_lists` (`listId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `isFavorite` INTEGER NOT NULL DEFAULT 0, `createdAt` INTEGER NOT NULL, `folderId` INTEGER)")
               // 2. Asegurar tabla intermedia (list_hymn_cross_ref)
               db.execSQL("CREATE TABLE IF NOT EXISTS `list_hymn_cross_ref` (`listId` INTEGER NOT NULL, `hymnId` INTEGER NOT NULL, `position` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`listId`, `hymnId`))")
               // 3. Revisar columnas en hymn_lists
               checkAndAddColumn(db, "hymn_lists", "description", "TEXT")
               checkAndAddColumn(db, "hymn_lists", "isFavorite", "INTEGER NOT NULL DEFAULT 0")
               checkAndAddColumn(db, "hymn_lists", "folderId", "INTEGER")
               // 4. Revisar columnas en hymns
               // checkAndAddColumn(db, "hymns", "isFavorite", "INTEGER NOT NULL DEFAULT 0")
               // 5. Revisar columna position en list_hymn_cross_ref
               checkAndAddColumn(db, "list_hymn_cross_ref", "position", "INTEGER NOT NULL DEFAULT 0")
               // 6. Crear tabla de carpetas si no existe
               db.execSQL("CREATE TABLE IF NOT EXISTS `folders` (`folderId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `createdAt` INTEGER NOT NULL)")
           }

        // Función auxiliar robusta para verificar y agregar columnas
        private fun checkAndAddColumn(db: SupportSQLiteDatabase, tableName: String, columnName: String, columnDef: String) {
            var cursor: Cursor? = null
            try {
                cursor = db.query("SELECT * FROM `$tableName` LIMIT 0")
                if (cursor.getColumnIndex(columnName) == -1) {
                    // La columna no existe, intentamos agregarla
                    db.execSQL("ALTER TABLE `$tableName` ADD COLUMN `$columnName` $columnDef")
                }
            } catch (e: Exception) {
                // Si falla la consulta (ej. tabla no existe, aunque ya la creamos con IF NOT EXISTS), lo ignoramos
                // O si falla el ALTER, no podemos hacer mucho más sin romper la migración, pero al menos intentamos.
            } finally {
                cursor?.close()
            }
        }

        // Definimos migraciones hacia la versión 11 desde cualquier punto
        val MIGRATION_GENERIC_TO_11 = object : Migration(1, 11) {
            override fun migrate(db: SupportSQLiteDatabase) { ensureSchema(db) }
        }
        // Rutas directas para ayudar a Room
        val MIGRATION_10_11 = object : Migration(10, 11) { override fun migrate(db: SupportSQLiteDatabase) { ensureSchema(db) } }
        val MIGRATION_9_11 = object : Migration(9, 11) { override fun migrate(db: SupportSQLiteDatabase) { ensureSchema(db) } }
        val MIGRATION_8_11 = object : Migration(8, 11) { override fun migrate(db: SupportSQLiteDatabase) { ensureSchema(db) } }
        val MIGRATION_7_11 = object : Migration(7, 11) { override fun migrate(db: SupportSQLiteDatabase) { ensureSchema(db) } }
        fun getDatabase(context: Context): HymnDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HymnDatabase::class.java,
                    "hymn_database"
                )
                    .createFromAsset("database/himnario.db")
                    .addMigrations(
                        MIGRATION_GENERIC_TO_11,
                        MIGRATION_10_11,
                        MIGRATION_9_11,
                        MIGRATION_8_11,
                        MIGRATION_7_11,
                        // Mantenemos otras rutas antiguas mapeadas a la nueva lógica
                        object : Migration(6, 11) { override fun migrate(db: SupportSQLiteDatabase) { ensureSchema(db) } },
                        object : Migration(5, 11) { override fun migrate(db: SupportSQLiteDatabase) { ensureSchema(db) } }
                    )
                    // IMPORTANTE: NO usamos fallbackToDestructiveMigration para proteger los datos del usuario
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
