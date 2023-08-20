package com.example.classes;

public class CassaVino {
    private Vino vino;
    private int quantita;//6 o 12å
    private float scontoPercentuale;
    private float prezzo;

    public CassaVino(Vino vino, int quantita, float scontoPercentuale) {
        this.vino = vino;
        this.quantita = quantita;
        this.scontoPercentuale = scontoPercentuale;
        this.prezzo = calcolaPrezzo();
    }

    public Vino getVino() {
        return vino;
    }

    public void setVino(Vino vino) {
        this.vino = vino;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
        //this.prezzo = calcolaPrezzo();
    }

    public float getScontoPercentuale() {
        return scontoPercentuale;
    }

    public void setScontoPercentuale(float scontoPercentuale) {
        this.scontoPercentuale = scontoPercentuale;
        //this.prezzo = calcolaPrezzo();
    }

    public float getPrezzo() {
        //this.prezzo = calcolaPrezzo();
        return prezzo;
    }

    //controlla che siano già stati inseriti quantità e sconto
    //in realtà compilo un form quindi sempre di default
    private float calcolaPrezzo() {
        float prezzoUnitario = vino.getPrezzo();
        float sconto = (scontoPercentuale / 100) * prezzoUnitario;
        float prezzoTotale = prezzoUnitario * quantita - sconto;
        return prezzoTotale;
    }


}
