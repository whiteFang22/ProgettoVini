package com.example.classes;

import java.util.HashMap;
import java.util.Map;

public class ConfezioneVini {
    private Map<Vino, Integer> vini;
    private float prezzo;
    private int capacita = 5;
    private boolean pieno = false;

    public ConfezioneVini() {
        this.vini = new HashMap<>();
        this.prezzo = 0;
    }

    // Capisci se pieno o no
    public boolean aggiungiVino(Vino vino, int quantita) {
        //se la quantità di vino sta nella confezione
        if (capacita-quantita>=0){
            //se dentro alla confezione c'è gia un vino uguale aggiungo alla sua quantità
            if(this.vini.containsKey(vino)){
                int oldqty = vini.get(vino);
                vini.put(vino,oldqty+quantita);
            }
            //altrimenti aggiungo il nuovo vino
            else{
                vini.put(vino, quantita);
            }
            calcolaPrezzo();
            capacita-=quantita;
            if (capacita==0) pieno=true;
        }
        else pieno = true;
        return pieno;
    }
    public boolean rimuoviVino(Vino vino) {
        if (capacita<5){
            vini.remove(vino);
            calcolaPrezzo();
            capacita++;
        }
        else pieno = false;
        return pieno;
    }
    public Map<Vino,Integer> getVini(){ return vini; }

    public int getCapacita() {return capacita;}
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

    /*public void visualizza() {
        for (Map.Entry<Vino, Integer> entry : vini.entrySet()) {
            Vino vino = entry.getKey();
            //System.out.println(vino.getNome()+" - quantità: "+entry.getValue());
        }
    }*/
}
