package com.example.classes;

import java.util.List;

public class Response {
    private List<Vino> vini;
    private String messaggio;
    private List<OrdineVendita> ordiniVendita;
    private List<OrdineAcquisto> ordiniAcquisto;
    private List<PropostaAcquisto> proposteAcquisto;

    public Response(List<Vino> vini, String messaggio, List<OrdineVendita> ordiniVendita,
                    List<OrdineAcquisto> ordiniAcquisto, List<PropostaAcquisto> proposteAcquisto) {
        this.vini = vini;
        this.messaggio = messaggio;
        this.ordiniVendita = ordiniVendita;
        this.ordiniAcquisto = ordiniAcquisto;
        this.proposteAcquisto = proposteAcquisto;
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
}
