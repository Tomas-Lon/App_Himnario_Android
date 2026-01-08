package com.example.himnariobeta

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/* ----------------------------- */
/* -------- FOLDERS LIST -------- */
/* ----------------------------- */

@Composable
fun FoldersScreen(
    folders: List<FolderEntity>,
    lists: List<HymnListEntity>,
    onFolderClick: (FolderEntity) -> Unit,
    onDeleteFolder: (FolderEntity) -> Unit,
    onListClick: (HymnListEntity) -> Unit,
    onDeleteList: (HymnListEntity) -> Unit,
    onToggleFavorite: (HymnListEntity) -> Unit,
    allFolders: List<FolderEntity>,
    onMoveListToFolder: (HymnListEntity, FolderEntity?) -> Unit
) {
    val noFolderLists = remember(lists) { lists.filter { it.folderId == null } }

    if (folders.isEmpty() && noFolderLists.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay carpetas ni listas creadas.", style = MaterialTheme.typography.titleMedium)
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        items(folders, key = { it.folderId }) { folder ->
            val listsInFolder = remember(lists) {
                lists.filter { it.folderId == folder.folderId }
            }

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onFolderClick(folder) }
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(folder.name, style = MaterialTheme.typography.titleMedium)
                        folder.description?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            if (listsInFolder.isNotEmpty())
                                "${listsInFolder.size} listas"
                            else "Sin listas",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    IconButton(onClick = { onDeleteFolder(folder) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar carpeta")
                    }
                }
            }
            }
        }

        if (noFolderLists.isNotEmpty()) {
            item {
                Text(
                    "Listas sin carpeta",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }

            items(noFolderLists, key = { "nofolder_${it.listId}" }) { list ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300)) + 
                            expandVertically(animationSpec = tween(300)) +
                            scaleIn(initialScale = 0.8f, animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(200)) + 
                           shrinkVertically(animationSpec = tween(200)) +
                           scaleOut(targetScale = 0.8f, animationSpec = tween(200))
                ) {
                    ListCard(
                        list = list,
                        onClick = onListClick,
                        onDelete = onDeleteList,
                        onToggleFavorite = onToggleFavorite,
                        allFolders = allFolders,
                        onMoveListToFolder = onMoveListToFolder
                    )
                }
            }
        }
    }
}

/* ----------------------------- */
/* -------- SINGLE FOLDER ------- */
/* ----------------------------- */

@Composable
fun FolderScreen(
    folder: FolderEntity,
    lists: List<HymnListEntity>,
    allFolders: List<FolderEntity>,
    onListClick: (HymnListEntity) -> Unit,
    onDeleteList: (HymnListEntity) -> Unit,
    onToggleFavorite: (HymnListEntity) -> Unit,
    onDeleteFolder: (FolderEntity) -> Unit,
    onEditFolder: (FolderEntity, String, String?) -> Unit,
    onMoveListToFolder: (HymnListEntity, FolderEntity?) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(folder.name, style = MaterialTheme.typography.headlineSmall)
            Row {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar carpeta")
                }
                IconButton(onClick = { onDeleteFolder(folder) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar carpeta")
                }
            }
        }

        if (lists.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No hay listas en esta carpeta.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn {
                items(lists, key = { "folder_${folder.folderId}_${it.listId}" }) { list ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(300)) + 
                                expandVertically(animationSpec = tween(300)) +
                                scaleIn(initialScale = 0.8f, animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(200)) + 
                               shrinkVertically(animationSpec = tween(200)) +
                               scaleOut(targetScale = 0.8f, animationSpec = tween(200))
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { onListClick(list) }
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(list.name, style = MaterialTheme.typography.titleMedium)
                                list.description?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Row {
                                IconButton(onClick = { onDeleteList(list) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar lista")
                                }
                                IconButton(onClick = { onToggleFavorite(list) }) {
                                    Icon(
                                        if (list.isFavorite)
                                            Icons.Default.Favorite
                                        else
                                            Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorito"
                                    )
                                }
                                FolderMoveDropdown(
                                    list = list,
                                    allFolders = allFolders,
                                    onMove = { onMoveListToFolder(list, it) }
                                )
                            }
                        }
                    }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        EditFolderDialog(
            initialName = folder.name,
            initialDesc = folder.description,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, desc ->
                onEditFolder(folder, name, desc)
                showEditDialog = false
            }
        )
    }
}

/* ----------------------------- */
/* -------- MOVE DROPDOWN ------- */
/* ----------------------------- */

@Composable
fun FolderMoveDropdown(
    list: HymnListEntity,
    allFolders: List<FolderEntity>,
    onMove: (FolderEntity?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.List, contentDescription = "Mover lista")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sin carpeta") },
                onClick = {
                    onMove(null)
                    expanded = false
                }
            )

            allFolders.forEach { folder ->
                DropdownMenuItem(
                    text = { Text(folder.name) },
                    onClick = {
                        onMove(folder)
                        expanded = false
                    }
                )
            }
        }
    }
}

/* ----------------------------- */
/* -------- EDIT DIALOG --------- */
/* ----------------------------- */

@Composable
fun EditFolderDialog(
    initialName: String,
    initialDesc: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var desc by remember { mutableStateOf(initialDesc ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar carpeta") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, desc.ifBlank { null }) },
                enabled = name.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/* ----------------------------- */
/* -------- LIST CARD ----------- */
/* ----------------------------- */

@Composable
private fun ListCard(
    list: HymnListEntity,
    onClick: (HymnListEntity) -> Unit,
    onDelete: (HymnListEntity) -> Unit,
    onToggleFavorite: (HymnListEntity) -> Unit,
    allFolders: List<FolderEntity>? = null,
    onMoveListToFolder: ((HymnListEntity, FolderEntity?) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(list) }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(list.name, style = MaterialTheme.typography.titleMedium)
                list.description?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
            }
            Row {
                IconButton(onClick = { onDelete(list) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
                IconButton(onClick = { onToggleFavorite(list) }) {
                    Icon(
                        if (list.isFavorite)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito"
                    )
                }
                if (allFolders != null && onMoveListToFolder != null) {
                    FolderMoveDropdown(
                        list = list,
                        allFolders = allFolders,
                        onMove = { folder -> onMoveListToFolder(list, folder) }
                    )
                }
            }
        }
    }
}
