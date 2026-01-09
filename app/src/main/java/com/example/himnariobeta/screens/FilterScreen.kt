package com.example.himnariobeta.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.himnariobeta.FilterViewModel
import com.example.himnariobeta.components.HymnItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    viewModel: FilterViewModel
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedKey by viewModel.selectedKey.collectAsState()
    val filteredHymns by viewModel.filteredHymns.collectAsState()
    val expandedHymnId by viewModel.expandedHymnId.collectAsState()
    val categoryMenuExpanded by viewModel.categoryMenuExpanded.collectAsState()
    val keyMenuExpanded by viewModel.keyMenuExpanded.collectAsState()
    
    var showKeysDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {
        Text("Filtros", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Bloque compacto dividido en 2: Filtros activos y botones de acción
        Surface(
            color = if (selectedCategory != null || selectedKey != null)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Parte 1: Información de filtros activos
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (selectedCategory != null || selectedKey != null)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (selectedCategory != null || selectedKey != null) {
                            if (selectedCategory != null) {
                                Text(
                                    text = "Categoría: $selectedCategory",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            if (selectedKey != null) {
                                Text(
                                    text = "Tonalidad: $selectedKey",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        } else {
                            Text(
                                text = "Sin filtros activos",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                // Parte 2: Botones de filtro
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Botón Categoría
                    Box {
                        FilledTonalButton(
                            onClick = { viewModel.toggleCategoryMenu() },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Categoría",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        
                        DropdownMenu(
                            expanded = categoryMenuExpanded,
                            onDismissRequest = { viewModel.closeCategoryMenu() }
                        ) {
                            viewModel.categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        viewModel.setCategory(category)
                                        viewModel.closeCategoryMenu()
                                    }
                                )
                            }
                        }
                    }
                    
                    // Botón Tonalidad
                    FilledTonalButton(
                        onClick = { showKeysDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Tonalidad",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    
                    // Botón limpiar filtros (icono)
                    if (selectedCategory != null || selectedKey != null) {
                        TextButton(
                            onClick = {
                                viewModel.clearCategory()
                                viewModel.clearKey()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        // RESULTADOS
        if (filteredHymns.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedCategory == null && selectedKey == null) {
                        "Selecciona un filtro para ver resultados"
                    } else {
                        "No se encontraron himnos con estos filtros"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            LazyColumn {
                items(filteredHymns) { hymn ->
                    val safeId = hymn.id ?: 0
                    HymnItem(
                        hymn = hymn,
                        expanded = safeId == expandedHymnId,
                        onToggleExpand = { viewModel.toggleHymnExpansion(safeId) },
                        onNoteChange = { note ->
                            viewModel.updateHymnNote(safeId, note)
                        },
                        showAddButton = false
                    )
                }
            }
        }
    }
    
    // Diálogo con cuadrícula de tonalidades
    if (showKeysDialog) {
        AlertDialog(
            onDismissRequest = { showKeysDialog = false },
            title = { 
                Text(
                    text = "Seleccionar tonalidad:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    viewModel.musicalKeys.chunked(4).forEach { rowKeys ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowKeys.forEach { key ->
                                val keySimple = key.substringBefore("/")
                                val isSelected = selectedKey == keySimple
                                
                                Button(
                                    onClick = {
                                        if (selectedKey == keySimple) {
                                            viewModel.clearKey()
                                        } else {
                                            viewModel.setKey(keySimple)
                                        }
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
