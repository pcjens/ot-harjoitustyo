# Arkkitehtuuri

## Luokkakaavio
![Tässä on kuva joka esittää ohjelman arkkitehtuuria
luokkakaaviona. Jos et näe kuvaa, se ei luultavasti ole ladannut tai
tule latautumaan.](class-diagram.png)

Luokkakaavio on hieman vanhasta versiosta ohjelmaa, mutta siitä näkee
pääosin ohjelman luokat ja niiden väliset suhteet.

`Main` sisältää kaksi osaa: `pom.properties`-tiedoston lukemisen
versioinformaatiota varten, ja ohjelman käynnistysfunktion, joka
yksinkertaisesti käynnistää käyttöliittymän `RoguesqueApp`:n
launchaamalla.

`RoguesqueApp` sisältää ruudun luontilogiikan, sekä eventtien
käsittelyn. Eventeistä tieto siirtyy `Input`-luokkaan, joka toimii
abstraktiona hiirelle ja näppäimistölle. `RoguesqueApp` sisältää myös
taulukon `GameState`:ja, jotka kuvaavat erilaisia peli-tiloja. Aina on
jokin aktiivinen peli-tila, ja tälle lähetetään piirto- ja
päivityskutsut.

## Peli-tilat
`InGameState`, `IntroState`, `MainMenuState` ja `GameOverState` ovat
peli-tiloja, jotka hoitavat pelissä nimensämukaisten osien piirtämisen
ja logiikan. Käytännössä ne ovat siis luokkia, jotka implementoivat
GameState-rajapinnan, joka sisältää funktiot `draw`, `update` ja
`initialize`.

## Pelin ydin
`InGameState` on itse pelin ydin. Se luo ja kutsuu `Dungeon`ia, joka
sisältää tiedot kentän koostavista laatoista, sekä listan kentällä
oleskelevista eliöístä. `DungeonRenderer` hoitaa pelimaailman
piirtämisen perustuen `Dungeon`:n dataan. `InGameState`:n tekemät
kutsut näkyvät sekvenssikaaviossa, mutta informaalimmin:
`update`-funktiossa `InGameState` tarkistaa painaako pelaaja mitään
nappeja jotka liikuttaisi pelaajaa, mikäli painaa, pelaaja liikkuu
kutsulla `Player`:n, ja sitten koska pelaaja liikkui, `InGameState`
kutsuu myös `Dungeon`:n vuoron prosessointifunktiota. Tämän jälkeen
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
