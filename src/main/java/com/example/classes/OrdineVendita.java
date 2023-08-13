package com.example.classes;

import java.util.Date;
import java.util.List;

public class OrdineVendita {
    private Cliente cliente;
    private List<ConfezioneVini> confezioniVini;
    private List<CassaVino> casseVino;
    private String indirizzoConsegna;
    private Date dataConsegna;

    public OrdineVendita(Cliente cliente, List<ConfezioneVini> confezioniVini, List<CassaVino> casseVino,
                         String indirizzoConsegna, Date dataConsegna) {
        this.cliente = cliente;
        this.confezioniVini = confezioniVini;
        this.casseVino = casseVino;
        this.indirizzoConsegna = indirizzoConsegna;
        this.dataConsegna = dataConsegna;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ConfezioneVini> getconfezioniVini() {
        return confezioniVini;
    }

    public void setconfezioniVini(List<ConfezioneVini> confezioniVini) {
        this.confezioniVini = confezioniVini;
    }

    public List<CassaVino> getCasseVino() {
        return casseVino;
    }

    public void setCasseVino(List<CassaVino> casseVino) {
        this.casseVino = casseVino;
    }

    public String getIndirizzoConsegna() {
        return indirizzoConsegna;
    }

    public void setIndirizzoConsegna(String indirizzoConsegna) {
        this.indirizzoConsegna = indirizzoConsegna;
    }

    public Date getDataConsegna() {
        return dataConsegna;
    }

    public void setDataConsegna(Date dataConsegna) {
        this.dataConsegna = dataConsegna;
    }

    public float calcolaTotale() {
        float totale = 0;
        for (ConfezioneVini confezione : confezioniVini) {
            totale += confezione.getPrezzo();
        }
        for (CassaVino cassa : casseVino) {
            totale += cassa.getPrezzo();
        }
        return totale;
    }
}
