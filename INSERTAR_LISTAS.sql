-- ============================================================
-- SQL PARA INSERTAR LISTAS PREGRABADAS EN LA BASE DE DATOS
-- Ejecutar en DB Browser después de abrir himnario.db
-- ============================================================

-- 0. Crear tablas necesarias si no existen
CREATE TABLE IF NOT EXISTS folders (
    folderId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    createdAt INTEGER NOT NULL
);

-- 1. Crear Carpeta "Alabanza"
INSERT INTO folders (folderId, name, description, createdAt) 
VALUES (1, 'Alabanza', 'Himnos organizados por tonalidad', strftime('%s', 'now') * 1000);

-- 2. Crear Listas de tonalidades

-- Lista DO
INSERT INTO hymn_lists (listId, name, description, isFavorite, createdAt, folderId)
VALUES (1, 'DO', 'Himnos en tonalidad DO', 0, strftime('%s', 'now') * 1000, 1);

-- Lista RE
INSERT INTO hymn_lists (listId, name, description, isFavorite, createdAt, folderId)
VALUES (2, 'RE', 'Himnos en tonalidad RE', 0, strftime('%s', 'now') * 1000, 1);

-- Lista MI-
INSERT INTO hymn_lists (listId, name, description, isFavorite, createdAt, folderId)
VALUES (3, 'MI-', 'Himnos en tonalidad MI menor', 0, strftime('%s', 'now') * 1000, 1);

-- Lista MI
INSERT INTO hymn_lists (listId, name, description, isFavorite, createdAt, folderId)
VALUES (4, 'MI', 'Himnos en tonalidad MI', 0, strftime('%s', 'now') * 1000, 1);

-- Lista SOL
INSERT INTO hymn_lists (listId, name, description, isFavorite, createdAt, folderId)
VALUES (5, 'SOL', 'Himnos en tonalidad SOL', 0, strftime('%s', 'now') * 1000, 1);

-- 3. Agregar himnos a las listas (list_hymn_cross_ref)
-- Nota: Los IDs de los himnos deben coincidir con los de tu base de datos
-- Usaremos subconsultas para buscar los IDs por título

-- Lista DO (listId = 1)
INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 0 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Agua de vida')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 1 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Aquí en esta tierra')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 2 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%alabad a jehová%' OR LOWER(TRIM(title)) LIKE '%alabad a jehova%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 3 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Alabaré')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 4 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Batalla no es batalla')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 5 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%bendito sea jehová mi roca%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 6 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%caminando voy para canaán%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 7 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Cristo la roca')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 8 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Cristo rompe las cadenas')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 9 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%cuando allá se pase lista%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 10 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%de jesús el nombre invoca%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 11 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%digno eres señor%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 12 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Dilo a Cristo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 13 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%dulce comunión%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 14 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('El encuentro en el aire')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 15 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('El culto de hoy va a ser maravilloso')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 16 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%en la nueva jerulalem%' OR LOWER(TRIM(title)) LIKE '%en la nueva jerusalén%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 17 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%en la viña del señor%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 18 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Grande gozo hay en mi alma hoy')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 19 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%habladme mas de cristo%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 20 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Hay un motivo que es de grande gozo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 21 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Junto a la cruz')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 22 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Llena de amor mi camino')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 23 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%lo entenderemos todo mas alla%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 24 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%mirad bendecid a jehova%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 25 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('No hay Dos tan grande como tu')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 26 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Oh bondad tan infinita')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 27 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Oh si quiero verle')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 28 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Oh yo quiero andar con Cristo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 29 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%resucito la nueva edad%' OR LOWER(TRIM(title)) LIKE '%resucitó la nueva edad%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 30 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Si en verdad esres salvo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 31 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Soy libertado')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 32 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Todo lo que respire')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 33 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%un dia cristo volverá%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 34 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Ven a la luz')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 35 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%vida nueva encontré%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 36 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Yo me gozo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 37 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Yo siento gozo en mi alma')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 38 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%jacob lucho con el angel%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 39 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('La historia de la cruz')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 40 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('El rapto')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 41 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Cristo es la roca de poder')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 42 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%el dia llegó%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 43 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Vamos subiendo una escalera')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 1, id, 44 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%firme estaré%' LIMIT 1;

