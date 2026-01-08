package com.example.himnariobeta.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Filtrar Himnos", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // FILTROS CON CHIPS FUNCIONALES
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // FilterChip Categoría - Ahora con lógica funcional
            Box(modifier = Modifier.weight(1f)) {
                FilterChip(
                    selected = selectedCategory != null,
                    onClick = { viewModel.toggleCategoryMenu() },
                    label = { Text(selectedCategory ?: "Categoría") },
                    trailingIcon = {
                        if (selectedCategory != null) {
                            Icon(
                                Icons.Default.Close, 
                                "Borrar", 
                                Modifier.clickable { viewModel.clearCategory() }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
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

        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // SELECTOR DE TONALIDAD - Grid de chips
        Text("Tonalidad:", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            items(viewModel.musicalKeys) { key ->
                FilterChip(
                    selected = selectedKey == key,
                    onClick = {
                        if (selectedKey == key) {
                            viewModel.clearKey()
                        } else {
                            viewModel.setKey(key)
                        }
                    },
                    label = { Text(key) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

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
}
