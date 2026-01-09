# 🎵 Modo Músicos - Guía de Uso

## ✅ Implementación Completada

Se ha implementado exitosamente el **Modo Músicos** con las siguientes características:

### 📋 Archivos Creados/Modificados:

1. **TransposeUtils.kt** - Sistema de transposición de acordes
2. **MusicianViewModel.kt** - Lógica de negocio
3. **MusicianScreen.kt** - Interfaz de usuario
4. **HymnEntity.kt** - Campo `musical_notation` agregado
5. **HymnDatabase.kt** - Migración v13 → v14
6. **Navigation.kt** - Ruta `Musicians` agregada
7. **ViewModelFactory.kt** - Support para MusicianViewModel
8. **MainActivity.kt** - Pestaña "Músicos" en NavigationBar

---

## 🚀 Cómo Usar

### 1. **Ejecutar la App**
La migración de base de datos se ejecutará automáticamente al abrir la app.

### 2. **Agregar Acordes a Himnos**

Tienes dos opciones:

#### Opción A: Desde Android Studio Database Inspector
1. Abre `View` → `Tool Windows` → `App Inspection`
2. Ejecuta la app en un dispositivo/emulador
3. Selecciona la pestaña `Database Inspector`
4. Encuentra la tabla `hymns`
5. Edita el campo `musical_notation` del himno que quieras

#### Opción B: Usando SQL (Recomendado para pruebas)
```sql
-- Ejecutar en Database Inspector o usando adb
UPDATE hymns 
SET musical_notation = '	I        IV      I
Cuan Gloriosa será la mañana,
	  IV    V           I
Cuando venga Jesús el Salvador;'
WHERE id = 64;
```

### 3. **Formato de los Acordes**

Los acordes deben usar **grados romanos**:
- `I, II, III, IV, V, VI, VII` = Acordes mayores
- `i, ii, iii, iv, v, vi, vii` = Acordes menores

**Ejemplo Real:**
```
	I        IV      I
Cuan Gloriosa será la mañana,
	  IV    V           I
Cuando venga Jesús el Salvador;
	IV    V              I     vi
Las naciones unidas como hermanas,
     V		IV       I
Bienvenida, daremos al Señor.
```

---

## 🎹 Tonalidades Soportadas

La app soporta transposición a **12 tonalidades**:
- C (DO)
- C#/Db (DO#/REb)
- D (RE)
- D#/Eb (RE#/MIb)
- E (MI)
- F (FA)
- F#/Gb (FA#/SOLb)
- G (SOL)
- G#/Ab (SOL#/LAb)
- A (LA)
- A#/Bb (LA#/SIb)
- B (SI)

---

## 🧪 Probar la Funcionalidad

1. **Ejecuta la app**
2. **Agrega acordes** al himno #64 usando el SQL de ejemplo
3. **Ve a la pestaña "Músicos"** (icono de nota musical)
4. **Busca** "Cuan gloriosa"
5. **Selecciona diferentes tonalidades** (C, D, E, G, etc.)
6. **Observa** cómo los acordes se transponen automáticamente:
   - En C: `I IV V` → `C F G`
   - En D: `I IV V` → `D G A`
   - En G: `I IV V` → `G C D`

---

## 📝 Para Agregar Más Himnos con Acordes

1. Identifica el ID del himno en la base de datos
2. Escribe los acordes en grados romanos
3. Ejecuta UPDATE en la base de datos:

```sql
UPDATE hymns 
SET musical_notation = 'AQUÍ TUS ACORDES EN GRADOS ROMANOS'
WHERE id = [ID_DEL_HIMNO];
```

---

## 🎨 Características de la UI

- ✅ Selector visual de tonalidades con chips
- ✅ Acordes mostrados en fuente monoespaciada
- ✅ Letra del himno debajo de los acordes
- ✅ Búsqueda integrada
- ✅ Contador de himnos con acordes
- ✅ Animaciones suaves
- ✅ Diseño Material 3
- ✅ Soporte para compartir y exportar PDF

---

## 🔧 Próximos Pasos Sugeridos

1. **Agregar editor in-app** para que los usuarios puedan agregar/editar acordes sin SQL
2. **Importar acordes** desde archivos de texto
3. **Compartir acordes** entre usuarios
4. **Favoritos musicales** para músicos
5. **Modo presentación** en pantalla completa

---

## 🐛 Solución de Problemas

**No veo himnos en la pestaña Músicos:**
- Los himnos solo aparecen si tienen el campo `musical_notation` con contenido
- Agrega acordes usando el SQL de ejemplo

**Los acordes no se transponen:**
- Verifica que usas grados romanos (I, IV, V) no notas (C, F, G)
- El sistema preserva el espaciado exacto

**Error de migración:**
- La migración es automática y segura
- No perderás datos existentes

---

## 📧 Contacto

Si tienes dudas o sugerencias, puedes seguir desarrollando estas características adicionales.

¡Disfruta tu nueva funcionalidad para músicos! 🎶
