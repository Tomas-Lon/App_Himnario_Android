-- Script SQL para insertar himnos con sus tonalidades
-- Ejecutar en DB Browser for SQLite
-- Nota: Si un himno ya existe, simplemente lo ignora (no lo duplica)

-- A combatir (G = SOL)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'A combatir', 'SOL', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'A combatir');

-- A dónde encontraré amor igual (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'A dónde encontraré amor igual', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'A dónde encontraré amor igual');

-- A Jesucristo ven sin tardar (A = LA)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'A Jesucristo ven sin tardar', 'LA', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'A Jesucristo ven sin tardar');

-- A la estatura de un varón perfecto (G = SOL)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'A la estatura de un varón perfecto', 'SOL', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'A la estatura de un varón perfecto');

-- Abre mis Ojos (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Abre mis Ojos', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Abre mis Ojos');

-- Agradecimiento (Dm = REm)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Agradecimiento', 'REm', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Agradecimiento');

-- Agua de vida (A = LA)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Agua de vida', 'LA', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Agua de vida');

-- Ahora mismo (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Ahora mismo', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Ahora mismo');

-- Alabad a Jehová (G = SOL) - Ya existe, solo actualizar
UPDATE hymns 
SET musical_key = 'SOL' 
WHERE LOWER(TRIM(title)) LIKE '%alabad a jehová%' OR LOWER(TRIM(title)) LIKE '%alabad a jehova%';

-- Alabare (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Alabare', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Alabare');

-- Aleluya (sin tonalidad especificada, usar DO por defecto)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Aleluya', 'DO', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Aleluya');

-- Aleluya al señor (G = SOL)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Aleluya al señor', 'SOL', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Aleluya al señor');

-- Alfarero (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Alfarero', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Alfarero');

-- Algo está descendiendo (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Algo está descendiendo', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Algo está descendiendo');

-- Allá en el monte Horeb (G = SOL)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Allá en el monte Horeb', 'SOL', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Allá en el monte Horeb');

-- Allí no habrá tribulación (G = SOL)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Allí no habrá tribulación', 'SOL', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Allí no habrá tribulación');

-- Amémonos de corazón (Em = MIm)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Amémonos de corazón', 'MIm', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Amémonos de corazón');

-- Amo a mi Dios (C = DO)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Amo a mi Dios', 'DO', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Amo a mi Dios');

-- Aquí en esta tierra (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Aquí en esta tierra', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Aquí en esta tierra');

-- Aquí estoy (G = SOL)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Aquí estoy', 'SOL', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Aquí estoy');

-- Ardiendo en fuego (C = DO)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Ardiendo en fuego', 'DO', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Ardiendo en fuego');

-- Aunque un ejército (Dm = REm)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Aunque un ejército', 'REm', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Aunque un ejército');

-- Bajo la sangre expiatoria (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Bajo la sangre expiatoria', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Bajo la sangre expiatoria');

-- Basta que me toques (C = DO)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Basta que me toques', 'DO', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Basta que me toques');

-- Batalla no es batalla (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Batalla no es batalla', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Batalla no es batalla');

-- Bendito sea Jehová mi roca (A = LA)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Bendito sea Jehová mi roca', 'LA', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Bendito sea Jehová mi roca');

-- Bienvenidos (C = DO)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Bienvenidos', 'DO', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Bienvenidos');

-- Cada día (Em = MIm)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cada día', 'MIm', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cada día');

-- Caminando (C = DO)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Caminando', 'DO', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Caminando');

-- Cómo el arca de Noé (C = DO)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cómo el arca de Noé', 'DO', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cómo el arca de Noé');

-- Cómo puede estar triste (Dm = REm)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cómo puede estar triste', 'REm', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cómo puede estar triste');

-- Comprado con sangre (G = SOL)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Comprado con sangre', 'SOL', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Comprado con sangre');

-- Con mi Jesucristo (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Con mi Jesucristo', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Con mi Jesucristo');

-- Cristo a tomado mi vida (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cristo a tomado mi vida', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cristo a tomado mi vida');

-- Cristo es la peña de Horeb (Dm = REm)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cristo es la peña de Horeb', 'REm', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cristo es la peña de Horeb');

-- Cristo es mío (G = SOL)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cristo es mío', 'SOL', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cristo es mío');

-- Cristo la roca (G = SOL)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cristo la roca', 'SOL', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cristo la roca');

-- Cristo maravilloso eres tu (D = RE)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cristo maravilloso eres tu', 'RE', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cristo maravilloso eres tu');

-- Cristo rompe las cadenas (G = SOL)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cristo rompe las cadenas', 'SOL', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cristo rompe las cadenas');

-- Cruzando el valle voy (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cruzando el valle voy', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cruzando el valle voy');

-- Cuan gloriosa será la mañana (C = DO) - Ya existe, solo actualizar
UPDATE hymns 
SET musical_key = 'DO' 
WHERE LOWER(TRIM(title)) LIKE '%cuan gloriosa%';

-- Cuan grande es el (A = LA)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cuan grande es el', 'LA', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cuan grande es el');

-- Cuando allá se pase lista (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cuando allá se pase lista', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cuando allá se pase lista');

-- Cuando cristo vino (D = RE)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cuando cristo vino', 'RE', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cuando cristo vino');

-- Cuando Dios la sangre ve (C = DO)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cuando Dios la sangre ve', 'DO', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cuando Dios la sangre ve');

-- Cuando el pueblo alaba a Dios (Em = MIm)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cuando el pueblo alaba a Dios', 'MIm', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cuando el pueblo alaba a Dios');

-- Cuando el vino a mi corazón (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cuando el vino a mi corazón', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cuando el vino a mi corazón');

-- Cuando los santos marchen ya (E = MI)
INSERT INTO hymns (title, musical_key, lyrics) 
SELECT 'Cuando los santos marchen ya', 'MI', '(Letra pendiente)'
WHERE NOT EXISTS (SELECT 1 FROM hymns WHERE title = 'Cuando los santos marchen ya');
