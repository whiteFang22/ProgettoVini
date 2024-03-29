package com.example.classes;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class PropostaAcquisto implements Serializable {
    private int id;
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
     public PropostaAcquisto(int id, Cliente cliente, Map<Vino, Integer> viniMancanti, String indirizzoConsegna, OrdineVendita ordineCorrispondente) {
        this.id = id;
        this.cliente = cliente;
        this.viniMancanti = viniMancanti;
        this.indirizzoConsegna = indirizzoConsegna;
        this.ordineCorrispondente = ordineCorrispondente;
    }
    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public Date getDataCreazione(){ return this.ordineCorrispondente.getDataCreazione(); }

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

