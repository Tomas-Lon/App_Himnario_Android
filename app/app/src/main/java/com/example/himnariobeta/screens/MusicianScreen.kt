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
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Modo Músicos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Selecciona una tonalidad y visualiza los acordes transpuestos automáticamente",
            style = MaterialTheme.typography.bodySmall, 
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        KeySelector(selectedKey, viewModel.availableKeys, 
            { viewModel.setSelectedKey(it) }, { viewModel.resetToOriginalKey() })
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        
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
            Spacer(modifier = Modifier.height(8.dp))
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
    Column {
        // Sección 1: Nota Original / Transpuesto
        Surface(
            color = if (selectedKey == null) 
                MaterialTheme.colorScheme.tertiaryContainer 
            else 
                MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(if (selectedKey == null) 24.dp else 32.dp),
                        tint = if (selectedKey == null) 
                            MaterialTheme.colorScheme.onTertiaryContainer 
                        else 
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedKey == null) "Nota Original" else "¡Transpuesto!",
                            style = if (selectedKey == null) 
                                MaterialTheme.typography.labelSmall 
                            else 
                                MaterialTheme.typography.labelMedium,
                            color = if (selectedKey == null) 
                                MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = if (selectedKey == null) FontWeight.Normal else FontWeight.Bold
                        )
                        Text(
                            text = if (selectedKey == null) 
                                "Usando tonalidad original de cada himno" 
                            else 
                                "Todos los himnos transpuestos a: $selectedKey",
                            style = if (selectedKey == null) 
                                MaterialTheme.typography.titleMedium 
                            else 
                                MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedKey == null) 
                                MaterialTheme.colorScheme.onTertiaryContainer
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                if (selectedKey != null) {
                    FilledTonalButton(
                        onClick = onResetToOriginal,
                        modifier = Modifier.fillMaxWidth()
                    ) { 
                        Text("Restaurar a tonalidad original") 
                    }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        
        // Sección 2: Transportar a
        Text(
            text = "Transportar a:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(12.dp))
        
        // Grid de tonalidades
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableKeys.chunked(4).forEach { rowKeys ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowKeys.forEach { key ->
                        val keySimple = key.substringBefore("/")
                        FilterChip(
                            selected = selectedKey == keySimple,
                            onClick = { onKeySelected(keySimple) },
                            label = { 
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selectedKey == keySimple) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Rellenar espacios vacíos si la fila no está completa
                    repeat(4 - rowKeys.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
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
