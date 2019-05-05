# Roguesque
Roguesque on Roguen kaltainen peli. Pelaaja seikkailee luolastoissa
vuoropohjaisilla mekaniikoilla, löytäen aarteita ja taistellen
kaikenlaisia vastustajia vastaan. Peli toimii harjoitustyönä
Ohjelmistotekniikan kurssille.

## Dokumentaatio
- [Käyttöohje](dokumentaatio/manual.md)
- [Vaatimusmäärittely](dokumentaatio/vaatimusmaarittely.md)
- [Arkkitehtuuri](dokumentaatio/arkkitehtuuri.md)
- [Testausdokumentti](dokumentaatio/testaus.md)
- [Työaikakirjanpito](dokumentaatio/tuntikirjanpito.md)

## Releaset
- [Viikko 5 (v0.1)](https://github.com/pcjens/otm-roguesque/releases/tag/v0.1)
- [Viikko 6 (v0.3)](https://github.com/pcjens/otm-roguesque/releases/tag/v0.3)
- [Loppupalautus (v1.0)](https://github.com/pcjens/otm-roguesque/releases/tag/loppupalautus)

## Komennot
### Pelin käynnistäminen
```sh
mvn compile exec:java -Dexec.mainClass=otm.roguesque.Main
```
Tai alternatiivisesti, jar-tiedoston käynnistämällä, kuten selitetty
alla.

### Suoritettavan jar-arkiston luominen
```sh
mvn package
```
Jar-arkisto luodaan polkuun
[`target/Roguesque-1.0.jar`](target/Roguesque-1.0.jar). Luodun
jar-arkiston voi suorittaa komennolla `java -jar
target/Roguesque-1.0.jar`.

#### Palvelin-jarin luominen
Mikäli haluat vain leaderboards-palvelimen suorittavan .jarin, ei
tarvitse suurinta osaa pelistä siihen sisällyttää. Tämän takia
Maven-projektissa on `server`-profiili, joka luo .jarin joka
käynnistyy vain ja ainoastaan palvelimena, eikä sisällä pelikoodia,
eli esimerkiksi vaadi JavaFX:ää. Server-profiilin saa päälle
`-Dserver` flagilla:
```sh
mvn package -Dserver
```

#### build.sh
Skripti `build.sh` sisältää jar-arkiston luomisen lisäksi testaamisen,
kattavuusraportin luomisen, checkstylen suorittamisen, ja zippaamisen
kaiken tarvittavan (`items.csv`) kanssa. Tuloksena on
`Roguesque-1.0.zip` valmiina jakoon.
```sh
sh build.sh
```

### Pelin testien suorittaminen
```sh
mvn test
```

#### Testien kattavuus
```sh
mvn test jacoco:report
```
Tulos löytyy tiedostosta
[`target/site/jacoco/index.html`](target/site/jacoco/index.html).

### Checkstyle
Koodin laatutarkastukset voi suorittaa seuraavalla komennolla:
```sh
mvn jxr:jxr checkstyle:checkstyle
```

### JavaDoc
JavaDocin voi generoida seuraavalla komennolla:
```sh
mvn javadoc:javadoc
```

## Muut huomiot
Henkilökohtainen GitHub-tilini [`neonmoe`](https://github.com/neonmoe/)
saattaa näkyä commit-listalla, koska en aina muista muuttaa git configeja 
ja Git-historia on ikuisempi kuin timantit :)
