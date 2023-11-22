package com.example.classes;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

public class OrdineVendita implements Serializable{
    private Cliente cliente;
    private Map<Vino, Integer> viniAcquistati;
    private List<ConfezioneVini> confezioniVini = new ArrayList<>();
    private List<CassaVino> casseVino = new ArrayList<>();
    private String indirizzoConsegna;
    private Date dataConsegna;
    private Date dataCreazione;
    private boolean completato;
    private boolean firmato;
    private int id;

    public boolean isCompletato() {
        return completato;
    }
    public void setCompletato(boolean completato) {
        this.completato = completato;
    }

    //Ridondante il campo indirizzo di consegna, ses ha gia il cliente è sufficiente fare cliente.getIndirizzodiConsegna()
    public boolean isFirmato() {
        return firmato;
    }
    public void setFirmato(boolean firmato) {
        this.firmato = firmato;
    }
    public OrdineVendita(Cliente cliente, Date dataConsegna, Date dataCreazione) {
        this.cliente = cliente;
        //QUA
        this.indirizzoConsegna = cliente.getIndirizzoDiConsegna();
        this.dataConsegna = dataConsegna;
        this.dataCreazione = dataCreazione;
    }
    public OrdineVendita(Cliente cliente,Map<Vino, Integer> viniAcquistati, Date dataConsegna, Date dataCreazione) {
        this.cliente = cliente;
        //QUA
        this.indirizzoConsegna = cliente.getIndirizzoDiConsegna();
        this.dataConsegna = dataConsegna;
        this.dataCreazione = dataCreazione;
        this.viniAcquistati = viniAcquistati;
    }
    public OrdineVendita(int id,Cliente cliente,Map<Vino, Integer> viniAcquistati, Date dataConsegna, Date dataCreazione) {
        this.cliente = cliente;
        //QUA
        this.indirizzoConsegna = cliente.getIndirizzoDiConsegna();
        this.dataConsegna = dataConsegna;
        this.dataCreazione = dataCreazione;
        this.viniAcquistati = viniAcquistati;
        this.id = id;
    }
    public int getId(){
        return id;
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
    public Map<Vino,Integer> getViniAcquistati(){
        return this.viniAcquistati;
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

    public void ottimizza(){
        Map<Vino,Integer> rimanenti = new HashMap<Vino,Integer>();
        for (Map.Entry<Vino, Integer> line : this.viniAcquistati.entrySet()){
            Vino vino = line.getKey();
            int quantita = line.getValue();
            //dividi i vini in casse e confezione

            //casse da 12
            for(int casse12 = quantita / 12; casse12>0; casse12--){
                //aggiungi casse da 12
                this.casseVino.add(new CassaVino(vino, 12 , 0));        //INSERIRE QUI VALORE SCONTI
            }
            int rimanenti12 = quantita % 12;
            //casse da 6
            for(int casse6 = rimanenti12 / 6; casse6>0; casse6--){
                //aggiungi casse da 6
                this.casseVino.add(new CassaVino(vino, 6, 0));          //ANCHE QUI
            }
            //rimanenti in confezione
            int bottiglieRimaste = rimanenti12 % 6;
            //lista dei rimanenti da inserire in confezioni
            rimanenti.put(vino,bottiglieRimaste);
        }
        ConfezioneVini confezione = new ConfezioneVini();

        for (Map.Entry<Vino, Integer> entry : rimanenti.entrySet()) {
            Vino vino = entry.getKey();
            int quantita = entry.getValue();

            for (int i = 0; i < quantita; i++) {
                if (confezione.getCapacita() >= 1) {
                    confezione.aggiungiVino(vino, 1);
                } else {
                    //Confezione piena, aggiungi all'ordine e creane una nuova
                    this.confezioniVini.add(confezione);
                    confezione = new ConfezioneVini();
                    confezione.aggiungiVino(vino, 1);
                }
            }
        }
        if(confezione.getCapacita()<5){
            this.confezioniVini.add(confezione);
        }
    }

    public Date getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
    
}