-- Lista RE (listId = 2)
INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 0 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('A combatir')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 1 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('A Jesucristo ven sin tardar')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 2 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Bendito sea Jehova la roca')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 3 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%cristo es mío%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 4 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%edifiqué mi casa%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 5 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Gloria gloria Aleluya')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 6 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%hasta el fin lucharé%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 7 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%hay poder en jesús%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 8 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%las promesas de jesús%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 9 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Todo en todo es Jesucristo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 10 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Ven, ven, ven Espiritu divino')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 11 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Vengan a cenar')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 12 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Yo tengo paz y gozo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 13 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Esperanza plena')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 2, id, 14 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Cristo es la roca de poder')) LIMIT 1;

-- Lista MI- (listId = 3)
INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 3, id, 0 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%con gozo y alegría%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 3, id, 1 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Alaba oh alma mia a Jehova')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 3, id, 2 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('El poderoso de Israel')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 3, id, 3 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Cada dia')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 3, id, 4 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Salmo 150')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 3, id, 5 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%el señor es mi rey%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 3, id, 6 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%hay victoria en mi jesús%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 3, id, 7 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Jesucristo es tan ggrande')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 3, id, 8 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%aclamad a dios%' LIMIT 1;

-- Lista MI (listId = 4)
INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 0 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Caminando')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 1 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Amo a mi Dios')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 2 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Ardiendo en fuego')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 3 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%como el arca de noé%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 4 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Cuando Dios la sangre ve')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 5 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Dios esta aqui, que precioso es')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 6 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Donde esta el espiritu de Dios')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 7 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('El gozo que tengo yo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 8 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('El reloj de Dios')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 9 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%el señor me lleno de gozo%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 10 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('En el hogar')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 11 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('En arizona')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 12 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Es un canto de libertad')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 13 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Hay un presioso manantial')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 14 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%la gloriosa aparicion%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 15 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Libre, tu me hiciste libre')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 16 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Libertad, oh que buena')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 17 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%manda el fuego señor%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 18 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Poder, maravillosos poder')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 19 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Si ruge tempestad')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 20 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Si soy yo soy el templo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 21 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Solamente en Cristo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 22 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%seguiré a mi jesus%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 23 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Ya viene Cristo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 24 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Yo soy testigo del poder de Dios')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 25 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%yo vivo señor porque tu vives%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 26 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Esta cayendo el fuego')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 27 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Bienvenidos')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 28 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%mi corazon alabara%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 29 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Trigo soy')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 30 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%feliz cumpleaños%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 4, id, 31 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Por la sanagre')) LIMIT 1;

-- Lista SOL (listId = 5)
INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 0 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%cuan gloriosa sera la mañana%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 1 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%cuando cristo vino a mi corazon%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 2 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%dios me cuidara%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 3 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('El aposento alto')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 4 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('El ciello es el trono de mi Dios')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 5 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('El coro celestial')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 6 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Espiritu de Dios, desciende sobre mi')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 7 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%galardon%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 8 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Halle un buen amigo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 9 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Que binito es')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 10 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('En el arrebatamiento')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 11 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%tal como el siervo brama%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 12 FROM hymns WHERE LOWER(TRIM(title)) LIKE '%demos gracias al señor%' LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 13 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Bendita sea la gracia')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 14 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Victoria en Cristo')) LIMIT 1;

INSERT INTO list_hymn_cross_ref (listId, hymnId, position)
SELECT 5, id, 15 FROM hymns WHERE LOWER(TRIM(title)) = LOWER(TRIM('Vuelvo a mi hogar')) LIMIT 1;

-- ============================================================
-- VERIFICACIÓN
-- Ejecuta este query para ver si se insertaron correctamente:
-- ============================================================
/*
SELECT 
    f.name as Carpeta,
    hl.name as Lista,
    COUNT(lhcr.hymnId) as 'Cantidad de Himnos'
FROM folders f
LEFT JOIN hymn_lists hl ON hl.folderId = f.folderId
LEFT JOIN list_hymn_cross_ref lhcr ON lhcr.listId = hl.listId
GROUP BY f.folderId, hl.listId
ORDER BY f.name, hl.name;
*/
