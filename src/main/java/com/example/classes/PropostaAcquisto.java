package com.example.classes;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PropostaAcquisto implements Serializable {
    private Cliente cliente;
    private Map<Vino, Integer> viniMancanti;
    private String indirizzoConsegna;
    private OrdineVendita ordineCorrispondente;

    public PropostaAcquisto(Cliente cliente, Map<Vino, Integer> viniMancanti, String indirizzoConsegna, OrdineVendita ordineCorrispondente) {
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

    public Map<Vino, Integer> getVini() {
        return viniMancanti;
    }

    public void setVini(Map<Vino, Integer> viniMancanti) {
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

