# Testing Setup - App Himnario Android

## 📋 Arquitectura de Testing Implementada

Se ha creado una suite completa de tests unitarios para validar la lógica de negocio de la aplicación.

## 🧪 Tests Creados

### 1. **HomeViewModelTest.kt**
Tests para el ViewModel de la pantalla principal:
- ✅ Estado inicial de búsqueda
- ✅ Actualización de query de búsqueda
- ✅ Activación/desactivación de búsqueda
- ✅ Expansión/colapso de himnos
- ✅ Actualización de notas
- ✅ Gestión de snackbar messages

### 2. **ListsViewModelTest.kt**
Tests para el ViewModel de listas y carpetas:
- ✅ Opciones de ordenamiento (fecha, nombre, favoritos)
- ✅ Selección de listas y carpetas
- ✅ Creación, actualización y eliminación de listas
- ✅ Toggle de favoritos
- ✅ Gestión de carpetas
- ✅ Mover listas entre carpetas
- ✅ Agregar/quitar himnos de listas

### 3. **FilterViewModelTest.kt**
Tests para el ViewModel de filtros:
- ✅ Selección de categorías
- ✅ Selección de notas musicales
- ✅ Limpieza de filtros
- ✅ Toggle de menús desplegables
- ✅ Validación de listas de opciones
- ✅ Expansión de himnos filtrados

### 4. **HymnRepositoryTest.kt**
Tests para el Repository (capa de datos):
- ✅ Obtención de todos los himnos
- ✅ Búsqueda de himnos
- ✅ Filtrado de himnos
- ✅ Actualización de notas
- ✅ CRUD de listas
- ✅ CRUD de carpetas
- ✅ Duplicación de listas
- ✅ Gestión de relaciones lista-himno

## 📦 Dependencias Requeridas

Agrega estas dependencias en `app/build.gradle.kts`:

```kotlin
dependencies {
    // Testing - JUnit
    testImplementation("junit:junit:4.13.2")
    
    // Testing - Mockito para mocking
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    
    // Testing - Coroutines Testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    
    // Testing - Turbine para testing de Flows (opcional pero recomendado)
    testImplementation("app.cash.turbine:turbine:1.0.0")
    
    // Testing - AndroidX Test Core
    testImplementation("androidx.arch.core:core-testing:2.2.0")
}
```

## 🚀 Ejecutar los Tests

### Desde Android Studio
1. Click derecho en la carpeta `test/`
2. Seleccionar "Run 'Tests in 'himnariobeta''"

### Desde Terminal
```bash
./gradlew test
```

### Tests específicos
```bash
# Solo HomeViewModel
./gradlew test --tests HomeViewModelTest

# Solo un test específico
./gradlew test --tests HomeViewModelTest.updateSearchQuery*
```

## 📊 Cobertura de Tests

| Componente | Tests | Cobertura Estimada |
|------------|-------|-------------------|
| HomeViewModel | 11 tests | ~85% |
| ListsViewModel | 12 tests | ~80% |
| FilterViewModel | 11 tests | ~90% |
| HymnRepository | 15 tests | ~75% |
| **Total** | **49 tests** | **~82%** |

## 🔧 Configuración de Coroutines para Testing

Los tests usan `StandardTestDispatcher` para controlar la ejecución de coroutines:

```kotlin
private val testDispatcher = StandardTestDispatcher()

@Before
fun setup() {
    Dispatchers.setMain(testDispatcher)
}

@After
fun tearDown() {
    Dispatchers.resetMain()
}
```

Esto permite:
- Control total sobre cuándo se ejecutan las coroutines
- Tests determinísticos y repetibles
- Uso de `advanceUntilIdle()` para avanzar el tiempo virtual

## 📝 Patrón de Tests Implementado

### AAA Pattern (Arrange-Act-Assert)

```kotlin
@Test
fun `updateSearchQuery updates state correctly`() = runTest {
    // Given (Arrange): A search query
    val query = "Himno 123"
    
    // When (Act): Updating search query
    viewModel.updateSearchQuery(query)
    advanceUntilIdle()
    
    // Then (Assert): State should be updated
    assertEquals(query, viewModel.searchQuery.value)
}
```

## 🎯 Mejores Prácticas Aplicadas

1. **✅ Nombres descriptivos**: Tests con nombres que describen comportamiento
2. **✅ Tests independientes**: Cada test puede ejecutarse por separado
3. **✅ Mocking apropiado**: DAOs mockeados para aislar lógica de ViewModels
4. **✅ Setup/TearDown**: Configuración y limpieza consistente
5. **✅ Tests rápidos**: No dependen de base de datos real o red
6. **✅ One Assert per Test**: Cada test verifica una cosa específica

## 🔍 Tests Avanzados con Turbine (Opcional)

Para testing más sofisticado de Flows, puedes usar Turbine:

```kotlin
@Test
fun `allLists emits sorted lists when sortOption changes`() = runTest {
    viewModel.allLists.test {
        // Verify initial emission
        val initial = awaitItem()
        
        // Change sort option
        viewModel.setSortOption(SortOption.NAME_ASC)
        
        // Verify new emission
        val sorted = awaitItem()
        assertTrue(sorted.isSortedBy { it.name })
    }
}
```

## 🚫 Limitaciones Actuales

1. **No hay tests de UI**: Los Composables no tienen tests (se recomienda agregar)
2. **No hay tests de integración**: Solo tests unitarios
3. **Mocking básico**: Se podría usar Mockk en lugar de Mockito para mejor soporte Kotlin
4. **Sin cobertura de código real**: Se recomienda configurar JaCoCo

## 📈 Próximos Pasos Recomendados

### Tests de UI con Compose Testing
```kotlin
@get:Rule
val composeTestRule = createComposeRule()

@Test
fun filterChip_whenClicked_opensMenu() {
    composeTestRule.setContent {
        FilterScreen(viewModel)
    }
    
    composeTestRule
        .onNodeWithText("Categoría")
        .performClick()
    
    composeTestRule
        .onNodeWithText("Adoración")
        .assertIsDisplayed()
}
```

### Tests de Integración
```kotlin
@Test
fun `creating list adds it to database`() = runTest {
    val repository = HymnRepository(realHymnDao, realListDao, realFolderDao)
    val viewModel = ListsViewModel(repository)
    
    viewModel.createList("Test List", null)
    advanceUntilIdle()
    
    val lists = repository.getAllLists().first()
    assertTrue(lists.any { it.name == "Test List" })
}
```

### Configurar JaCoCo para Cobertura
```kotlin
// build.gradle.kts
plugins {
    id("jacoco")
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
```

## ✅ Validación

Todos los tests deben pasar antes de mergear código:

```bash
# Ejecutar todos los tests
./gradlew test

# Ver reporte HTML
open app/build/reports/tests/test/index.html
```

## 📚 Recursos Adicionales

- [Android Testing Documentation](https://developer.android.com/training/testing)
- [Kotlin Coroutines Testing](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/)
- [Mockito Documentation](https://site.mockito.org/)
- [Compose Testing Guide](https://developer.android.com/jetpack/compose/testing)
