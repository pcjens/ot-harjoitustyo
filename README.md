# Roguesque
Roguesque on Roguen kaltainen peli. Pelaaja seikkailee luolastoissa
vuoropohjaisilla mekaniikoilla, löytäen aarteita ja taistellen
kaikenlaisia vastustajia vastaan. Peli toimii harjoitustyönä
Ohjelmistotekniikan kurssille.

## Dokumentaatio
- [Vaatimusmäärittely](dokumentaatio/vaatimusmaarittely.md)
- [Tuntikirjanpito](dokumentaatio/tuntikirjanpito.md)
- [Manuaali](dokumentaatio/manual.md)

## Komennot
### Pelin käynnistäminen
```sh
mvn compile exec:java -Dexec.mainClass=otm.roguesque.Main
```
Tai alternatiivisesti, jar-tiedoston käynnistämällä, kuten selitetty alla.

### Suoritettavan jar-arkiston luominen
```sh
mvn package
```
Jar-arkisto luodaan polkuun [`target/Roguesque-0.1-SNAPSHOT.jar`](target/Roguesque-0.1-SNAPSHOT.jar). Luodun jar-arkiston voi suorittaa komennolla `java -jar target/Roguesque-0.1-SNAPSHOT.jar`.

### Pelin testien suorittaminen
```sh
mvn test
```

#### Testien kattavuus
```sh
mvn test jacoco:report
```
Tulos löytyy tiedostosta [`target/site/jacoco/index.html`](target/site/jacoco/index.html).
