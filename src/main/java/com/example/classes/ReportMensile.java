package com.example.classes;

import java.util.Map;

public class ReportMensile {
    private float introiti;
    private float spese;
    private int bottiglieVendute;
    private int bottiglieDisponibili;
    private Map<Vino, Integer> venditePerVino;
    private Map<String, Integer> valutazioneDipendenti;

    public ReportMensile(float introiti, float spese, int bottiglieVendute, int bottiglieDisponibili,
                         Map<Vino, Integer> venditePerVino, Map<String, Integer> valutazioneDipendenti) {
        this.introiti = introiti;
        this.spese = spese;
        this.bottiglieVendute = bottiglieVendute;
        this.bottiglieDisponibili = bottiglieDisponibili;
        this.venditePerVino = venditePerVino;
        this.valutazioneDipendenti = valutazioneDipendenti;
    }

    public float getIntroiti() {
        return introiti;
    }

    public void setIntroiti(float introiti) {
        this.introiti = introiti;
    }

    public float getSpese() {
        return spese;
    }

    public void setSpese(float spese) {
        this.spese = spese;
    }

    public int getBottiglieVendute() {
        return bottiglieVendute;
    }

    public void setBottiglieVendute(int bottiglieVendute) {
        this.bottiglieVendute = bottiglieVendute;
    }

    public int getBottiglieDisponibili() {
        return bottiglieDisponibili;
    }

    public void setBottiglieDisponibili(int bottiglieDisponibili) {
        this.bottiglieDisponibili = bottiglieDisponibili;
    }

    public Map<Vino, Integer> getVenditePerVino() {
        return venditePerVino;
    }

    public void setVenditePerVino(Map<Vino, Integer> venditePerVino) {
        this.venditePerVino = venditePerVino;
    }

    public Map<String, Integer> getValutazioneDipendenti() {
        return valutazioneDipendenti;
    }

    public void setValutazioneDipendenti(Map<String, Integer> valutazioneDipendenti) {
        this.valutazioneDipendenti = valutazioneDipendenti;
    }
}
