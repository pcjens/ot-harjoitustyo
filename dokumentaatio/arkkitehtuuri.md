# Arkkitehtuuri

## Luokkakaavio
![Tässä on kuva joka esittää ohjelman arkkitehtuuria
luokkakaaviona. Jos et näe kuvaa, se ei luultavasti ole ladannut tai
tule latautumaan.](class-diagram.png)

Luokkakaavio on hieman vanhasta versiosta ohjelmaa, mutta siitä näkee
pääosin ohjelman luokat ja niiden väliset suhteet.

`Main` sisältää ohjelman käynnistysfunktion, joka yksinkertaisesti
käynnistää käyttöliittymän `RoguesqueApp`:n launchaamalla.

`RoguesqueApp` sisältää ruudun luontilogiikan, sekä eventtien
käsittelyn. Eventeistä tieto siirtyy `Input`-luokkaan, joka toimii
abstraktiona hiirelle ja näppäimistölle. `RoguesqueApp` sisältää myös
taulukon `GameState`:ja, jotka kuvaavat erilaisia peli-tiloja. Aina on
jokin aktiivinen peli-tila, ja tälle lähetetään piirto- ja
päivityskutsut. Piirtokutsuissa annetaan parametrina
`GraphicsContext`, jolla voi tehdä piirtokutsuja, ja päivityskutsuissa
annetaan parametrina `Input`, jolloin peli-tila voi reagoida
näppäimistön tai hiiren tilaan.

## Peli-tilat
`InGameState`, `IntroState`, `MainMenuState` ja `GameOverState` ovat
peli-tiloja, jotka hoitavat pelissä nimensämukaisten osien piirtämisen
ja logiikan. Käytännössä ne ovat siis luokkia, jotka implementoivat
GameState-rajapinnan, joka sisältää funktiot `draw`, `update` ja
`initialize`.

## Pelin ydin
`InGameState` on itse pelin ydin. Se luo ja kutsuu `Dungeon`:ia, joka
sisältää tiedot kentän koostavista laatoista, sekä listan kentällä
oleskelevista eliöístä. `DungeonRenderer` hoitaa pelimaailman
piirtämisen perustuen `Dungeon`:n dataan. `InGameState`:n tekemät
kutsut näkyvät sekvenssikaaviossa, mutta informaalimmin:
`update`-funktiossa `InGameState` tarkistaa painaako pelaaja mitään
nappeja jotka liikuttaisi pelaajaa, mikäli painaa, pelaaja liikkuu
kutsulla `Player`:n, ja sitten koska pelaaja liikkui, `InGameState`
kutsuu myös `Dungeon`:n vuoro-prosessointi-funktiota. Tämän jälkeen
`InGameState` vielä tarkistaa, onko käyttöliittymän nappeja painettu,
tai ruutuja valittu, ja päivittää käyttöliittymää pelitilanteen
mukaan. Piirtäminen tapahtuu `draw`-funktiossa, missä `InGameState`
kutsuu `DungeonRenderer`:iä piirtääkseen pelimaailman, ja sen jälkeen
piirtää loput käyttöliittymästä.

## Pelimaailma
`Dungeon` toimii pelimaailman loogisena ytimenä, ja se tekee kutsuja
`Entity`:ille jotka ovat myös `AI`:ta liikuttaakseen pelimaailmassa
olevia "eläviä" olentoja. Jokaisen vuoron lopussa kutsutaan myös
`Dungeon`:n metodia joka siivoaa kuolleet olennot pois kentältä, mikä
käytännössä tarkoittaa niiden poistamista `Entity`-listalta.

## Sekvenssikaavio
![Tässä on kuva joka esittää ohjelman kulkua sekvenssikaaviona. Jos et
näe kuvaa, se ei luultavasti ole ladannut tai tule
latautumaan.](sequence-diagram.png)

## Leaderboardit
Pelin päätyttyä pelaaja voi lähettää saamansa pisteet
leaderboard-palvelimelle, ja kaikki koodi (palvelin mukaanlukien)
tähän liittyen löytyy paketista `otm.roguesque.net`.

## Loppuhuomioita
Pelin tila on käyttöliittymäluokan sisällä, mikä voi vaikuttaa
ensisilmäyksellä pahalta logiikan ja presentaation
sekoittamiselta. Perustelen tämän kuitenkin sillä, että tämä välttää
turhaa tiedon passailua ympäri koodia, ja pelit ovat jokatapauksessa
hyvin riippuvaisia käyttöliittymästään, joten koodin kytkemistä on
vaikea välttää.

Käyttöliittymässä ei ole käytetty JavaFX:n ominaisuuksia, lähinnä sen
takia, että ne toimivat ns. "retained GUI" periaatteella, kun taas
suunnittelin pelin käyttöliittymän "immediate mode GUI" periaatteilla.
