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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [HymnEntity::class, HymnListEntity::class, ListHymnCrossRef::class, FolderEntity::class], version = 13, exportSchema = false)
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
        
        // Migración 12 -> 13: Precargar datos iniciales
        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // La estructura ya existe, solo precargamos datos
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.let { database ->
                        prepopulateDatabase(database)
                    }
                }
            }
        }
        
        // Callback para precargar datos iniciales
        private val prepopulateCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Usar una corrutina para insertar datos en segundo plano
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.let { database ->
                        prepopulateDatabase(database)
                    }
                }
            }
        }
        
        private suspend fun prepopulateDatabase(database: HymnDatabase) {
            val folderDao = database.folderDao()
            val hymnListDao = database.hymnListDao()
            val hymnDao = database.hymnDao()
            
            // Crear Carpeta: "Alabanza"
            val folderAlabaId = folderDao.insertFolder(
                FolderEntity(
                    name = "Alabanza",
                    description = "Himnos organizados por tonalidad"
                )
            )
            
            // Lista "DO"
            val listaDo = hymnListDao.insertList(
                HymnListEntity(
                    name = "DO",
                    description = "Himnos en tonalidad DO",
                    folderId = folderAlabaId.toInt()
                )
            )
            addHymnsToList(hymnDao, hymnListDao, listaDo.toInt(), listOf(
                "Agua de vida", "Aquí en esta tierra", "Alabad a Jehová", "Alabaré",
                "Batalla no es batalla", "Bendito sea Jehová mi roca", "Caminando voy para canaán",
                "Cristo la roca", "Cristo rompe las cadenas", "Cuando allá se pase lista",
                "De Jesús el nombre invoca", "Digno eres Señor", "Dilo a Cristo", "Dulce comunión",
                "El encuentro en el aire", "El culto de hoy va a ser maravilloso",
                "En la nueva Jerulalem", "En la viña del Señor", "Grande gozo hay en mi alma hoy",
                "Habladme mas de Cristo", "Hay un motivo que es de grande gozo", "Junto a la cruz",
                "Llena de amor mi camino", "Lo entenderemos todo mas alla", "Mirad bendecid a Jehova",
                "No hay Dos tan grande como tu", "Oh bondad tan infinita", "Oh si quiero verle",
                "Oh yo quiero andar con Cristo", "Resucito la nueva edad", "Si en verdad esres salvo",
                "Soy libertado", "Todo lo que respire", "Un dia Cristo volverá", "Ven a la luz",
                "Vida nueva encontré", "Yo me gozo", "Yo siento gozo en mi alma",
                "Jacob lucho con el angel", "La historia de la cruz", "El rapto",
                "Cristo es la roca de poder", "El dia llegó", "Vamos subiendo una escalera",
                "Firme estaré"
            ))
            
            // Lista "RE"
            val listaRe = hymnListDao.insertList(
                HymnListEntity(
                    name = "RE",
                    description = "Himnos en tonalidad RE",
                    folderId = folderAlabaId.toInt()
                )
            )
            addHymnsToList(hymnDao, hymnListDao, listaRe.toInt(), listOf(
                "A combatir", "A Jesucristo ven sin tardar", "Bendito sea Jehova la roca",
                "Cristo es mío", "Edifiqué mi casa", "Gloria gloria Aleluya", "Hasta el fin lucharé",
                "Hay poder en Jesús", "Las promesas de Jesús", "Todo en todo es Jesucristo",
                "Ven, ven, ven Espiritu divino", "Vengan a cenar", "Yo tengo paz y gozo",
                "Esperanza plena", "Cristo es la roca de poder"
            ))
            
            // Lista "MI-"
            val listaMiMenor = hymnListDao.insertList(
                HymnListEntity(
                    name = "MI-",
                    description = "Himnos en tonalidad MI menor",
                    folderId = folderAlabaId.toInt()
                )
            )
            addHymnsToList(hymnDao, hymnListDao, listaMiMenor.toInt(), listOf(
                "Con gozo y alegría", "Alaba oh alma mia a Jehova", "El poderoso de Israel",
                "Cada dia", "Salmo 150", "El Señor es mi rey, mi todo", "Hay victoria en mi Jesús",
                "Jesucristo es tan ggrande", "Aclamad a Dios (Sal 65)"
            ))
            
            // Lista "MI"
            val listaMi = hymnListDao.insertList(
                HymnListEntity(
                    name = "MI",
                    description = "Himnos en tonalidad MI",
                    folderId = folderAlabaId.toInt()
                )
            )
            addHymnsToList(hymnDao, hymnListDao, listaMi.toInt(), listOf(
                "Caminando", "Amo a mi Dios", "Ardiendo en fuego", "Como el arca de Noé",
                "Cuando Dios la sangre ve", "Dios esta aqui, que precioso es",
                "Donde esta el espiritu de Dios", "El gozo que tengo yo", "El reloj de Dios",
                "El Señor me lleno de gozo", "En el hogar", "En arizona", "Es un canto de libertad",
                "Hay un presioso manantial", "La gloriosa aparicion", "Libre, tu me hiciste libre",
                "Libertad, oh que buena", "Manda el fuego Señor", "Poder, maravillosos poder",
                "Si ruge tempestad", "Si soy yo soy el templo", "Solamente en Cristo",
                "Seguiré a mi Jesus", "Ya viene Cristo", "Yo soy testigo del poder de Dios",
                "Yo vivo Señor porque tu vives", "Esta cayendo el fuego", "Bienvenidos",
                "Mi corazon alabara", "Trigo soy", "Feliz cumpleaños", "Por la sanagre"
            ))
            
            // Lista "SOL"
            val listaSol = hymnListDao.insertList(
                HymnListEntity(
                    name = "SOL",
                    description = "Himnos en tonalidad SOL",
                    folderId = folderAlabaId.toInt()
                )
            )
            addHymnsToList(hymnDao, hymnListDao, listaSol.toInt(), listOf(
                "Cuan gloriosa sera la mañana", "Cuando Cristo vino a mi corazon",
                "Dios me cuidara", "El aposento alto", "El ciello es el trono de mi Dios",
                "El coro celestial", "Espiritu de Dios, desciende sobre mi", "Galardon",
                "Halle un buen amigo", "Que binito es", "En el arrebatamiento",
                "Tal como el siervo brama (Sal 42)", "Demos gracias al Señor",
                "Bendita sea la gracia", "Victoria en Cristo", "Vuelvo a mi hogar"
            ))
        }
        
        // Función auxiliar para agregar himnos a una lista por nombre
        private suspend fun addHymnsToList(
            hymnDao: HymnDao,
            hymnListDao: HymnListDao,
            listId: Int,
            hymnTitles: List<String>
        ) {
            hymnTitles.forEachIndexed { index, title ->
                val hymn = hymnDao.getHymnByTitle(title)
                if (hymn != null && hymn.id != null) {
                    hymnListDao.addHymnToList(
                        ListHymnCrossRef(
                            listId = listId,
                            hymnId = hymn.id,
                            position = index
                        )
                    )
                }
            }
        }
        
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
                        MIGRATION_12_13,
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
