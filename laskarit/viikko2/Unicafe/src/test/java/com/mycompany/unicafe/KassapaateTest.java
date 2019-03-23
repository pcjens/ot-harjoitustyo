package com.mycompany.unicafe;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class KassapaateTest {
    Kassapaate kassapaate;
    
    @Before
    public void setUp() {
        kassapaate = new Kassapaate();
    }

    @Test
    public void alussaOikeaMaaraRahaa() {
        assertTrue(kassapaate.kassassaRahaa() == 100000);
    }

    @Test
    public void alussaOikeaMaaraMyytyjaLounaita() {
        assertTrue(kassapaate.edullisiaLounaitaMyyty() + kassapaate.maukkaitaLounaitaMyyty() == 0);
    }
    
    @Test
    public void kortilleLataaminenNostaaSaldoaOikein() {
        Maksukortti kortti = new Maksukortti(0);
        kassapaate.lataaRahaaKortille(kortti, 1000);
        assertTrue(kortti.saldo() == 1000);
    }
    
    @Test
    public void kortilleLataaminenNostaaKassaaOikein() {
        Maksukortti kortti = new Maksukortti(0);
        kassapaate.lataaRahaaKortille(kortti, 1000);
        assertTrue(kassapaate.kassassaRahaa() == 101000);
    }
    
    @Test
    public void kortilleNegatiivisenRahanLataaminenEiTeeMitaan() {
        Maksukortti kortti = new Maksukortti(100);
        kassapaate.lataaRahaaKortille(kortti, -50);
        assertTrue(kortti.saldo() == 100);
        assertTrue(kassapaate.kassassaRahaa() == 100000);
    }
    
    // Käteisellä tapahtuvat edulliset ostot
    
    @Test
    public void onnistunutEdullinenOstoNostaaKassanRahoja() {
        kassapaate.syoEdullisesti(260);
        assertTrue(kassapaate.kassassaRahaa() == 100240);
    }
    
    @Test
    public void onnistunutEdullinenOstoNostaaMyytyjenLounaidenMaaraa() {
        kassapaate.syoEdullisesti(260);
        assertTrue(kassapaate.edullisiaLounaitaMyyty() == 1);
    }

    @Test
    public void onnistunutEdullinenOstoAntaaOikeanVaihtorahan() {
        assertTrue(kassapaate.syoEdullisesti(260) == 20);
    }

    @Test
    public void edullinenOstoEiOnnistuLiianPienellaMaksulla() {
        assertTrue(kassapaate.syoEdullisesti(230) == 230);
        assertTrue(kassapaate.edullisiaLounaitaMyyty() == 0);
        assertTrue(kassapaate.kassassaRahaa() == 100000);
    }
    
    // Käteisellä tapahtuvat maukkaat ostot
    
    @Test
    public void onnistunutMaukasOstoNostaaKassanRahoja() {
        kassapaate.syoMaukkaasti(420);
        assertTrue(kassapaate.kassassaRahaa() == 100400);
    }
    
    @Test
    public void onnistunutMaukasOstoNostaaMyytyjenLounaidenMaaraa() {
        kassapaate.syoMaukkaasti(420);
        assertTrue(kassapaate.maukkaitaLounaitaMyyty() == 1);
    }

    @Test
    public void onnistunutMaukasOstoAntaaOikeanVaihtorahan() {
        assertTrue(kassapaate.syoMaukkaasti(420) == 20);
    }

    @Test
    public void maukasOstoEiOnnistuLiianPienellaMaksulla() {
        assertTrue(kassapaate.syoMaukkaasti(390) == 390);
        assertTrue(kassapaate.maukkaitaLounaitaMyyty() == 0);
        assertTrue(kassapaate.kassassaRahaa() == 100000);
    }
    
    // Kortilla tapahtuvat edulliset ostot
    
    @Test
    public void edullinenKorttiOstoToimiiKunSaldoRiittaa() {
        Maksukortti kortti = new Maksukortti(1000);
        assertTrue(kassapaate.syoEdullisesti(kortti));
        assertTrue(kortti.saldo() == 760);
    }
    
    @Test
    public void edullinenKorttiOstoEiMuutaKassaa() {
        Maksukortti kortti = new Maksukortti(1000);
        kassapaate.syoEdullisesti(kortti);
        assertTrue(kassapaate.kassassaRahaa() == 100000);
    }
    
    @Test
    public void edullinenKorttiOstoNostaaMyytyjenLounaidenMaaraa() {
        Maksukortti kortti = new Maksukortti(1000);
        kassapaate.syoEdullisesti(kortti);
        assertTrue(kassapaate.edullisiaLounaitaMyyty() == 1);
    }
    
    @Test
    public void edullinenKorttiOstoEiToimiKunSaldoEiRiita() {
        Maksukortti kortti = new Maksukortti(230);
        assertFalse(kassapaate.syoEdullisesti(kortti));
        assertTrue(kassapaate.edullisiaLounaitaMyyty() == 0);
        assertTrue(kortti.saldo() == 230);
    }
    
    // Kortilla tapahtuvat maukkaat ostot
    
    @Test
    public void maukasKorttiOstoToimiiKunSaldoRiittaa() {
        Maksukortti kortti = new Maksukortti(1000);
        assertTrue(kassapaate.syoMaukkaasti(kortti));
        assertTrue(kortti.saldo() == 600);
    }
    
    @Test
    public void maukasKorttiOstoEiMuutaKassaa() {
        Maksukortti kortti = new Maksukortti(1000);
        kassapaate.syoMaukkaasti(kortti);
        assertTrue(kassapaate.kassassaRahaa() == 100000);
    }
    
    @Test
    public void maukasKorttiOstoNostaaMyytyjenLounaidenMaaraa() {
        Maksukortti kortti = new Maksukortti(1000);
        kassapaate.syoMaukkaasti(kortti);
        assertTrue(kassapaate.maukkaitaLounaitaMyyty() == 1);
    }
    
    @Test
    public void maukasKorttiOstoEiToimiKunSaldoEiRiita() {
        Maksukortti kortti = new Maksukortti(390);
        assertFalse(kassapaate.syoMaukkaasti(kortti));
        assertTrue(kassapaate.maukkaitaLounaitaMyyty() == 0);
        assertTrue(kortti.saldo() == 390);
    }
}
