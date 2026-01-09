package com.example.himnariobeta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.himnariobeta.components.AddHymnToListDialog
import com.example.himnariobeta.components.CreateFolderDialog
import com.example.himnariobeta.components.CreateListDialog
import com.example.himnariobeta.components.HymnList
import com.example.himnariobeta.screens.FilterScreen
import com.example.himnariobeta.screens.ListDetailScreen
import com.example.himnariobeta.screens.MusicianScreen
import com.example.himnariobeta.ui.theme.HimnarioBetaTheme


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnApp() {
    val context = LocalContext.current
    val database = remember { HymnDatabase.getDatabase(context) }

    val repository = remember {
        HymnRepository(
            hymnDao = database.hymnDao(),
            listDao = database.hymnListDao(),
            folderDao = database.folderDao()
        )
    }

    val viewModelFactory = remember { HymnViewModelFactory(repository) }

    val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
    val listsViewModel: ListsViewModel = viewModel(factory = viewModelFactory)
    val filterViewModel: FilterViewModel = viewModel(factory = viewModelFactory)
    val musicianViewModel: MusicianViewModel = viewModel(factory = viewModelFactory)

    val navController = rememberNavController()
    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    val isSearchActive by homeViewModel.isSearchActive.collectAsState()
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val selectedList by listsViewModel.selectedList.collectAsState()
    val selectedFolder by listsViewModel.selectedFolder.collectAsState()
    val allFolders by listsViewModel.allFolders.collectAsState()
    val allLists by listsViewModel.allLists.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val currentSnackbar =
        homeViewModel.snackbarMessage.collectAsState().value
            ?: listsViewModel.snackbarMessage.collectAsState().value

    LaunchedEffect(currentSnackbar) {
        currentSnackbar?.let {
            snackbarHostState.showSnackbar(it)
            homeViewModel.clearSnackbar()
            listsViewModel.clearSnackbar()
        }
    }

    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showCreateListDialog by remember { mutableStateOf(false) }
    var showAddToListDialog by remember { mutableStateOf(false) }
    var hymnToAdd by remember { mutableStateOf<HymnEntity?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    when {
                        selectedList != null -> Text(
                            text = selectedList!!.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        selectedFolder != null -> Text(
                            text = selectedFolder!!.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        isSearchActive -> OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { homeViewModel.updateSearchQuery(it) },
                            placeholder = { 
                                Text(
                                    "Buscar himnos (ignora tildes)...",
                                    style = MaterialTheme.typography.bodyMedium
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge,
                            shape = MaterialTheme.shapes.medium,
                            colors = androidx.compose.material3.TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                        else -> {
                            val titleText = when (currentRoute) {
                                Screen.Home.route -> "Himnario"
                                Screen.Lists.route -> "Carpetas y Listas"
                                Screen.Filters.route -> "Filtros"
                                else -> "Himnario"
                            }
                            Text(text = titleText)
                        }
                    }
                },
                navigationIcon = {
                    when {
                        selectedList != null -> IconButton(onClick = { listsViewModel.selectList(null) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                        selectedFolder != null -> IconButton(onClick = { listsViewModel.selectFolder(null) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                },
                actions = {
                    // Botones de edición y eliminación para carpetas
                    if (selectedFolder != null) {
                        var showEditDialog by remember { mutableStateOf(false) }
                        
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar carpeta")
                        }
                        IconButton(onClick = { 
                            listsViewModel.deleteFolder(selectedFolder!!)
                            listsViewModel.selectFolder(null)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar carpeta")
                        }
                        
                        if (showEditDialog) {
                            EditFolderDialog(
                                initialName = selectedFolder!!.name,
                                initialDesc = selectedFolder!!.description,
                                onDismiss = { showEditDialog = false },
                                onConfirm = { name, desc ->
                                    listsViewModel.updateFolder(selectedFolder!!, name, desc)
                                    showEditDialog = false
                                }
                            )
                        }
                    }
                    
                    if (currentRoute == Screen.Lists.route && selectedList == null && selectedFolder == null) {
                        var menuExpanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Crear carpeta") },
                                onClick = {
                                    showCreateFolderDialog = true
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Ordenar listas por fecha") },
                                onClick = {
                                    listsViewModel.setSortOption(SortOption.DATE_DESC)
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Ordenar listas por nombre") },
                                onClick = {
                                    listsViewModel.setSortOption(SortOption.NAME_ASC)
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Favoritos primero") },
                                onClick = {
                                    listsViewModel.setSortOption(SortOption.FAVORITES)
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                    if (selectedList == null && selectedFolder == null && currentRoute != Screen.Filters.route && currentRoute != Screen.Lists.route && currentRoute != Screen.Musicians.route) {
                        if (isSearchActive) {
                            IconButton(onClick = { homeViewModel.setSearchActive(false) }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar búsqueda")
                            }
                        } else {
                            IconButton(onClick = { homeViewModel.setSearchActive(true) }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar")
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (selectedList == null && selectedFolder == null) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Himnos") },
                        label = { Text("Himnos") },
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                            listsViewModel.selectList(null)
                            listsViewModel.selectFolder(null)
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = "Listas") },
                        label = { Text("Listas") },
                        selected = currentRoute == Screen.Lists.route,
                        onClick = {
                            navController.navigate(Screen.Lists.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                            listsViewModel.selectList(null)
                            listsViewModel.selectFolder(null)
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Filtros") },
                        label = { Text("Filtros") },
                        selected = currentRoute == Screen.Filters.route,
                        onClick = {
                            navController.navigate(Screen.Filters.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                            listsViewModel.selectList(null)
                            listsViewModel.selectFolder(null)
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = "Músicos") },
                        label = { Text("Músicos") },
                        selected = currentRoute == Screen.Musicians.route,
                        onClick = {
                            navController.navigate(Screen.Musicians.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                            listsViewModel.selectList(null)
                            listsViewModel.selectFolder(null)
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            when {
                currentRoute == Screen.Lists.route && selectedList == null && selectedFolder == null -> {
                    FloatingActionButton(
                        onClick = { showCreateFolderDialog = true },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Crear carpeta")
                            Text("Nueva Carpeta")
                        }
                    }
                }
                currentRoute == Screen.Lists.route && selectedList == null && selectedFolder != null -> {
                    FloatingActionButton(onClick = { showCreateListDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Crear Lista en carpeta")
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Screen.Home.route) {
                AnimatedContent(
                    targetState = when {
                        selectedList != null -> "list"
                        selectedFolder != null -> "folder"
                        else -> "home"
                    },
                    label = "home"
                ) { state ->
                    when (state) {

                        "list" -> selectedList?.let { list ->
                            BackHandler { listsViewModel.selectList(null) }

                            val liveList =
                                allLists.find { it.listId == list.listId } ?: list
                            val expandedId by listsViewModel.expandedHymnId.collectAsState()

                            ListDetailScreen(
                                list = liveList,
                                listDao = database.hymnListDao(),
                                hymnDao = database.hymnDao(),
                                expandedHymnId = expandedId,
                                onToggleExpand = listsViewModel::toggleHymnExpansion,
                                onNoteChange = homeViewModel::updateHymnNote,
                                onDuplicateList = {
                                    listsViewModel.duplicateList(liveList)
                                    listsViewModel.selectList(null)
                                },
                                repository = repository
                            )
                        }

                        "folder" -> selectedFolder?.let { folder ->
                            BackHandler { listsViewModel.selectFolder(null) }

                            FolderScreen(
                                folder = folder,
                                lists = allLists.filter { it.folderId == folder.folderId },
                                onListClick = listsViewModel::selectList,
                                onDeleteList = listsViewModel::deleteList,
                                onToggleFavorite = listsViewModel::toggleListFavorite,
                                onDeleteFolder = listsViewModel::deleteFolder,
                                onEditFolder = listsViewModel::updateFolder,
                                onMoveListToFolder = listsViewModel::moveListToFolder,
                                allFolders = allFolders
                            )
                        }

                        "home" -> {
                            val hymns by homeViewModel.displayedHymns.collectAsState()
                            val expandedId by homeViewModel.expandedHymnId.collectAsState()

                            HymnList(
                                hymns = hymns,
                                expandedHymnId = expandedId,
                                onToggleExpand = homeViewModel::toggleHymnExpansion,
                                onAddToList = {
                                    hymnToAdd = it
                                    showAddToListDialog = true
                                },
                                onNoteChange = homeViewModel::updateHymnNote
                            )
                        }
                    }
                }
            }

            composable(Screen.Lists.route) {
                AnimatedContent(
                    targetState = when {
                        selectedList != null -> "list"
                        selectedFolder != null -> "folder"
                        else -> "lists"
                    },
                    label = "lists"
                ) { state ->
                    when (state) {

                        "list" -> selectedList?.let { list ->
                            BackHandler { listsViewModel.selectList(null) }

                            val liveList =
                                allLists.find { it.listId == list.listId } ?: list
                            val expandedId by listsViewModel.expandedHymnId.collectAsState()

                            ListDetailScreen(
                                list = liveList,
                                listDao = database.hymnListDao(),
                                hymnDao = database.hymnDao(),
                                expandedHymnId = expandedId,
                                onToggleExpand = listsViewModel::toggleHymnExpansion,
                                onNoteChange = homeViewModel::updateHymnNote,
                                onDuplicateList = {
                                    listsViewModel.duplicateList(liveList)
                                    listsViewModel.selectList(null)
                                },
                                repository = repository
                            )
                        }

                        "folder" -> selectedFolder?.let { folder ->
                            BackHandler { listsViewModel.selectFolder(null) }

                            FolderScreen(
                                folder = folder,
                                lists = allLists.filter { it.folderId == folder.folderId },
                                onListClick = listsViewModel::selectList,
                                onDeleteList = listsViewModel::deleteList,
                                onToggleFavorite = listsViewModel::toggleListFavorite,
                                onDeleteFolder = listsViewModel::deleteFolder,
                                onEditFolder = listsViewModel::updateFolder,
                                onMoveListToFolder = listsViewModel::moveListToFolder,
                                allFolders = allFolders
                            )
                        }

                        "lists" -> {
                            FoldersScreen(
                                folders = allFolders,
                                lists = allLists,
                                onFolderClick = listsViewModel::selectFolder,
                                onDeleteFolder = listsViewModel::deleteFolder,
                                onListClick = listsViewModel::selectList,
                                onDeleteList = listsViewModel::deleteList,
                                onToggleFavorite = listsViewModel::toggleListFavorite,
                                onMoveListToFolder = listsViewModel::moveListToFolder,
                                allFolders = allFolders
                            )
                        }
                    }
                }
            }

            composable(Screen.Filters.route) {
                FilterScreen(viewModel = filterViewModel)
            }

            composable(Screen.Musicians.route) {
                MusicianScreen(viewModel = musicianViewModel)
            }
        }
    }

    if (showCreateFolderDialog) {
        CreateFolderDialog(
            onDismiss = { showCreateFolderDialog = false },
            onConfirm = { name, desc ->
                listsViewModel.createFolder(name, desc)
                showCreateFolderDialog = false
            }
        )
    }

    if (showCreateListDialog && selectedFolder != null) {
        CreateListDialog(
            onDismiss = { showCreateListDialog = false },
            onConfirm = { name, desc ->
                listsViewModel.createList(name, desc, selectedFolder!!.folderId)
                showCreateListDialog = false
            }
        )
    }

    if (showAddToListDialog && hymnToAdd != null) {
        AddHymnToListDialog(
            lists = allLists,
            folders = allFolders,
            onDismiss = {
                showAddToListDialog = false
                hymnToAdd = null
            },
            onListSelected = { list ->
                hymnToAdd?.id?.let {
                    listsViewModel.addHymnToList(list.listId, it)
                }
                showAddToListDialog = false
                hymnToAdd = null
            }
        )
    }
}
