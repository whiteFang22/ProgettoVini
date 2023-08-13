package com.example.classes;

import java.util.List;

public class PropostaAcquisto {
    private Cliente cliente;
    private List<ConfezioneVini> confezioniVini;
    private List<CassaVino> casseVino;
    private String indirizzoConsegna;

    public PropostaAcquisto(Cliente cliente, List<ConfezioneVini> confezioniVini, List<CassaVino> casseVino,
                            String indirizzoConsegna) {
        this.cliente = cliente;
        this.confezioniVini = confezioniVini;
        this.casseVino = casseVino;
        this.indirizzoConsegna = indirizzoConsegna;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ConfezioneVini> getConfezioniVini() {
        return confezioniVini;
    }

    public void setConfezioniVini(List<ConfezioneVini> confezioniVini) {
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
}

