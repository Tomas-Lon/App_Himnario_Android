package com.example.himnariobeta.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.himnariobeta.FolderEntity
import com.example.himnariobeta.HymnListEntity

/* ---------------------------------------------------
 *  Crear Carpeta
 * --------------------------------------------------- */

@Composable
fun CreateFolderDialog(
    initialName: String = "",
    initialDesc: String? = null,
    title: String = "Nueva carpeta",
    confirmText: String = "Crear",
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var desc by remember { mutableStateOf(initialDesc.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la carpeta") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción (opcional)") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, desc.ifBlank { null }) },
                enabled = name.isNotBlank()
            ) {
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

/* ---------------------------------------------------
 *  Crear Lista
 * --------------------------------------------------- */

@Composable
fun CreateListDialog(
    initialName: String = "",
    initialDesc: String? = null,
    title: String = "Nueva lista",
    confirmText: String = "Crear",
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var desc by remember { mutableStateOf(initialDesc.orEmpty()) }

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
                    label = { Text("Descripción (opcional)") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, desc.ifBlank { null }) },
                enabled = name.isNotBlank()
            ) {
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

/* ---------------------------------------------------
 *  Agregar Himno a Lista
 * --------------------------------------------------- */

@Composable
fun AddHymnToListDialog(
    lists: List<HymnListEntity>,
    folders: List<FolderEntity>,
    onDismiss: () -> Unit,
    onListSelected: (HymnListEntity) -> Unit
) {
    val uniqueLists = lists.distinctBy { it.listId }
    val noFolderLists = uniqueLists.filter { it.folderId == null }

    var expandedFolders by remember {
        mutableStateOf<Set<Int>>(emptySet())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar a lista") },
        text = {
            if (uniqueLists.isEmpty()) {
                Text("No hay listas disponibles. Crea una lista primero.")
            } else {
                LazyColumn {

                    folders.forEach { folder ->
                        val listsInFolder =
                            uniqueLists.filter { it.folderId == folder.folderId }

                        item(key = folder.folderId) {
                            val isExpanded = expandedFolders.contains(folder.folderId)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedFolders =
                                            if (isExpanded) {
                                                expandedFolders - folder.folderId
                                            } else {
                                                expandedFolders + folder.folderId
                                            }
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isExpanded)
                                        Icons.Default.KeyboardArrowDown
                                    else
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null
                                )
                                Text(
                                    text = "${folder.name} (${listsInFolder.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        if (expandedFolders.contains(folder.folderId)) {
                            items(listsInFolder) { list ->
                                TextButton(
                                    onClick = { onListSelected(list) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 32.dp)
                                ) {
                                    Text(list.name)
                                }
                            }
                        }
                    }

                    if (noFolderLists.isNotEmpty()) {
                        item {
                            Text(
                                text = "Sin carpeta",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(noFolderLists) { list ->
                            TextButton(
                                onClick = { onListSelected(list) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(list.name)
                            }
                        }
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
