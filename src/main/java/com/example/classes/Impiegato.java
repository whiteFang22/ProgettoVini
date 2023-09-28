package com.example.classes;

import java.util.Date;
import java.util.List;

public class Impiegato extends UtenteGenerico {
    private String indirizzoResidenza;

    final Connettivity connettivity = new Connettivity("localhost", 12345);

    public Impiegato(String nome, String cognome, String codiceFiscale, String email, String numeroTelefonico,
                     String indirizzoResidenza) {
        super(nome, cognome, codiceFiscale, email, numeroTelefonico);
        this.indirizzoResidenza = indirizzoResidenza;
    }

    public String getIndirizzoResidenza() {
        return indirizzoResidenza;
    }

    public void setIndirizzoResidenza(String indirizzoResidenza) {
        this.indirizzoResidenza = indirizzoResidenza;
    }

    // estrai dal metodo message il risultato dell'operazione
    public List<OrdineVendita> ricercaOrdiniVendita(Date data1, Date data2) {
        // Esempio: Esegui una ricerca degli ordini di vendita tra le date specificate
        Request req = new Request("ricercaOrdiniVendita", this.getCodiceFiscale());
        FiltriRicerca filtri = new FiltriRicerca(data1, data2, null, null);
        req.setFiltriRicerca(filtri);
        Response res = connettivity.message( req);
        return res.getOrdiniVenditaCliente();
    }

    public List<OrdineAcquisto> ricercaOrdiniAcquisto(Date data1, Date data2) {
        // Esempio: Esegui una ricerca degli ordini di acquisto tra le date specificate
        Request req = new Request("ricercaOrdiniVendita", this.getCodiceFiscale());
        FiltriRicerca filtri = new FiltriRicerca(data1, data2, null, null);
        req.setFiltriRicerca(filtri);
        Response res = connettivity.message( req);
        return res.getOrdiniAcquistoCliente();
    }

    public List<PropostaAcquisto> ricercaProposteAcquisto(Date data1, Date data2) {
        // Esempio: Esegui una ricerca delle proposte di acquisto tra le date specificate
        Request req = new Request("ricercaOrdiniVendita", this.getCodiceFiscale());
        FiltriRicerca filtri = new FiltriRicerca(data1, data2, null, null);
        req.setFiltriRicerca(filtri);
        Response res = connettivity.message(req);
        return res.getProposteAcquistoCliente();
    }

    // DA DEFINIRE...
    public void gestioneOrdineVendita(OrdineVendita ordine) {
        // Esempio: Gestisci l'ordine di vendita (approva, rifiuta, etc.)
    }

    public void gestionePropostaAcquisto(PropostaAcquisto proposta) {
        // Esempio: Gestisci la proposta di acquisto (accetta, rifiuta, etc.)
    }

    public void gestioneOrdineAcquisto(PropostaAcquisto ordine) {
        // Esempio: Gestisci l'ordine di acquisto (processa, verifica, etc.)
    }
}

