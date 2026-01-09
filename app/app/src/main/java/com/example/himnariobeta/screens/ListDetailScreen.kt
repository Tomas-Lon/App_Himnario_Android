package com.example.himnariobeta.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import com.example.himnariobeta.HymnDao
import com.example.himnariobeta.HymnEntity
import com.example.himnariobeta.HymnListDao
import com.example.himnariobeta.HymnListEntity
import com.example.himnariobeta.ListHymnCrossRef
import com.example.himnariobeta.components.CreateListDialog
import com.example.himnariobeta.components.HymnItem
import com.example.himnariobeta.components.PlaylistAddIcon
import com.example.himnariobeta.utils.exportListAsPdf
import com.example.himnariobeta.utils.shareListAsText
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListDetailScreen(
    list: HymnListEntity,
    listDao: HymnListDao,
    hymnDao: HymnDao,
    expandedHymnId: Int?,
    onToggleExpand: (Int) -> Unit,
    onNoteChange: (Int, String) -> Unit,
    onDuplicateList: () -> Unit,
    repository: com.example.himnariobeta.HymnRepository
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

    // Estados para drag & drop
    var draggingItemId by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }
    var dragStartIndex by remember { mutableStateOf<Int?>(null) }
    var currentDragIndex by remember { mutableStateOf<Int?>(null) }
    
    val canReorder = hymnsInList.size > 1
    
    var showSearchInList by remember { mutableStateOf(false) }
    var showAddToList by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Header dividido en 2 áreas: información y botones
        androidx.compose.material3.Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Parte 1: Información de la lista
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${hymnsInList.size} himnos",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Parte 2: Botones de acción
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    androidx.compose.material3.FilledTonalButton(
                        onClick = { showSearchInList = !showSearchInList },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    androidx.compose.material3.FilledTonalButton(
                        onClick = { showAddToList = !showAddToList },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Más opciones"
                        )
                    }
                }
            }
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

        // Campos de búsqueda expandibles
        if (showSearchInList) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar en esta lista", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                     if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Close, contentDescription = "Limpiar") }
                    }
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }
        
        if (showAddToList) {
            OutlinedTextField(
                value = searchToAddQuery,
                onValueChange = { searchToAddQuery = it },
                placeholder = { Text("Agregar himno a la lista", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                trailingIcon = {
                    if (searchToAddQuery.isNotEmpty()) {
                        IconButton(onClick = { searchToAddQuery = "" }) { Icon(Icons.Default.Close, contentDescription = "Limpiar") }
                    }
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }
        
        if (showSearchInList || showAddToList) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }

        if (searchToAddQuery.isNotEmpty()) {
            LazyColumn(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                items(globalSearchResults) { hymn ->
                    androidx.compose.material3.Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                scope.launch {
                                    val safeId = hymn.id ?: 0
                                    repository.addHymnToList(list.listId, safeId)
                                    searchToAddQuery = ""
                                }
                            },
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = PlaylistAddIcon,
                                contentDescription = "Agregar a la lista",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            val safeId = hymn.id ?: 0
                            val safeTitle = hymn.title ?: "Sin título"
                            Text(
                                text = "${safeId}. ${safeTitle}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
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
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(
                        items = hymnsInList,
                        key = { _, hymn -> hymn.id ?: 0 }
                    ) { index, hymn ->
                        val safeId = hymn.id ?: 0
                        val isDragging = draggingItemId == safeId
                        
                        // Calcular desplazamiento visual para separar items
                        val visualOffset = when {
                            !isDragging && dragStartIndex != null && currentDragIndex != null -> {
                                when {
                                    // Items entre la posición inicial y actual se desplazan
                                    dragStartIndex!! < currentDragIndex!! && index > dragStartIndex!! && index <= currentDragIndex!! -> -100f
                                    dragStartIndex!! > currentDragIndex!! && index >= currentDragIndex!! && index < dragStartIndex!! -> 100f
                                    else -> 0f
                                }
                            }
                            else -> 0f
                        }
                        
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300)) + 
                                    expandVertically(animationSpec = tween(300)) +
                                    scaleIn(initialScale = 0.8f, animationSpec = tween(300)),
                            exit = fadeOut(animationSpec = tween(200)) + 
                                   shrinkVertically(animationSpec = tween(200)) +
                                   scaleOut(targetScale = 0.8f, animationSpec = tween(200))
                        ) {
                            HymnItem(
                                hymn = hymn,
                                expanded = safeId == expandedHymnId,
                                onToggleExpand = { onToggleExpand(safeId) },
                                onNoteChange = { note -> onNoteChange(safeId, note) },
                                showAddButton = false,
                                onRemoveFromList = {
                                    scope.launch { listDao.removeHymnFromList(list.listId, safeId) }
                                },
                                onMoveUp = if (canReorder && index > 0) {
                                    {
                                        scope.launch {
                                            moveHymn(listDao, list.listId, hymnsInList, index, -1)
                                        }
                                    }
                                } else null,
                                onMoveDown = if (canReorder && index < hymnsInList.size - 1) {
                                    {
                                        scope.launch {
                                            moveHymn(listDao, list.listId, hymnsInList, index, 1)
                                        }
                                    }
                                } else null,
                                modifier = Modifier
                                    .zIndex(if (isDragging) 1f else 0f)
                                    .graphicsLayer {
                                        translationY = if (isDragging) dragOffset else visualOffset
                                        shadowElevation = if (isDragging) 16f else 0f
                                        alpha = if (isDragging) 0.85f else 1f
                                        scaleX = if (isDragging) 1.05f else 1f
                                        scaleY = if (isDragging) 1.05f else 1f
                                    }
                                    .then(
                                        if (canReorder) {
                                            Modifier.pointerInput(safeId) {
                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = {
                                                        draggingItemId = safeId
                                                        dragStartIndex = index
                                                        currentDragIndex = index
                                                        dragOffset = 0f
                                                    },
                                                    onDrag = { change, dragAmount ->
                                                        change.consume()
                                                        dragOffset += dragAmount.y
                                                        
                                                        // Calcular nuevo índice basado en offset total
                                                        val itemHeight = 100f
                                                        val offsetInItems = (dragOffset / itemHeight).toInt()
                                                        val newIndex = (dragStartIndex!! + offsetInItems).coerceIn(0, hymnsInList.size - 1)
                                                        
                                                        if (newIndex != currentDragIndex) {
                                                            currentDragIndex = newIndex
                                                        }
                                                    },
                                                    onDragEnd = {
                                                    // Actualizar BD solo al soltar
                                                    val startIdx = dragStartIndex
                                                    val endIdx = currentDragIndex
                                                    
                                                    if (startIdx != null && endIdx != null && startIdx != endIdx) {
                                                        scope.launch {
                                                            reorderHymn(listDao, list.listId, hymnsInList, startIdx, endIdx)
                                                        }
                                                    }
                                                    
                                                    draggingItemId = null
                                                    dragOffset = 0f
                                                    dragStartIndex = null
                                                    currentDragIndex = null
                                                },
                                                onDragCancel = {
                                                    draggingItemId = null
                                                    dragOffset = 0f
                                                    dragStartIndex = null
                                                    currentDragIndex = null
                                                }
                                            )
                                        }
                                    } else Modifier
                                )
                            )
                        }
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

// Función para reordenar con drag & drop
suspend fun reorderHymn(
    listDao: HymnListDao,
    listId: Int,
    currentList: List<HymnEntity>,
    fromIndex: Int,
    toIndex: Int
) {
    val refs = listDao.getCrossRefsForList(listId).toMutableList()
    
    // Asegurarnos de que las posiciones sean consecutivas
    refs.forEachIndexed { index, ref ->
        if (ref.position != index) {
            listDao.updateCrossRef(ref.copy(position = index))
        }
    }
    
    // Volver a leer para tener datos limpios
    val cleanRefs = listDao.getCrossRefsForList(listId).toMutableList()
    
    // Mover el elemento de fromIndex a toIndex
    if (fromIndex != toIndex && fromIndex in cleanRefs.indices && toIndex in cleanRefs.indices) {
        val movedItem = cleanRefs.removeAt(fromIndex)
        cleanRefs.add(toIndex, movedItem)
        
        // Actualizar todas las posiciones
        cleanRefs.forEachIndexed { index, ref ->
            listDao.updateCrossRef(ref.copy(position = index))
        }
    }
}

// Función auxiliar para reordenar (legacy - usar reorderHymn para drag & drop)
suspend fun moveHymn(
    listDao: HymnListDao,
    listId: Int,
    currentList: List<HymnEntity>,
    currentIndex: Int,
    direction: Int // -1 para subir, +1 para bajar
) {
    val refs = listDao.getCrossRefsForList(listId).toMutableList()

    // Asegurarnos de que las posiciones sean consecutivas (0, 1, 2...)
    refs.forEachIndexed { index, ref ->
        if (ref.position != index) {
            listDao.updateCrossRef(ref.copy(position = index))
        }
    }

    // Volver a leer para tener datos limpios
    val cleanRefs = listDao.getCrossRefsForList(listId).toMutableList()
    
    // Calcular el target index
    val targetIndex = currentIndex + direction
    if (targetIndex in cleanRefs.indices) {
        val temp = cleanRefs[currentIndex]
        cleanRefs[currentIndex] = cleanRefs[targetIndex]
        cleanRefs[targetIndex] = temp
        
        // Actualizar las posiciones intercambiadas
        listDao.updateCrossRef(cleanRefs[currentIndex].copy(position = currentIndex))
        listDao.updateCrossRef(cleanRefs[targetIndex].copy(position = targetIndex))
    }
}
