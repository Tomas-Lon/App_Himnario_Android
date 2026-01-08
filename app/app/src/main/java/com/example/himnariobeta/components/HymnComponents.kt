package com.example.himnariobeta.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.himnariobeta.HymnEntity
import com.example.himnariobeta.getAmericanKey
import com.example.himnariobeta.getSafeLyrics
import com.example.himnariobeta.getSafeTitle
import com.example.himnariobeta.utils.exportHymnAsPdf
import com.example.himnariobeta.utils.shareHymn

// DEFINICIÓN MANUAL DEL ICONO PLAYLIST_ADD
val PlaylistAddIcon: ImageVector
    get() {
        if (_playlistAdd != null) {
            return _playlistAdd!!
        }
        _playlistAdd = ImageVector.Builder(
            name = "PlaylistAdd",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
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
        }.build()
        return _playlistAdd!!
    }

private var _playlistAdd: ImageVector? = null

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
    compactButtons: Boolean = false,
    modifier: Modifier = Modifier,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null
) {
    val safeTitle = hymn.getSafeTitle()
    val safeId = hymn.id ?: 0
    val safeLyrics = hymn.getSafeLyrics()

    val buttonSize = if (compactButtons) 28.dp else 40.dp
    val iconSize = if (compactButtons) 18.dp else 24.dp
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = if (compactButtons) 4.dp else 8.dp, vertical = 6.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
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
                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                    Text(
                        text = "${safeId}. ${safeTitle}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    hymn.getAmericanKey()?.let { key ->
                        Text(
                            text = "Tonalidad: $key",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showAddButton && onAddToList != null) {
                        IconButton(
                            onClick = onAddToList,
                            modifier = Modifier.size(buttonSize)
                        ) {
                            Icon(
                                PlaylistAddIcon, 
                                contentDescription = "Agregar a lista", 
                                modifier = Modifier.size(iconSize),
                                tint = MaterialTheme.colorScheme.primary
                            )
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
                    
                    // Botones de reordenamiento mejorados
                    if (onMoveUp != null || onMoveDown != null) {
                        Column {
                            IconButton(
                                onClick = { onMoveUp?.invoke() },
                                enabled = onMoveUp != null,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Subir",
                                    modifier = Modifier.size(16.dp),
                                    tint = if (onMoveUp != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            }
                            IconButton(
                                onClick = { onMoveDown?.invoke() },
                                enabled = onMoveDown != null,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Bajar",
                                    modifier = Modifier.size(16.dp),
                                    tint = if (onMoveDown != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(300)) + 
                        expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ),
                exit = fadeOut(animationSpec = tween(250)) + 
                       shrinkVertically(
                           animationSpec = spring(
                               dampingRatio = Spring.DampingRatioNoBouncy,
                               stiffness = Spring.StiffnessMedium
                           )
                       )
            ) {
                Column {
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
}
