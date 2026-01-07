
package com.example.himnariobeta




import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
// ...existing code...
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.himnariobeta.ui.theme.HimnarioBetaTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HimnarioBetaTheme {
                HymnApp()
            }
        }
    }
}

enum class Screen {
    HOME, LISTS, FILTERS
}

enum class SortOption {
    DATE_DESC, NAME_ASC, FAVORITES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnApp() {
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val database = remember { HymnDatabase.getDatabase(context) }
    val hymnDao = database.hymnDao()
    val listDao = database.hymnListDao()
    val folderDao = database.folderDao()

    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedList by remember { mutableStateOf<HymnListEntity?>(null) }
    var selectedFolder by remember { mutableStateOf<FolderEntity?>(null) }
    var listSortOption by remember { mutableStateOf(SortOption.DATE_DESC) }

    var expandedHymnId by remember { mutableStateOf<Int?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var showCreateListDialog by remember { mutableStateOf(false) }
    var showAddToListDialog by remember { mutableStateOf(false) }
    var hymnToAdd by remember { mutableStateOf<HymnEntity?>(null) }

    val scope = rememberCoroutineScope()

    val allHymns by hymnDao.getAllHymns().collectAsState(initial = emptyList())
    val searchResults by hymnDao.searchHymns(searchQuery).collectAsState(initial = emptyList())

    val allFolders by folderDao.getAllFolders().collectAsState(initial = emptyList())
    val allLists by when(listSortOption) {
        SortOption.DATE_DESC -> listDao.getAllLists().collectAsState(initial = emptyList())
        SortOption.NAME_ASC -> listDao.getAllListsByName().collectAsState(initial = emptyList())
        SortOption.FAVORITES -> listDao.getAllListsFavoritesFirst().collectAsState(initial = emptyList())
    }

    val currentHymnList = if (searchQuery.isNotEmpty()) searchResults else allHymns

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    when {
                        selectedList != null -> Text(text = selectedList!!.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        selectedFolder != null -> Text(text = selectedFolder!!.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        isSearchActive -> OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Buscar...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        else -> {
                            val titleText = when(currentScreen) {
                                Screen.HOME -> "Himnario"
                                Screen.LISTS -> "Carpetas y Listas"
                                Screen.FILTERS -> "Filtros"
                            }
                            Text(text = titleText)
                        }
                    }
                },
                navigationIcon = {
                    when {
                        selectedList != null -> IconButton(onClick = { selectedList = null }) { Icon(Icons.Default.ArrowBack, contentDescription = "Atrás") }
                        selectedFolder != null -> IconButton(onClick = { selectedFolder = null }) { Icon(Icons.Default.ArrowBack, contentDescription = "Atrás") }
                    }
                },
                actions = {
                    if (currentScreen == Screen.LISTS && selectedList == null && selectedFolder == null) {
                        var menuExpanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { menuExpanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Opciones") }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(text = { Text("Crear carpeta") }, onClick = { scope.launch { folderDao.insertFolder(FolderEntity(name = "Nueva carpeta")) }; menuExpanded = false })
                            DropdownMenuItem(text = { Text("Ordenar listas por fecha") }, onClick = { listSortOption = SortOption.DATE_DESC; menuExpanded = false })
                            DropdownMenuItem(text = { Text("Ordenar listas por nombre") }, onClick = { listSortOption = SortOption.NAME_ASC; menuExpanded = false })
                            DropdownMenuItem(text = { Text("Favoritos primero") }, onClick = { listSortOption = SortOption.FAVORITES; menuExpanded = false })
                        }
                    }
                    if (selectedList == null && selectedFolder == null && currentScreen != Screen.FILTERS && currentScreen != Screen.LISTS) {
                        if (isSearchActive) {
                            IconButton(onClick = {
                                isSearchActive = false
                                searchQuery = ""
                            }) { Icon(Icons.Default.Close, contentDescription = "Cerrar búsqueda") }
                        } else {
                            IconButton(onClick = { isSearchActive = true }) { Icon(Icons.Default.Search, contentDescription = "Buscar") }
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (selectedList == null && selectedFolder == null) {
                NavigationBar {
                    NavigationBarItem(icon = { Icon(Icons.Default.Home, contentDescription = "Himnos") }, label = { Text("Himnos") }, selected = currentScreen == Screen.HOME, onClick = { currentScreen = Screen.HOME; selectedList = null; selectedFolder = null })
                    NavigationBarItem(icon = { Icon(Icons.Default.List, contentDescription = "Listas") }, label = { Text("Listas") }, selected = currentScreen == Screen.LISTS, onClick = { currentScreen = Screen.LISTS; selectedList = null; selectedFolder = null })
                    NavigationBarItem(icon = { Icon(Icons.Default.Search, contentDescription = "Filtros") }, label = { Text("Filtros") }, selected = currentScreen == Screen.FILTERS, onClick = { currentScreen = Screen.FILTERS; selectedList = null; selectedFolder = null })
                }
            }
        },
        floatingActionButton = {
            if (currentScreen == Screen.LISTS && selectedList == null && selectedFolder != null) {
                FloatingActionButton(onClick = { showCreateListDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Crear Lista en carpeta")
                }
            }
        },
        snackbarHost = {
            if (snackbarMessage != null) {
                androidx.compose.material3.Snackbar(
                    action = {
                        androidx.compose.material3.TextButton(onClick = { snackbarMessage = null }) {
                            Text("Cerrar")
                        }
                    },
                    content = { Text(snackbarMessage!!) }
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when {
                selectedList != null -> {
                    val liveList = allLists.find { it.listId == selectedList!!.listId } ?: selectedList!!
                    ListDetailScreen(
                        list = liveList,
                        listDao = listDao,
                        hymnDao = hymnDao,
                        expandedHymnId = expandedHymnId,
                        onToggleExpand = { id -> expandedHymnId = if (expandedHymnId == id) null else id },
                        onNoteChange = { id, note -> scope.launch { hymnDao.updateNote(id, note) } },
                        onDuplicateList = {
                            scope.launch {
                                val ids = listDao.getHymnIdsForList(liveList.listId)
                                val newId = listDao.insertList(HymnListEntity(name = "${liveList.name} (Copia)", description = liveList.description, folderId = liveList.folderId))
                                ids.forEach { hId -> listDao.addHymnToList(ListHymnCrossRef(newId.toInt(), hId)) }
                            }
                            selectedList = null
                        }
                    )
                    BackHandler { selectedList = null }
                }
                selectedFolder != null -> {
                    FolderScreen(
                        folder = selectedFolder!!,
                        lists = allLists.filter { it.folderId == selectedFolder!!.folderId },
                        onListClick = { selectedList = it },
                        onDeleteList = { list -> scope.launch {
                            try {
                                listDao.deleteList(list)
                                snackbarMessage = "Lista eliminada"
                            } catch (e: Exception) {
                                snackbarMessage = "Error al eliminar lista: ${e.message}"
                            }
                        } },
                        onToggleFavorite = { list -> scope.launch {
                            try {
                                listDao.updateList(list.copy(isFavorite = !list.isFavorite))
                                snackbarMessage = if (!list.isFavorite) "Marcada como favorita" else "Desmarcada como favorita"
                            } catch (e: Exception) {
                                snackbarMessage = "Error al actualizar favorito: ${e.message}"
                            }
                        } },
                        onDeleteFolder = { folder -> scope.launch {
                            try {
                                folderDao.deleteFolder(folder)
                                selectedFolder = null
                                snackbarMessage = "Carpeta eliminada"
                            } catch (e: Exception) {
                                snackbarMessage = "Error al eliminar carpeta: ${e.message}"
                            }
                        } },
                        onEditFolder = { folder, name, desc -> scope.launch {
                            try {
                                folderDao.updateFolder(folder.copy(name = name, description = desc))
                                snackbarMessage = "Carpeta actualizada"
                            } catch (e: Exception) {
                                snackbarMessage = "Error al editar carpeta: ${e.message}"
                            }
                        } },
                        onMoveListToFolder = { list, targetFolder -> scope.launch {
                            try {
                                listDao.updateList(list.copy(folderId = targetFolder?.folderId))
                                snackbarMessage = "Lista movida"
                            } catch (e: Exception) {
                                snackbarMessage = "Error al mover lista: ${e.message}"
                            }
                        } },
                        allFolders = allFolders
                    )
                }
                currentScreen == Screen.HOME -> {
                    HymnList(
                        hymns = currentHymnList,
                        expandedHymnId = expandedHymnId,
                        onToggleExpand = { id -> expandedHymnId = if (expandedHymnId == id) null else id },
                        onAddToList = { hymn -> hymnToAdd = hymn; showAddToListDialog = true },
                        onNoteChange = { id, note -> scope.launch { hymnDao.updateNote(id, note) } }
                    )
                }
                currentScreen == Screen.LISTS -> {
                    FoldersScreen(
                        folders = allFolders,
                        lists = allLists,
                        onFolderClick = { selectedFolder = it },
                        onDeleteFolder = { folder -> scope.launch {
                            try {
                                folderDao.deleteFolder(folder)
                                snackbarMessage = "Carpeta eliminada"
                            } catch (e: Exception) {
                                snackbarMessage = "Error al eliminar carpeta: ${e.message}"
                            }
                        } },
                        onListClick = { selectedList = it },
                        onDeleteList = { list -> scope.launch {
                            try {
                                listDao.deleteList(list)
                                snackbarMessage = "Lista eliminada"
                            } catch (e: Exception) {
                                snackbarMessage = "Error al eliminar lista: ${e.message}"
                            }
                        } },
                        onToggleFavorite = { list -> scope.launch {
                            try {
                                listDao.updateList(list.copy(isFavorite = !list.isFavorite))
                                snackbarMessage = if (!list.isFavorite) "Marcada como favorita" else "Desmarcada como favorita"
                            } catch (e: Exception) {
                                snackbarMessage = "Error al actualizar favorito: ${e.message}"
                            }
                        } },
                        allFolders = allFolders,
                        onMoveListToFolder = { list, folder -> scope.launch {
                            try {
                                listDao.updateList(list.copy(folderId = folder?.folderId))
                                snackbarMessage = if (folder != null) "Lista movida a carpeta" else "Lista sin carpeta"
                            } catch (e: Exception) {
                                snackbarMessage = "Error al mover lista: ${e.message}"
                            }
                        } }
                    )
                }
                currentScreen == Screen.FILTERS -> {
                    FilterScreen(hymnDao = hymnDao, onHymnClick = { })
                }
            }
        }
    }

    if (showCreateListDialog && selectedFolder != null) {
        CreateListDialog(
            onDismiss = { showCreateListDialog = false },
            onConfirm = { name, desc ->
                scope.launch {
                    listDao.insertList(HymnListEntity(name = name, description = desc, folderId = selectedFolder!!.folderId))
                }
                showCreateListDialog = false
            }
        )
    }

    if (showAddToListDialog && hymnToAdd != null) {
        AddHymnToListDialog(
            lists = allLists,
            onDismiss = { showAddToListDialog = false },
            onListSelected = { list ->
                scope.launch {
                    val safeId = hymnToAdd!!.id ?: 0
                    listDao.addHymnToList(ListHymnCrossRef(list.listId, safeId))
                }
                showAddToListDialog = false
            }
        )
    }
}

// ... HymnList, HymnItem, ListsScreen se mantienen casi igual, solo limpieza ...

@Composable
fun HymnList(
    hymns: List<HymnEntity>,
    expandedHymnId: Int?,
    onToggleExpand: (Int) -> Unit,
    onAddToList: (HymnEntity) -> Unit,
    onNoteChange: (Int, String) -> Unit
) {
    if (hymns.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "No hay himnos disponibles.", style = MaterialTheme.typography.titleMedium)
        }
    } else {
        LazyColumn {
            items(hymns) { hymn ->
                val safeId = hymn.id ?: 0
                HymnItem(
                    hymn = hymn,
                    expanded = safeId == expandedHymnId,
                    onToggleExpand = { onToggleExpand(safeId) },
                    onAddToList = { onAddToList(hymn) },
                    onNoteChange = { note -> onNoteChange(safeId, note) },
                    showAddButton = true
                )
            }
        }
    }
}

@Composable
fun HymnItem(
    hymn: HymnEntity,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onAddToList: (() -> Unit)? = null,
    onRemoveFromList: (() -> Unit)? = null,
    onNoteChange: (String) -> Unit,
    showAddButton: Boolean,
    // Eliminado: onToggleFavorite
    compactButtons: Boolean = false,
    // Botones opcionales para reordenar
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    canMoveUp: Boolean = true,
    canMoveDown: Boolean = true
) {
    fun cleanText(input: String?): String {
        if (input.isNullOrBlank()) return ""
        return input.replace("\uFFFD", "")
            .replace(Regex("[^\\p{L}\\p{N}\\p{P}\\p{Z}\\n\\r]"), "")
            .trim()
    }

    val rawTitle = hymn.title ?: "Sin título"
    val safeTitle = cleanText(rawTitle).ifBlank { "Himno ${hymn.id ?: 0}" }
    val safeId = hymn.id ?: 0
    val rawLyrics = hymn.lyrics
    val safeLyrics = if (rawLyrics.isNullOrBlank()) "No se encontró la letra." else cleanText(rawLyrics)

    val buttonSize = if (compactButtons) 28.dp else 40.dp
    val iconSize = if (compactButtons) 18.dp else 24.dp
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (compactButtons) 4.dp else 8.dp, vertical = 6.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(modifier = Modifier.padding(if (compactButtons) 8.dp else 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Reordenamiento manual (Flechas)
                if (onMoveUp != null || onMoveDown != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { if (canMoveUp && onMoveUp != null) onMoveUp() },
                            enabled = canMoveUp && onMoveUp != null,
                            modifier = Modifier.height(24.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Subir")
                        }
                        IconButton(
                            onClick = { if (canMoveDown && onMoveDown != null) onMoveDown() },
                            enabled = canMoveDown && onMoveDown != null,
                            modifier = Modifier.height(24.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Bajar")
                        }
                    }
                }

                Text(
                    text = "${safeId}. ${safeTitle}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showAddButton && onAddToList != null) {
                        IconButton(
                            onClick = onAddToList,
                            modifier = Modifier.size(buttonSize)
                        ) {
                            Icon(PlaylistAddIcon, contentDescription = "Agregar a lista", modifier = Modifier.size(iconSize))
                        }
                    }
                    if (onRemoveFromList != null) {
                        IconButton(
                            onClick = onRemoveFromList,
                            modifier = Modifier.size(buttonSize)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Quitar de lista", modifier = Modifier.size(iconSize))
                        }
                    }
                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier.size(buttonSize)
                    ) {
                        Icon(
                            if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Contraer" else "Expandir",
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = safeLyrics,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                var noteText by remember { mutableStateOf(hymn.note ?: "") }
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { newNote ->
                        noteText = newNote
                        onNoteChange(newNote)
                    },
                    label = { Text("Comentarios personales") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe un comentario aquí...") },
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                val context = LocalContext.current
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { shareHymn(context, hymn) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("TXT")
                    }
                    Button(
                        onClick = { exportHymnAsPdf(context, hymn) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF")
                    }
                }
            }
        }
    }
}

@Composable
fun ListsScreen(
    // Esta pantalla ya no se usa, la lógica de carpetas reales está en FoldersScreen y FolderScreen
) {}

@Composable
fun ListDetailScreen(
    list: HymnListEntity,
    listDao: HymnListDao,
    hymnDao: HymnDao,
    expandedHymnId: Int?,
    onToggleExpand: (Int) -> Unit,
    onNoteChange: (Int, String) -> Unit,
    onDuplicateList: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val hymnsInList by listDao.getHymnsForList(list.listId, if (searchQuery.isBlank()) null else searchQuery)
        .collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    var searchToAddQuery by remember { mutableStateOf("") }
    val globalSearchResults by hymnDao.searchHymns(searchToAddQuery).collectAsState(initial = emptyList())

    val canReorder = hymnsInList.size > 1
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
             Text(
                 text = "${hymnsInList.size} himnos",
                 style = MaterialTheme.typography.labelMedium,
                 modifier = Modifier.padding(start = 8.dp)
             )

             Box {
                 IconButton(onClick = { menuExpanded = true }) {
                     Icon(Icons.Default.MoreVert, contentDescription = "Opciones de lista")
                 }
                 DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                     DropdownMenuItem(
                         text = { Text("Editar Nombre/Comentario") },
                         onClick = { showRenameDialog = true; menuExpanded = false },
                         leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                     )
                     DropdownMenuItem(
                         text = { Text("Duplicar Lista") },
                         onClick = { onDuplicateList(); menuExpanded = false },
                         leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
                     )
                     DropdownMenuItem(
                         text = { Text("Compartir Lista (TXT)") },
                         onClick = {
                             shareListAsText(context, list, hymnsInList)
                             menuExpanded = false
                         },
                         leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) }
                     )
                     DropdownMenuItem(
                         text = { Text("Compartir Lista (PDF)") },
                         onClick = {
                             exportListAsPdf(context, list, hymnsInList)
                             menuExpanded = false
                         },
                         leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) }
                     )
                     DropdownMenuItem(
                         text = { Text("Vaciar Lista") },
                         onClick = { showDeleteAllDialog = true; menuExpanded = false },
                         leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                     )
                 }
             }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar dentro de esta lista...") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                 if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Close, contentDescription = "Limpiar") }
                }
            }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        OutlinedTextField(
            value = searchToAddQuery,
            onValueChange = { searchToAddQuery = it },
            placeholder = { Text("Buscar himno para AGREGAR...") },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
            trailingIcon = {
                if (searchToAddQuery.isNotEmpty()) {
                    IconButton(onClick = { searchToAddQuery = "" }) { Icon(Icons.Default.Close, contentDescription = "Limpiar") }
                }
            }
        )

        if (searchToAddQuery.isNotEmpty()) {
            LazyColumn {
                items(globalSearchResults) { hymn ->
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable {
                        scope.launch {
                            val safeId = hymn.id ?: 0
                            listDao.addHymnToList(ListHymnCrossRef(list.listId, safeId))
                            searchToAddQuery = ""
                        }
                    }) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = PlaylistAddIcon, contentDescription = "Agregar a la lista")
                            Spacer(modifier = Modifier.padding(4.dp))
                            val safeId = hymn.id ?: 0
                            val safeTitle = hymn.title ?: "Sin título"
                            Text(text = "${safeId}. ${safeTitle}")
                        }
                    }
                }
            }
        } else {
            if (hymnsInList.isEmpty()) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No hay himnos aquí.")
                }
            } else {
                LazyColumn {
                    itemsIndexed(hymnsInList) { index, hymn ->
                        val safeId = hymn.id ?: 0
                        HymnItem(
                            hymn = hymn,
                            expanded = safeId == expandedHymnId,
                            onToggleExpand = { onToggleExpand(safeId) },
                            onNoteChange = { note -> onNoteChange(safeId, note) },
                            showAddButton = false,
                            onRemoveFromList = {
                                scope.launch { listDao.removeHymnFromList(list.listId, safeId) }
                            },
                            onMoveUp = if (canReorder) {
                                {
                                    scope.launch {
                                        moveHymn(listDao, list.listId, hymnsInList, index, -1)
                                    }
                                }
                            } else null,
                            onMoveDown = if (canReorder) {
                                {
                                    scope.launch {
                                        moveHymn(listDao, list.listId, hymnsInList, index, 1)
                                    }
                                }
                            } else null,
                            canMoveUp = index > 0,
                            canMoveDown = index < hymnsInList.size - 1
                        )
                    }
                }
            }
        }
    }

    if (showRenameDialog) {
        CreateListDialog(
            initialName = list.name,
            initialDesc = list.description,
            title = "Editar Lista",
            confirmText = "Guardar",
            onDismiss = { showRenameDialog = false },
            onConfirm = { name, desc ->
                scope.launch { listDao.updateList(list.copy(name = name, description = desc)) }
                showRenameDialog = false
            }
        )
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("¿Vaciar lista?") },
            text = { Text("Se quitarán todos los himnos de '${list.name}'.") },
            confirmButton = {
                Button(onClick = {
                    scope.launch { listDao.removeAllHymnsFromList(list.listId) }
                    showDeleteAllDialog = false
                }) { Text("Vaciar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

// Función auxiliar para reordenar
suspend fun moveHymn(
    listDao: HymnListDao,
    listId: Int,
    currentList: List<HymnEntity>,
    currentIndex: Int,
    direction: Int // -1 para subir, +1 para bajar
) {
    val refs = listDao.getCrossRefsForList(listId).toMutableList()

    // Asegurarnos de que las posiciones sean consecutivas (0, 1, 2...)
    // Esto "normaliza" la lista antes de mover
    refs.forEachIndexed { index, ref ->
        if (ref.position != index) {
            listDao.updateCrossRef(ref.copy(position = index))
        }
    }

    // Volver a leer para tener datos limpios
    val cleanRefs = listDao.getCrossRefsForList(listId).toMutableList()
    if (currentIndex + direction in cleanRefs.indices) {
        val itemA = cleanRefs[currentIndex]
        val itemB = cleanRefs[currentIndex + direction]

        // Intercambiar posiciones
        listDao.updateCrossRef(itemA.copy(position = itemB.position))
        listDao.updateCrossRef(itemB.copy(position = itemA.position))
    }
}

// Compartir himno individual
fun shareHymn(context: Context, hymn: HymnEntity) {
    val sb = StringBuilder()
    sb.append("Nº ${hymn.numero ?: "S/N"}\n")
    sb.append("Título: ${hymn.title?.trim() ?: "Sin título"}\n")
    sb.append("----------------\n")
    sb.append(hymn.lyrics?.trim() ?: "Sin letra")
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Compartir himno vía")
    context.startActivity(shareIntent)
}

// Exportar himno como PDF
fun exportHymnAsPdf(context: Context, hymn: HymnEntity) {
    val pdfDocument = android.graphics.pdf.PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas
    val paint = android.graphics.Paint()
    val margin = 40f
    var y = 60f
    
    // Título del himno
    paint.textSize = 18f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    canvas.drawText("Nº ${hymn.numero ?: "S/N"}", margin, y, paint)
    y += 30f
    
    paint.textSize = 16f
    val title = hymn.title?.trim() ?: "Sin título"
    canvas.drawText("Título: $title", margin, y, paint)
    y += 30f
    
    y += 10f
    paint.typeface = android.graphics.Typeface.DEFAULT
    paint.textSize = 14f
    
    // Letra del himno
    val lyrics = hymn.lyrics?.trim() ?: "Sin letra"
    val lines = lyrics.split('\n')
    var pageNum = 1
    
    for (line in lines) {
        if (y > pageHeight - 60f) {
            pdfDocument.finishPage(page)
            pageNum++
            val newPageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
            page = pdfDocument.startPage(newPageInfo)
            canvas = page.canvas
            y = 60f
        }
        canvas.drawText(line, margin, y, paint)
        y += 22f
    }
    
    pdfDocument.finishPage(page)
    
    val safeTitle = hymn.title?.trim()?.replace(Regex("[^\\p{L}\\p{N}\\s]"), "")?.take(50) ?: "Sin_titulo"
    val fileName = "${hymn.numero ?: "sn"}. $safeTitle.pdf"
    val file = java.io.File(context.cacheDir, fileName)
    try {
        pdfDocument.writeTo(java.io.FileOutputStream(file))
        pdfDocument.close()
        
        val uri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Compartir himno (PDF)"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Función para compartir texto
fun shareListAsText(context: Context, list: HymnListEntity, hymns: List<HymnEntity>) {
    val sb = StringBuilder()
    sb.append("LISTA: ${list.name}\n")
    sb.append("----------------\n\n")

    hymns.forEachIndexed { index, hymn ->
        sb.append("${index + 1}. Nº ${hymn.numero ?: "S/N"} - ${hymn.title?.trim() ?: "Sin título"}\n")
        sb.append(hymn.lyrics?.trim() ?: "Sin letra")
        sb.append("\n\n")
    }

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Compartir lista vía")
    context.startActivity(shareIntent)
}

// Exportar lista como PDF
fun exportListAsPdf(context: Context, list: HymnListEntity, hymns: List<HymnEntity>) {
    val pdfDocument = android.graphics.pdf.PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val margin = 40f
    var pageNum = 1
    
    var pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas
    val paint = android.graphics.Paint()
    var y = 60f
    
    // Título de la lista
    paint.textSize = 18f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    canvas.drawText("LISTA: ${list.name}", margin, y, paint)
    y += 30f
    
    paint.textSize = 14f
    paint.typeface = android.graphics.Typeface.DEFAULT
    y += 10f
    
    // Himnos en la lista
    hymns.forEachIndexed { index, hymn ->
        if (y > pageHeight - 100f) {
            pdfDocument.finishPage(page)
            pageNum++
            pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            y = 60f
        }
        
        paint.textSize = 16f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("${index + 1}. Nº ${hymn.numero ?: "S/N"} - ${hymn.title?.trim() ?: "Sin título"}", margin, y, paint)
        y += 24f
        
        paint.textSize = 12f
        paint.typeface = android.graphics.Typeface.DEFAULT
        
        val lyrics = hymn.lyrics?.trim() ?: "Sin letra"
        val lines = lyrics.split('\n')
        for (line in lines) {
            if (y > pageHeight - 60f) {
                pdfDocument.finishPage(page)
                pageNum++
                pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 60f
            }
            canvas.drawText(line, margin, y, paint)
            y += 18f
        }
        y += 20f
    }
    
    pdfDocument.finishPage(page)
    
    val safeName = list.name.replace(Regex("[^\\p{L}\\p{N}\\s]"), "").take(50)
    val fileName = "$safeName.pdf"
    val file = java.io.File(context.cacheDir, fileName)
    try {
        pdfDocument.writeTo(java.io.FileOutputStream(file))
        pdfDocument.close()
        
        val uri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Compartir lista (PDF)"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun CreateListDialog(
    initialName: String = "",
    initialDesc: String? = null,
    title: String = "Nueva Lista",
    confirmText: String = "Crear",
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var desc by remember { mutableStateOf(initialDesc ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la lista") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Comentario / Descripción (Opcional)") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, desc.ifBlank { null }) }, enabled = name.isNotBlank()) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddHymnToListDialog(
    lists: List<HymnListEntity>,
    onDismiss: () -> Unit,
    onListSelected: (HymnListEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar a lista") },
        text = {
            LazyColumn {
                items(lists) { list ->
                    TextButton(
                        onClick = { onListSelected(list) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(list.name)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    hymnDao: HymnDao,
    onHymnClick: (Int) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedKey by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Adoración", "Alabanza", "Himno", "Cántico")
    val keys = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    val filteredHymns by hymnDao.filterHymns(selectedCategory, selectedKey)
        .collectAsState(initial = emptyList())

    var expandedHymnId by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Filtrar Himnos", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // FILTROS
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Dropdown Categoría
            FilterChip(
                selected = selectedCategory != null,
                onClick = { /* Lógica simple */ },
                label = { Text(selectedCategory ?: "Categoría") },
                trailingIcon = {
                    if (selectedCategory != null) {
                        Icon(Icons.Default.Close, "Borrar", Modifier.clickable { selectedCategory = null })
                    }
                },
                modifier = Modifier.weight(1f)
            )

            // Dropdown Nota
            FilterChip(
                selected = selectedKey != null,
                onClick = { /* Lógica simple */ },
                label = { Text(selectedKey ?: "Nota Musical") },
                trailingIcon = {
                    if (selectedKey != null) {
                        Icon(Icons.Default.Close, "Borrar", Modifier.clickable { selectedKey = null })
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        // MENÚS DESPLEGABLES
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            DropdownSelector("Categoría", categories) { selectedCategory = it }
            DropdownSelector("Nota", keys) { selectedKey = it }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // RESULTADOS
        if (filteredHymns.isEmpty()) {
            Text("No se encontraron himnos con estos filtros.", modifier = Modifier.padding(top = 20.dp))
        } else {
            LazyColumn {
                items(filteredHymns) { hymn ->
                    val safeId = hymn.id ?: 0
                    HymnItem(
                        hymn = hymn,
                        expanded = safeId == expandedHymnId,
                        onToggleExpand = { expandedHymnId = if (expandedHymnId == safeId) null else safeId },
                        onNoteChange = { note ->
                            scope.launch { hymnDao.updateNote(safeId, note) }
                        },
                        showAddButton = false
                    )
                }
            }
        }
    }
}

@Composable
fun DropdownSelector(label: String, options: List<String>, onSelection: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text(label)
            Icon(Icons.Default.KeyboardArrowDown, null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelection(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// DEFINICIÓN MANUAL DEL ICONO PLAYLIST_ADD
val PlaylistAddIcon: ImageVector
    get() {
        if (_playlistAdd != null) {
            return _playlistAdd!!
        }
        _playlistAdd = materialIcon(name = "PlaylistAdd") {
            materialPath {
                moveTo(14.0f, 10.0f)
                horizontalLineTo(2.0f)
                verticalLineTo(12.0f)
                horizontalLineTo(14.0f)
                verticalLineTo(10.0f)
                close()
                moveTo(14.0f, 6.0f)
                horizontalLineTo(2.0f)
                verticalLineTo(8.0f)
                horizontalLineTo(14.0f)
                verticalLineTo(6.0f)
                close()
                moveTo(2.0f, 16.0f)
                horizontalLineTo(10.0f)
                verticalLineTo(14.0f)
                horizontalLineTo(2.0f)
                verticalLineTo(16.0f)
                close()
                moveTo(16.0f, 16.0f)
                verticalLineTo(12.0f)
                horizontalLineTo(18.0f)
                verticalLineTo(16.0f)
                horizontalLineTo(22.0f)
                verticalLineTo(18.0f)
                horizontalLineTo(18.0f)
                verticalLineTo(22.0f)
                horizontalLineTo(16.0f)
                verticalLineTo(18.0f)
                horizontalLineTo(12.0f)
                verticalLineTo(16.0f)
                horizontalLineTo(16.0f)
                close()
            }
        }
        return _playlistAdd!!
    }

private var _playlistAdd: ImageVector? = null
