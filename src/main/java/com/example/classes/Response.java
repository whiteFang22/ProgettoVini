package com.example.classes;

import java.util.List;

public class Response {
    private List<Vino> vini;
    private String messaggio;
    private boolean success;
    private List<OrdineVendita> ordiniVendita;
    private List<OrdineAcquisto> ordiniAcquisto;
    private List<PropostaAcquisto> proposteAcquisto;
    private UtenteGenerico utente;
    private OrdineVendita ordineVendita;

    public Response(String messaggio, UtenteGenerico utente) {
        this.messaggio = messaggio;
        this.utente = utente;
    }

    public List<Vino> getVini() {
        return vini;
    }

    public void setVini(List<Vino> vini) {
        this.vini = vini;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<OrdineVendita> getOrdiniVenditaCliente() {
        return ordiniVendita;
    }

    public void setOrdiniVendita(List<OrdineVendita> ordiniVendita) {
        this.ordiniVendita = ordiniVendita;
    }

    public List<OrdineAcquisto> getOrdiniAcquistoCliente() {
        return ordiniAcquisto;
    }

    public void setOrdiniAcquistoCliente(List<OrdineAcquisto> ordiniAcquisto) {
        this.ordiniAcquisto = ordiniAcquisto;
    }

    public List<PropostaAcquisto> getProposteAcquistoCliente() {
        return proposteAcquisto;
    }

    public void setProposteAcquistoCliente(List<PropostaAcquisto> proposteAcquisto) {
        this.proposteAcquisto = proposteAcquisto;
    }

    public UtenteGenerico getUtente() { return utente; }

    public void setUtente(UtenteGenerico utente) { this.utente = utente; }

    public OrdineVendita getOrdineVendita() {
        return ordineVendita;
    }

    public void setOrdineVendita(OrdineVendita ordineVendita) {
        this.ordineVendita = ordineVendita;
    }
}
