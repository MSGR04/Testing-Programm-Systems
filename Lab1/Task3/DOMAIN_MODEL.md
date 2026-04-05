## Punkt 3. Domennaya model po tekstu

Tekst opisivaet nablyudaemuyu kosmicheskuyu stsenу. V modeli vydeleny sleduyushchie
sushchnosti:

- `SpaceScene` - vsya scena nablyudeniya.
- `Screen` - ekran, na kotorom poyavlyaetsya izobrazhenie.
- `AppearanceEvent` - poyavlenie obekta na ekrane v opredelennom meste i poryadke.
- `BinaryStarSystem` - sistema iz dvukh zvezd.
- `Star` - otdelnaya zvezda s tsvetom i vidimym razmerom.
- `PlanetView` - vid planety v kadre.
- `ColorGradient` - perekhod tsveta osveshchennoi chasti planety.

Svyazi mezhdu sushchnostyami:

- `SpaceScene` soderzhit odin `Screen`.
- `SpaceScene` soderzhit uporyadochennyi spisok `AppearanceEvent`.
- `AppearanceEvent` ssylayetsya na odin vidimyi obekt (`VisibleObject`) i oblast ekrana.
- `BinaryStarSystem` vklyuchaet rovno dve `Star`.
- `PlanetView` imeet fazu, razmer, priznak vidimosti nochnoi storony i `ColorGradient`.

Biznes-pravila, zakreplennye v modeli:

1. Sobytiya poyavleniya dolzhny byt strogo uporyadocheny po `order`.
2. Gradient planety dolzhen soedinyat raznye tsveta.
3. Esli u planety vidna nochnaya storona, gradient dolzhen zavershat'sya chernym tsvetom.
4. Dlya varianta `330906` ekran ogromnyi, snachala poyavlyaetsya binarnaya sistema na krayu,
   zatem v uglu - bolshoi krasno-chernyi polumesyats planety.

Testovoe pokrytie:

- proverka korrektnogo postroeniya stseny varianta `330906`;
- proverka ogranicheniya dlya planetarnogo gradienta;
- proverka strogoi uporyadochennosti sobytii;
- proverka nemodifitsiruemosti kollektsii sobytii;
- proverka dostupnosti glavnykh obektov (`BinaryStarSystem`, `PlanetView`) cherez domenные accessors.
