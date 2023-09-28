package com.example.classes;

import java.util.Map;

public class OrdineAcquisto {
    private Cliente cliente;
    private Map<Vino, Integer> viniMancanti;
    private String indirizzoAzienda;

    public OrdineAcquisto(Cliente cliente, Map<Vino, Integer> viniMancanti, String indirizzoAzienda) {
        this.cliente = cliente;
        this.viniMancanti = viniMancanti;
        this.indirizzoAzienda = indirizzoAzienda;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Map<Vino, Integer> getViniMancanti() {
        return viniMancanti;
    }

    public void setViniMancanti(Map<Vino, Integer> viniMancanti) {
        this.viniMancanti = viniMancanti;
    }

    public String getIndirizzoAzienda() {
        return indirizzoAzienda;
    }

    public void setIndirizzoAzienda(String indirizzoAzienda) {
        this.indirizzoAzienda = indirizzoAzienda;
    }

    public float calcolaTotale() {
        float totale = 0;
        for (Map.Entry<Vino, Integer> entry : viniMancanti.entrySet()) {
            Vino vino = entry.getKey();
            int quantita = entry.getValue();
            totale += vino.getPrezzo() * quantita;
        }
        return totale;
    }
}

