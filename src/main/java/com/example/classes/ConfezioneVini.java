package com.example.classes;

import java.util.HashMap;
import java.util.Map;

public class ConfezioneVini {
    private Map<Vino, Integer> vini;
    private float prezzo;

    public ConfezioneVini() {
        this.vini = new HashMap<>();
        this.prezzo = 0;
    }

    // Capisci se pieno o no
    public void aggiungiVino(Vino vino, int quantita) {
        vini.put(vino, quantita);
        calcolaPrezzo();
    }

    public void rimuoviVino(Vino vino) {
        vini.remove(vino);
        calcolaPrezzo();
    }

    public float getPrezzo() {
        return prezzo;
    }

    private void calcolaPrezzo() {
        prezzo = 0;
        for (Map.Entry<Vino, Integer> entry : vini.entrySet()) {
            Vino vino = entry.getKey();
            int quantita = entry.getValue();
            prezzo += vino.getPrezzo() * quantita;
        }
    }

    public void visualizza() {
        for (Map.Entry<Vino, Integer> entry : vini.entrySet()) {
            Vino vino = entry.getKey();
            System.out.println(vino.getNome()+" - quantità: "+entry.getValue());
        }
    }
}
