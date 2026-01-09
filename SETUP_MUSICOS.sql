-- ================================================
-- SQL para configurar la base de datos 
-- para el Modo Músicos
-- ================================================

-- 1. AGREGAR COLUMNA musical_notation
ALTER TABLE hymns ADD COLUMN musical_notation TEXT;

-- 2. AGREGAR DATOS DE PRUEBA - Himno #64
-- Tonalidad original: SOL
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
WHERE id = 64;

-- 3. VERIFICAR QUE SE AGREGÓ CORRECTAMENTE
SELECT id, numero, title, musical_key, musical_notation
FROM hymns 
WHERE id = 64;

-- ================================================
-- OPCIONAL: Agregar más ejemplos
-- ================================================

-- Ejemplo en tonalidad DO
UPDATE hymns 
SET musical_key = 'DO',
    musical_notation = '    I      IV     I
(Acordes del himno aquí)'
WHERE title LIKE '%Agua de vida%';

-- Ejemplo en tonalidad RE
UPDATE hymns 
SET musical_key = 'RE',
    musical_notation = '    I      V      IV
(Acordes del himno aquí)'
WHERE title LIKE '%A combatir%';

-- ================================================
-- CONSULTA FINAL: Ver todos los himnos con acordes
-- ================================================
SELECT id, numero, title, musical_key, 
       CASE 
           WHEN musical_notation IS NOT NULL THEN 'SÍ' 
           ELSE 'NO' 
       END as tiene_acordes
FROM hymns 
WHERE musical_notation IS NOT NULL
ORDER BY numero;
