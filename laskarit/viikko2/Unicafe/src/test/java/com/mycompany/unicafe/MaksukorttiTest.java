package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MaksukorttiTest {

    Maksukortti kortti;

    @Before
    public void setUp() {
        kortti = new Maksukortti(10);
    }

    @Test
    public void luotuKorttiOlemassa() {
        assertTrue(kortti!=null);      
    }
    
    @Test
    public void saldoAlussaOikein() {
        assertEquals("saldo: 0.10", kortti.toString());
    }
    
    @Test
    public void rahanLataaminenToimii() {
        kortti.lataaRahaa(100);
        assertEquals("saldo: 1.10", kortti.toString());
    }
    
    @Test
    public void ottaminenToimii() {
        kortti.otaRahaa(10);
        assertEquals("saldo: 0.0", kortti.toString());
    }
    
    @Test
    public void liikaaOttaminenEiToimi() {
        kortti.otaRahaa(11);
        assertEquals("saldo: 0.10", kortti.toString());
    }

    @Test
    public void otaRahaaPalauttaaTrueKunOttaminenOnnistuu() {
        assertTrue(kortti.otaRahaa(10));
    }

    @Test
    public void otaRahaaPalauttaaFalseKunOttaminenEiOnnistu() {
        assertFalse(kortti.otaRahaa(11));
    }
}
