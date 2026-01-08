-- Script SQL para actualizar tonalidades de himnos existentes
-- Ejecutar en DB Browser for SQLite

-- A combatir (G = SOL)
UPDATE hymns SET musical_key = 'SOL' WHERE title = 'A combatir';

-- A dónde encontraré amor igual (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title = 'A donde encontrare amor igual' OR title = 'A dónde encontraré amor igual';

-- A Jesucristo ven sin tardar (A = LA)
UPDATE hymns SET musical_key = 'LA' WHERE title = 'A Jesucristo ven sin tardar';

-- A la estatura de un varón perfecto (G = SOL)
UPDATE hymns SET musical_key = 'SOL' WHERE title = 'A la estatura de un varón perfecto' OR title LIKE '%estatura%varón perfecto%';

-- Abre mis Ojos (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title = 'Abre mis ojos' OR title = 'Abre mis Ojos';

-- Agradecimiento (Dm = REm)
UPDATE hymns SET musical_key = 'REm' WHERE title = 'Agradecimiento';

-- Agua de vida (A = LA)
UPDATE hymns SET musical_key = 'LA' WHERE title = 'Agua de vida';

-- Ahora mismo (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title = 'Ahora mismo';

-- Alabad a Jehová (G = SOL)
UPDATE hymns SET musical_key = 'SOL' WHERE title LIKE '%Alabad a Jehová%' OR title LIKE '%Alabad a Jehova%';

-- Alabare (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title = 'Alabare' OR title = 'Alabaré';

-- Aleluya (DO por defecto)
UPDATE hymns SET musical_key = 'DO' WHERE title = 'Aleluya' AND musical_key IS NULL;

-- Aleluya al señor (G = SOL)
UPDATE hymns SET musical_key = 'SOL' WHERE title LIKE '%Aleluya al señor%';

-- Alfarero (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title = 'Alfarero';

-- Algo está descendiendo (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title LIKE '%Algo está descendiendo%' OR title LIKE '%Algo esta descendiendo%';

-- Allá en el monte Horeb (G = SOL)
UPDATE hymns SET musical_key = 'SOL' WHERE title LIKE '%Allá en el monte Horeb%' OR title LIKE '%Alla en el monte Horeb%';

-- Allí no habrá tribulación (G = SOL)
UPDATE hymns SET musical_key = 'SOL' WHERE title LIKE '%Allí no habrá tribulación%' OR title LIKE '%Alli no habra tribulacion%';

-- Amémonos de corazón (Em = MIm)
UPDATE hymns SET musical_key = 'MIm' WHERE title LIKE '%Amémonos de corazón%' OR title LIKE '%Amemonos de corazon%';

-- Amo a mi Dios (C = DO)
UPDATE hymns SET musical_key = 'DO' WHERE title = 'Amo a mi Dios';

-- Aquí en esta tierra (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title LIKE '%Aquí en esta tierra%' OR title LIKE '%Aqui en esta tierra%';

-- Aquí estoy (G = SOL)
UPDATE hymns SET musical_key = 'SOL' WHERE title LIKE '%Aquí estoy%' OR title LIKE '%Aqui estoy%';

-- Ardiendo en fuego (C = DO)
UPDATE hymns SET musical_key = 'DO' WHERE title = 'Ardiendo en fuego';

-- Aunque un ejército (Dm = REm)
UPDATE hymns SET musical_key = 'REm' WHERE title LIKE '%Aunque un ejército%' OR title LIKE '%Aunque un ejercito%';

-- Bajo la sangre expiatoria (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title = 'Bajo la sangre expiatoria';

-- Basta que me toques (C = DO)
UPDATE hymns SET musical_key = 'DO' WHERE title = 'Basta que me toques';

-- Batalla no es batalla (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title = 'Batalla no es batalla';

-- Bendito sea Jehová mi roca (A = LA)
UPDATE hymns SET musical_key = 'LA' WHERE title LIKE '%Bendito sea Jehová mi roca%' OR title LIKE '%Bendito sea Jehova mi roca%';

-- Bienvenidos (C = DO)
UPDATE hymns SET musical_key = 'DO' WHERE title = 'Bienvenidos';

-- Cada día (Em = MIm)
UPDATE hymns SET musical_key = 'MIm' WHERE title LIKE '%Cada día%' OR title = 'Cada dia';

-- Caminando (C = DO)
UPDATE hymns SET musical_key = 'DO' WHERE title = 'Caminando';

-- Cómo el arca de Noé (C = DO)
UPDATE hymns SET musical_key = 'DO' WHERE title LIKE '%arca de Noé%' OR title LIKE '%arca de Noe%';

-- Cómo puede estar triste (Dm = REm)
UPDATE hymns SET musical_key = 'REm' WHERE title LIKE '%Cómo puede estar triste%' OR title LIKE '%Como puede estar triste%';

-- Comprado con sangre (G = SOL)
UPDATE hymns SET musical_key = 'SOL' WHERE title = 'Comprado con sangre';

-- Con mi Jesucristo (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title = 'Con mi Jesucristo';

-- Cristo a tomado mi vida (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title LIKE '%Cristo%tomado mi vida%' OR title LIKE '%Cristo ha tomado mi vida%';

-- Cristo es la peña de Horeb (Dm = REm)
UPDATE hymns SET musical_key = 'REm' WHERE title LIKE '%Cristo es la peña de Horeb%' OR title LIKE '%Cristo es la pena de Horeb%';

-- Cristo es mío (G = SOL)
UPDATE hymns SET musical_key = 'SOL' WHERE title LIKE '%Cristo es mío%' OR title = 'Cristo es mio';

-- Cristo la roca (G = SOL)
UPDATE hymns SET musical_key = 'SOL' WHERE title = 'Cristo la roca';

-- Cristo maravilloso eres tu (D = RE)
UPDATE hymns SET musical_key = 'RE' WHERE title LIKE '%Cristo maravilloso eres%' OR title LIKE '%Cristo maravilloso eres tu%';

-- Cristo rompe las cadenas (G = SOL)
UPDATE hymns SET musical_key = 'SOL' WHERE title = 'Cristo rompe las cadenas';

-- Cruzando el valle voy (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title = 'Cruzando el valle voy';

-- Cuan gloriosa será la mañana (C = DO)
UPDATE hymns SET musical_key = 'DO' WHERE title LIKE '%Cuan gloriosa%' OR title LIKE '%Cuán gloriosa%';

-- Cuan grande es el (A = LA)
UPDATE hymns SET musical_key = 'LA' WHERE title LIKE '%Cuan grande es%' OR title LIKE '%Cuán grande es%';

-- Cuando allá se pase lista (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title LIKE '%Cuando allá se pase lista%' OR title LIKE '%Cuando alla se pase lista%';

-- Cuando cristo vino (D = RE)
UPDATE hymns SET musical_key = 'RE' WHERE title LIKE '%Cuando cristo vino%' OR title LIKE '%Cuando Cristo vino%';

-- Cuando Dios la sangre ve (C = DO)
UPDATE hymns SET musical_key = 'DO' WHERE title = 'Cuando Dios la sangre ve';

-- Cuando el pueblo alaba a Dios (Em = MIm)
UPDATE hymns SET musical_key = 'MIm' WHERE title = 'Cuando el pueblo alaba a Dios';

-- Cuando el vino a mi corazón (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title LIKE '%Cuando%vino a mi corazón%' OR title LIKE '%Cuando%vino a mi corazon%';

-- Cuando los santos marchen ya (E = MI)
UPDATE hymns SET musical_key = 'MI' WHERE title = 'Cuando los santos marchen ya';
