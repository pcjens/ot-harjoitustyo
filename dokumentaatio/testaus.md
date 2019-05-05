# Testausdokumentti
Pelin testaus koostuu JUnit-yksikkötesteistä, sekä manuaalisesti
tehdyllä käyttöohjeenmukaisella järjestelmätestauksella.

## JUnit-yksikkötestit
Yksikkötestit testaavat pelin yksittäisiä osia jotka ovat järkevästi
testattavissa.

[`TileGraphicTest`](../src/test/java/otm/roguesque/TileGraphicTest.java) varmistaa,
että DungeonRenderer käyttää oikeita polkuja kentän piirtämisessä
käytettäville kuville, ja että kyseisistä poluista ladatut kuvat
latautuvat onnistuneesti.

[`PlayerTest`](../src/test/java/otm/roguesque/PlayerTest.java) testaa pelaajaan liittyviä asioita:
- Pelaajan näköetäisyysalgoritmia testataan neljällä testillä jotka
  varmistavat, että pelaaja ei näe pidemmälle kuin pitäisi, eikä
  seinien läpi.
- Pelaajan "tutkitun asian kuvaus" -tekstin ilmestyminen, sekä sen
  resetoimisen toimivuus testataan.
- Seinään törmäämisen reportointi tarkistetaan. Tarkistetaan myös,
  liikkuuko pelaaja kun ei ole mitään edessä, ja pysyykö pelaaja
  paikallaan mikäli on.
- Varmistetaan, kuoleeko pelaaja tilanteessa, jossa sen elämäpisteet
  tippuvat nollaan.
- Varmistetaan, saako pelaaja oikeat muutokset statteihinsa
  nostettuaan tavaran.

[`EnemyTest`](../src/test/java/otm/roguesque/EnemyTest.java) testaa
vastustajien tekoälyjen prioriteetteja.

[`LeaderboardsTest`](../src/test/java/otm/roguesque/LeaderboardsTest.java)
testaa leaderboardien tietoliikenteen toimintaa käytännössä, oikealla
serverillä ja oikealla clientillä.

### JUnit-yksikkötestien kattavuus
Huomio: koska kyseessä on peli, testien kattavuus nousee nopeasti
muutamalla testillä tiettyyn pisteeseen asti, sillä iso osa koodista
on sellaista mikä pitää aina ajaa kun peliä pelataan. Toisaalta,
koodikattavuuden alueella on myös koodia, jota kutsutaan pelkästään
käyttöliittymästä, eikä siinä ole paljon testattavaa.

Tässä kuitenkin yksikkötestien kattavuus versiossa 1.0 (loppupalautus):
![Kuvakaappaus jacoco:n koodikattavuus-reportista](code-coverage.png)

## Järjestelmätestaus
Kokeiltu versio: 1.0 (loppupalautus).

### Asennus ym. ohjelman pystyyn pistäminen
Puran `Roguesque-1.0.zip`-arkiston ohjeen mukaisesti, ja varmistan,
että `Roguesque-1.0.jar`:n vieressä on `items.csv`.

### Ohjelman kulku
Käynnistän pelin komennolla `java -jar Roguesque-1.0.jar`, kuten
Java-ohjelmat yleensäkin. Alkuun avautuu ruutu josta voi pelata
(`Play`-nappi) tai sulkea pelin (`Quit`-nappi), klikkaan `Play`
käyttöohjeen mukaisesti. Käyttöohjeessa mainitut näppäimet hahmon
liikkuttamiseksi sekä pelaajan valitsemiseksi toimivat. Myöskin
hyökkääminen toimii kuten käyttöohjeessa kuvailtiin. Näkökenttä toimii
myös käyttöohjeen mukaisesti, testaan saamalla rotan seuraamaan
pelaajaa, ja liikkumalla pois päin. Kun rotta jää pimeyteen, se
katoaa. Käyn hetken aikaa luolaa läpi, ja löydän tikkaat. Käyttöohjeen
mukaisesti liikun tikkaiden päälle, ja painan M-näppäintä, ja pelaaja
siirtyy uuteen kenttään. Lopuksi vielä painan F3-näppäintä, ja
kaikenlaista informaatiota ilmestyy
ruudulle. Debug-informaationäkymäkin siis vaikuttaa toimivan.

Kokeilen vaihtaa `items.csv`:ssä kaikkien tavaroiden kuva-resurssin
poluksi `file:Test.png`, ja tallennan pelin kanssa samaan kansioon
kuvan nimeltä `Test.png`. Testaan peliä, ja kaikkien arkuista
löytyvien tavaroiden grafiikka on piirtämäni `Test.png`.  
![Kuva yllä kuvatusta
tilanteesta.](screenshots/switched-item-graphic.png)

Sijoitan `Roguesque-1.0.jar`-arkiston kanssa samaan kansioon
`rgsq-server.config` tiedoston, sisällöllä:

```
HOST=127.0.0.1
PORT=5378
```

Käynnistän palvelimen komennolla `java -jar Roguesque-1.0.jar
--server`, ja pelaan pelin. Leaderboardit vaikuttavat olevan tyhjät,
lukuunottamatta juuri lisäämääni pisteytystä. Kun suljen palvelimen
Ctrl-C:llä, huomaan `roguesque-server-data.csv`-tiedoston ilmestyneen,
ja se sisältää juuri lisäämäni pisteytyksen.

Peli siis toimii käyttöohjeen mukaisesti.
