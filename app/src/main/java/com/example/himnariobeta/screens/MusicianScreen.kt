package com.example.himnariobeta.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.himnariobeta.HymnEntity
import com.example.himnariobeta.MusicianViewModel
import com.example.himnariobeta.getSafeLyrics
import com.example.himnariobeta.getSafeTitle
import com.example.himnariobeta.utils.ChordTransposer
import com.example.himnariobeta.utils.exportHymnAsPdf
import com.example.himnariobeta.utils.exportHymnWithChordsAsPdf
import com.example.himnariobeta.utils.shareHymn
import com.example.himnariobeta.utils.shareHymnWithChords

@Composable
fun MusicianScreen(viewModel: MusicianViewModel) {
    val selectedKey by viewModel.selectedKey.collectAsState()
    val hymnsWithChords by viewModel.hymnsWithChords.collectAsState()
    val expandedHymnId by viewModel.expandedHymnId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("Modo Músicos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Selecciona una tonalidad y visualiza los acordes transpuestos automáticamente",
            style = MaterialTheme.typography.bodySmall, 
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        KeySelector(selectedKey, viewModel.availableKeys, 
            { viewModel.setSelectedKey(it) }, { viewModel.resetToOriginalKey() })
        
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        
        if (isSearchActive) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Buscar himno...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Star, null) },
                trailingIcon = { 
                    IconButton(onClick = { viewModel.setSearchActive(false) }) { 
                        Icon(Icons.Default.Close, null) 
                    } 
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${hymnsWithChords.size} himnos con acordes disponibles",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (!isSearchActive) {
                TextButton(onClick = { viewModel.setSearchActive(true) }) { 
                    Text("Buscar") 
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (hymnsWithChords.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "No hay himnos con acordes disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hymnsWithChords, key = { it.id ?: 0 }) { hymn ->
                    MusicianHymnItem(
                        hymn = hymn,
                        selectedKey = selectedKey,
                        expanded = hymn.id == expandedHymnId,
                        onToggleExpand = { viewModel.toggleHymnExpansion(hymn.id ?: 0) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeySelector(selectedKey: String?, availableKeys: List<String>,
    onKeySelected: (String) -> Unit, onResetToOriginal: () -> Unit) {
    var showKeysDialog by remember { mutableStateOf(false) }
    
    Column {
        // Dividido en 2: mensaje y botones
        Surface(
            color = if (selectedKey == null) 
                MaterialTheme.colorScheme.tertiaryContainer 
            else 
                MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Parte 1: Mensaje de tonalidad
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (selectedKey == null) 
                            MaterialTheme.colorScheme.onTertiaryContainer 
                        else 
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (selectedKey == null) 
                            "Tonalidad original" 
                        else 
                            "¡Transpuesto! Todos a: $selectedKey",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedKey == null) 
                            MaterialTheme.colorScheme.onTertiaryContainer
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Parte 2: Botones de acción
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilledTonalButton(
                        onClick = { showKeysDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) { 
                        Text(
                            text = "Transportar a",
                            style = MaterialTheme.typography.labelMedium
                        ) 
                    }
                    
                    if (selectedKey != null) {
                        TextButton(
                            onClick = onResetToOriginal,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) { 
                            Text(
                                text = "Restaurar",
                                style = MaterialTheme.typography.labelMedium
                            ) 
                        }
                    }
                }
            }
        }
    }
    
    // Diálogo con cuadrícula 4x4 de tonalidades
    if (showKeysDialog) {
        AlertDialog(
            onDismissRequest = { showKeysDialog = false },
            title = { 
                Text(
                    text = "Transportar a:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    availableKeys.chunked(4).forEach { rowKeys ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowKeys.forEach { key ->
                                val keySimple = key.substringBefore("/")
                                val isSelected = selectedKey == keySimple
                                
                                Button(
                                    onClick = {
                                        onKeySelected(keySimple)
                                        showKeysDialog = false
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) 
                                            MaterialTheme.colorScheme.primaryContainer 
                                        else 
                                            MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (isSelected) 
                                            MaterialTheme.colorScheme.onPrimaryContainer 
                                        else 
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = MaterialTheme.shapes.medium,
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = keySimple,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                        )
                                        if (key.contains("/")) {
                                            Text(
                                                text = key.substringAfter("/"),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                            // Rellenar espacios vacíos si la fila no está completa
                            repeat(4 - rowKeys.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showKeysDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun MusicianHymnItem(hymn: HymnEntity, selectedKey: String?, expanded: Boolean,
    onToggleExpand: () -> Unit) {
    val effectiveKey = selectedKey ?: hymn.musical_key ?: "C"
    val isOriginalKey = selectedKey == null
    
    // Transponer acordes basado en la tonalidad seleccionada
    val transposedChords = remember(selectedKey, hymn.musical_notation) {
        ChordTransposer.transpose(hymn.musical_notation, effectiveKey)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(
            spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMediumLow)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onToggleExpand),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text("${hymn.id}. ${hymn.getSafeTitle()}",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    if (!expanded) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tonalidad: $effectiveKey",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            if (isOriginalKey) {
                                Text("• Original", style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
            
            AnimatedVisibility(expanded,
                enter = fadeIn(tween(300)) + expandVertically(spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)),
                exit = fadeOut(tween(250)) + shrinkVertically(spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium))) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        color = if (isOriginalKey) MaterialTheme.colorScheme.tertiaryContainer 
                            else MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isOriginalKey) MaterialTheme.colorScheme.onTertiaryContainer
                                    else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = if (isOriginalKey) "Tonalidad Original: $effectiveKey" else "Transpuesto a: $effectiveKey",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isOriginalKey) MaterialTheme.colorScheme.onTertiaryContainer
                                    else MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    if (transposedChords.isNotBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = transposedChords,
                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(16.dp))
                    Text("LETRA:", style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(hymn.getSafeLyrics(), style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                    
                    val context = LocalContext.current
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                shareHymnWithChords(
                                    context = context,
                                    hymn = hymn,
                                    transposedChords = transposedChords,
                                    tonality = effectiveKey,
                                    isOriginal = isOriginalKey
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Compartir") }
                        OutlinedButton(
                            onClick = { 
                                exportHymnWithChordsAsPdf(
                                    context = context,
                                    hymn = hymn,
                                    transposedChords = transposedChords,
                                    tonality = effectiveKey,
                                    isOriginal = isOriginalKey
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("PDF") }
                    }
                }
            }
        }
    }
}
