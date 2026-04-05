## Punkt 2. Modul testing algoritma DFS

Kharakternye tochki, kotorye trasiruyutsya v realizatsii:

1. `ENTER_VERTEX` - vkhod v vershinu.
2. `START_NEIGHBOR_SCAN` - nachalo obkhoda spiska smezhnosti.
3. `CHECK_NEIGHBOR` - proverka ocherednogo soseda.
4. `RECURSIVE_DESCENT` - rekurisivnyi perekhod v neposeshchennogo soseda.
5. `EXIT_VERTEX` - vykhod iz vershiny.

Vybrannoe pokrytie:

- Lineinaya tsepochka `1-2-3` - bazovyi rekurisivnyi prokhod bez razvetvleniya.
- Tsikl `1-2-3-1` - proverka propuska uzhe poseshennoi vershiny.
- Derevo `1-2, 1-3, 2-4` - proverka vozvrata iz glubiny i perekhoda v sleduyushchuyu vetv.
- Dva komponenta `1-2` i `3-4` - proverka, chto DFS obkhodit tolko komponentu dostizhimosti iz starta.
- Petlya `1-1` - proverka obrabotki rebra v samu vershinu.
- Otsutstvuyushchaya startovaya vershina `99` - proverka granichnogo sluchaya.

Etalonnye posledovatelnosti dlya kazhdogo nabora dannykh zadany vruchnuyu v
`Task2/src/test/java/org/example/DFSTest.java` i sravnivayutsya s fakticheskoi
trassoi algoritma.
