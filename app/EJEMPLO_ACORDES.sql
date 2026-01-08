-- SQL para insertar datos de prueba con acordes
-- Ejecutar este script manualmente en tu base de datos SQLite para probar la funcionalidad

-- Ejemplo 1: Himno 64 - "Cuan gloriosa sera la mañana"
UPDATE hymns 
SET musical_notation = '	I        IV      I
Cuan Gloriosa será la mañana,
	  IV    V           I
Cuando venga Jesús el Salvador;
	IV    V              I     vi
Las naciones unidas como hermanas,
     V		IV       I
Bienvenida, daremos al Señor.'
WHERE id = 64 OR LOWER(TRIM(title)) LIKE '%cuan gloriosa%';

-- Ejemplo 2: Si tienes "Alabad a Jehová" (ejemplo)
UPDATE hymns 
SET musical_notation = '    I       V      vi
Alabad a Jehová
    IV      I       V
Porque Él es bueno'
WHERE LOWER(TRIM(title)) LIKE '%alabad a jehová%' OR LOWER(TRIM(title)) LIKE '%alabad a jehova%';

-- Ejemplo 3: "Hay poder en Jesús" (si existe)
UPDATE hymns 
SET musical_notation = '  I              IV        I
Hay poder, poder, sin igual poder
     IV          I           V
En Jesús quien murió
  I              IV        I
Hay poder, poder, sin igual poder
     IV     V         I
En la sangre que Él vertió'
WHERE LOWER(TRIM(title)) LIKE '%hay poder%';

-- Para verificar qué himnos tienen acordes:
-- SELECT id, title, musical_notation FROM hymns WHERE musical_notation IS NOT NULL;

-- Para agregar acordes manualmente a cualquier himno:
-- UPDATE hymns SET musical_notation = 'I  IV  V  I' WHERE id = [numero_del_himno];
