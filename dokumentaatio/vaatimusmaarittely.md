# Vaatimusmäärittely
## Kuvaus
Rogue-tyylinen luolaseikkailu / roolipeli.

## Perustoiminnallisuus
Pelaaja voi seikkailla ylhäältä kuvatussa satunnaisesti generoidussa
kaksiulotteisessa luolastossa, löytäen portaita ylöspäin, kunnes
lopulta päätyy ylimmälle tasolle jossa on päihitettävänä viimeinen
(vaikea) vastustaja. Pelin ydinominaisuuksia ovat taistelu
satunnaisesti sijoitettujen vastustajien kanssa. Taistelussa auttaa
tavarat jotka parantavat pelaajan hyökkäys- ja puolustusarvoja, jotka
päättävät pelaajan onnekkuuden taistelussa.

Peli tulee myös pystyä tallentamaan, ja palaamaan entiseen
tallennukseen. Roguen perinteen mukaisesti tallennuksia voi kuitenkin
olla vain yksi per hahmo, eli pelin voi tallentaa vain lopettaessa sen
(eli peliä voi jatkaa myöhemmin, mutta häviöiltä ei ole pakenemista).

Pääominaisuuksia:
- Pelimaailma koostuu 2D ruudukosta
- Pelaaja voi liikkua ylös, alas ja sivuille
- Peli (ja siten vastustajat) etenee askeleen joka kerta kun pelaaja
  ottaa askeleen
- Pelaajalla (ja vastustajilla) on muutamia muuttujia:
  - Elämäpisteet, kun nämä tippuvat nollaan, peli loppuu
  - Hyökkäyspisteet, nämä määrittelevät kuinka paljon vahinkoa
    vastustajaan tehdään
  - Puolustuspisteet, nämä heikentävät vastustajan hyökkäyspisteitä
- Taistelu pelissä tapahtuu seuraavasti:
  - Jos pelaaja liikkuu ruutuun jossa on vastustaja, tai vastustaja
    liikkuu ruutuun jossa on pelaaja, se joka liikkuu toisen ruutuun,
    lyö liikkumisen sijaan
  - Lyönti vähentää elämäpisteitä perustuen laskuun joka tulee olemaan
    jotakuinkin muotoa `hp -= max(0, toisen_atk - def)` missä `hp` on
    lyönnin kohteen elämäpisteet, `toisen_atk` on lyöjän
    hyökkäyspisteet, ja `def` on kohteen puolustuspisteet.
- Vastustajat liikkuvat satunnaisiin suuntiin vuorollaan
  - Jotkut vastustajat eivät hyökkää suoraan, toiset alkavat suunnata
    pelaajaa kohti heti kun näkevät hänet
  - Vastustajat eroavat toisistaan nimiltään, kuvauksiltaan ja
    hyökkäys/puolustusarvoiltaan
- Pelaaja voi löytää tavaroita
  - Tavarat voivat nostaa pelaajan muuttujia tavalla jonka pelaaja saa
    selville klikatessaan tavaraa (esim. +1 hyökkäyspisteitä)
- Pelaaja voi jatkaa seuraavalle tasolle kävelemällä porras-ruutuun ja
  klikkaamalla ilmestyvää nappia
- Pelaaja voi klikata maailmassa olevia asioita hiirellä, ja tämä
  näyttää selityksen klikatusta ruudusta. Jos ruudussa on esimerkiksi
  peikko, sitä klikatessa ilmestyisi lyhyt selitys peikosta.

## Jatkokehitysideoita
Monipuolisempia kenttiä, enemmän vastustajia, enemmän tavaroita,
mahdollisesti jopa erilaisia aloitushahmoja. Ehkä jopa moninpeli
internetin yli, jos jää aikaa.

## Käyttöliittymä
Pelinäkymä on hyvin tuttu kenelle tahansa joka on aikaisemmin nähnyt
kuvia Roguesta tai pelannut sitä. Vaikka grafiikat ovatkin
tekstipohjaisia, ne tulee toteuttaa JavaFX:llä, eli terminaalien
rajoituksia ei tarvitse ottaa huomioon, esimerkiksi eri tekstialueiden
fonttikoossa. Tässä luonnoksessa näkyy eri ruudut joiden välillä
pelaaja voi siirtyillä.

![UI sketch](ui-sketch.jpg)

Pelaajalle siis näytetään alussa yläoikealla oleva ruutu, jossa he
voivat alottaa joko uuden pelin, tai ladata viimeksi kesken
jääneen. Peli voi loppua voittoon tai häviöön, ja lopussa näytetään
taulukko jossa näkee edellisten hahmojen saamat pisteet.

Pelin sisällä värit voivat toimia symbolien lisäksi indikaattoreina:
vastustajat ovat punaisia, pelaaja on sininen, tavarat ovat keltaisia
