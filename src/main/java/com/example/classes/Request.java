package com.example.classes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private List<Vino> vini;
    private String messaggio;
    private List<OrdineVendita> ordiniVendita;
    private List<OrdineAcquisto> ordiniAcquisto;
    private List<PropostaAcquisto> proposteAcquisto;
    private String codiceFiscale;
    private String password;
    private boolean conferma;
    private boolean reset;
    private FiltriRicerca filtriRicerca = new FiltriRicerca(null,null, null, null);
    private ReportMensile report;

    public Request(String messaggio, String codiceFiscale) {
        this.messaggio = messaggio;
        this.codiceFiscale = codiceFiscale;
    }

    public List<Vino> getVini(){ return vini; }

    public void setVini(List<Vino> newList) { vini = newList; }

    public  String getMessaggio(){ return messaggio;}
    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public List<OrdineVendita> getOrdiniVendita() {
        return ordiniVendita;
    }

    public void setOrdiniVendita(List<OrdineVendita> ordiniVendita) {
        this.ordiniVendita = ordiniVendita;
    }

    public List<OrdineAcquisto> getOrdiniAcquisto() {
        return ordiniAcquisto;
    }

    public void setOrdiniAcquisto(List<OrdineAcquisto> ordiniAcquisto) {
        this.ordiniAcquisto = ordiniAcquisto;
    }

    public List<PropostaAcquisto> getProposteAcquisto() {
        return proposteAcquisto;
    }

    public void setProposteAcquisto(List<PropostaAcquisto> proposteAcquisto) {
        this.proposteAcquisto = proposteAcquisto;
    }

    public String getUtente() { return codiceFiscale; }

    public void setUtente(String codiceFiscale) { this.codiceFiscale = codiceFiscale; }

    public String getPassword() { return password; }

    // Setter per impostare la password
    public void setPassword(String newPassword) { this.password = newPassword; }

    public void setConferma(boolean conferma) {
        this.conferma = conferma;
    }
    public boolean getConferma() {
        return conferma;
    }

    public void setFiltriRicerca(FiltriRicerca filtri) {
        this.filtriRicerca = filtri;
    }

    public FiltriRicerca getFiltriRicerca() {
        return filtriRicerca;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    public boolean getReset() {
        return reset;
    }

    public void setReport(ReportMensile report) {
        this.report = report;
    }

    public ReportMensile getReport() {
        return report;
    }
}
