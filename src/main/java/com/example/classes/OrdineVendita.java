package com.example.classes;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrdineVendita {
    private Cliente cliente;
    private Map<Vino, Integer> viniAcquistati;
    private List<ConfezioneVini> confezioniVini;
    private List<CassaVino> casseVino;
    private String indirizzoConsegna;
    private Date dataConsegna;
    private Date dataCreazione;

    public OrdineVendita(Cliente cliente, String indirizzoConsegna, Date dataConsegna, Date dataCreazione) {
        this.cliente = cliente;
        this.indirizzoConsegna = indirizzoConsegna;
        this.dataConsegna = dataConsegna;
        this.dataCreazione = dataCreazione;
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

    /*
       nel server dopo aver Calcolato il Map va inserito nell'ordine
       così che una volta restituito al client, esso possa accedere
       a tutte le informazioni senza dover contattare nuovamente il server
   */
    public void setViniAcquistati(Map<Vino, Integer> vini){
        this.viniAcquistati = vini;
    }
    /*
        Dato il Map viniAquistati calcola il numero di casse e di confezioni
    */
    public void creaContenitori(){

    }

    // retituisce il prezzo senza però applicare gli sconti, poi lo completo
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

    public Date getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
}
