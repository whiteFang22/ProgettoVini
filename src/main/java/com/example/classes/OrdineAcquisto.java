package com.example.classes;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class OrdineAcquisto implements Serializable{
    private Cliente cliente;
    private PropostaAcquisto propostaAssociata;
    private String indirizzoAzienda;
    private Date dataCreazione;
    private Impiegato impiegato;

    public OrdineAcquisto(Cliente cliente, Impiegato impiegato, PropostaAcquisto propostaAssociata, String indirizzoAzienda, Date dataCreazione) {
        this.cliente = cliente;
        this.impiegato = impiegato;
        this.propostaAssociata = propostaAssociata;
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
        return propostaAssociata.getVini();
    }

    public String getIndirizzoAzienda() {
        return indirizzoAzienda;
    }

    public void setIndirizzoAzienda(String indirizzoAzienda) {
        this.indirizzoAzienda = indirizzoAzienda;
    }

    public float calcolaTotale() {
        float totale = 0;
        for (Map.Entry<Vino, Integer> entry : propostaAssociata.getVini().entrySet()) {
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

    public Impiegato getImpiegato() {
        return impiegato;
    }

    public void setImpiegato(Impiegato impiegato) {
        this.impiegato = impiegato;
    }

    public PropostaAcquisto getPropostaAssociata() {
        return propostaAssociata;
    }

    public void setPropostaAssociata(PropostaAcquisto propostaAssociata) {
        this.propostaAssociata = propostaAssociata;
    }
}

