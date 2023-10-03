package com.example.classes;

import java.util.List;

public class PropostaAcquisto {
    private Cliente cliente;
    private List<ConfezioneVini> confezioniVini;
    private List<CassaVino> casseVino;
    private List<Vino> viniMancanti;
    private String indirizzoConsegna;
    private OrdineVendita ordineCorrispondente;

    public PropostaAcquisto(Cliente cliente, List<Vino> viniMancanti, String indirizzoConsegna, OrdineVendita ordineCorrispondente) {
        this.cliente = cliente;
        this.viniMancanti = viniMancanti;
        this.indirizzoConsegna = indirizzoConsegna;
        this.ordineCorrispondente = ordineCorrispondente;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Vino> getVini() {
        return viniMancanti;
    }

    public void setVini(List<Vino> viniMancanti) {
        this.viniMancanti = viniMancanti;
    }

    public String getIndirizzoConsegna() {
        return indirizzoConsegna;
    }

    public void setIndirizzoConsegna(String indirizzoConsegna) {
        this.indirizzoConsegna = indirizzoConsegna;
    }

    public OrdineVendita getOrdineCorrispondente() {
        return ordineCorrispondente;
    }

    public void setOrdineCorrispondente(OrdineVendita ordineCorrispondente) {
        this.ordineCorrispondente = ordineCorrispondente;
    }
}

