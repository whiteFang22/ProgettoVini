package com.example.classes;

import java.util.Date;
import java.util.Map;

public class OrdineAcquisto {
    private Cliente cliente;
    private Map<Vino, Integer> viniMancanti;
    private String indirizzoAzienda;
    private Date dataCreazione;

    public OrdineAcquisto(Cliente cliente, Map<Vino, Integer> viniMancanti, String indirizzoAzienda, Date dataCreazione) {
        this.cliente = cliente;
        this.viniMancanti = viniMancanti;
        this.indirizzoAzienda = indirizzoAzienda;
        this.dataCreazione = dataCreazione;
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

    public Date getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
}

