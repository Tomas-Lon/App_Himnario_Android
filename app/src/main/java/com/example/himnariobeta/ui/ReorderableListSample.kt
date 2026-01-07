package com.example.himnariobeta.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ReorderableListSample() {
    var items by remember { mutableStateOf((1..10).map { "Item $it" }.toMutableList()) }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(items, key = { _, item -> item }) { index, item ->
            val isDragged = draggedIndex == index
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(if (isDragged) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { draggedIndex = index },
                            onDragEnd = { draggedIndex = null },
                            onDragCancel = { draggedIndex = null },
                            onDrag = { change, dragAmount ->
                                val targetIndex = (index + dragAmount.y.toInt() / 100).coerceIn(0, items.lastIndex)
                                if (targetIndex != index) {
                                    scope.launch {
                                        val newList = items.toMutableList()
                                        val movedItem = newList.removeAt(index)
                                        newList.add(targetIndex, movedItem)
                                        items = newList
                                        draggedIndex = targetIndex
                                    }
                                }
                            }
                        )
                    },
                shadowElevation = if (isDragged) 8.dp else 2.dp
            ) {
                Text(
                    text = item,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
}