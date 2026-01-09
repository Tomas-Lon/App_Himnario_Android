# Cómo extraer la base de datos del emulador/dispositivo

## Opción 1: Usando ADB (Android Debug Bridge)

1. Asegúrate de tener la app ejecutándose en un emulador o dispositivo con depuración USB activada

2. Abre una terminal y ejecuta:
```bash
adb shell
run-as com.example.himnariobeta
cp databases/hymn_database /sdcard/hymn_database
exit
adb pull /sdcard/hymn_database C:\temp\hymn_database.db
```

3. Abre `C:\temp\hymn_database.db` en DB Browser

## Opción 2: Device File Explorer (Android Studio)

1. Con la app corriendo, ve a: **View → Tool Windows → Device File Explorer**

2. Navega a: `/data/data/com.example.himnariobeta/databases/`

3. Clic derecho en `hymn_database` → **Save As...**

4. Guarda en tu computadora y ábrelo con DB Browser

## Opción 3: Agregar datos directamente al archivo original

Si prefieres que los datos estén desde el inicio (antes de instalar la app), 
modifica el archivo `assets/database/himnario.db` con estos SQL:

```sql
-- Agregar columna si no existe
ALTER TABLE hymns ADD COLUMN musical_key TEXT;
ALTER TABLE hymns ADD COLUMN musical_notation TEXT;

-- Insertar datos de prueba
UPDATE hymns 
SET musical_key = 'SOL',
    musical_notation = '	I        IV      I
Cuan Gloriosa será la mañana,
	  IV    V           I
Cuando venga Jesús el Salvador;
	IV    V              I     vi
Las naciones unidas como hermanas,
     V		IV       I
Bienvenida, daremos al Señor.'
WHERE id = 64 OR LOWER(TRIM(title)) LIKE '%cuan gloriosa%';

UPDATE hymns 
SET musical_key = 'DO',
    musical_notation = '    I       V      vi
Alabad a Jehová
    IV      I       V
Porque Él es bueno'
WHERE LOWER(TRIM(title)) LIKE '%alabad a jehová%' 
   OR LOWER(TRIM(title)) LIKE '%alabad a jehova%';

UPDATE hymns 
SET musical_key = 'RE',
    musical_notation = '  I              IV        I
Hay poder, poder, sin igual poder
     IV          I           V
En Jesús quien murió
  I              IV        I
Hay poder, poder, sin igual poder
     IV     V         I
En la sangre que Él vertió'
WHERE LOWER(TRIM(title)) LIKE '%hay poder%';
```

**IMPORTANTE:** Si modificas el archivo original en `assets/`, debes desinstalar 
completamente la app y reinstalarla para que use la nueva versión.

## Verificar que los datos se cargaron

En DB Browser, ejecuta:
```sql
SELECT id, title, musical_key, musical_notation 
FROM hymns 
WHERE musical_notation IS NOT NULL;
```

Deberías ver al menos 2 himnos con datos.
