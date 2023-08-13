package com.example.classes;

import java.util.Date;
import java.util.List;

public class Impiegato extends UtenteGenerico {
    private String indirizzoResidenza;

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

    public List<OrdineVendita> ricercaOrdiniVendita(Date data1, Date data2) {
        // Implementazione del metodo ricercaOrdiniVendita
        // Esempio: Esegui una ricerca degli ordini di vendita tra le date specificate
        return null; // Modificare per restituire la lista effettiva
    }

    public List<OrdineAcquisto> ricercaOrdiniAcquisto(Date data1, Date data2) {
        // Implementazione del metodo ricercaOrdiniAcquisto
        // Esempio: Esegui una ricerca degli ordini di acquisto tra le date specificate
        return null; // Modificare per restituire la lista effettiva
    }

    public List<PropostaAcquisto> ricercaProposteAcquisto(Date data1, Date data2) {
        // Implementazione del metodo ricercaProposteAcquisto
        // Esempio: Esegui una ricerca delle proposte di acquisto tra le date specificate
        return null; // Modificare per restituire la lista effettiva
    }

    public void gestioneOrdineVendita(OrdineVendita ordine) {
        // Implementazione del metodo gestioneOrdineVendita
        // Esempio: Gestisci l'ordine di vendita (approva, rifiuta, etc.)
    }

    public void gestionePropostaAcquisto(PropostaAcquisto proposta) {
        // Implementazione del metodo gestionePropostaAcquisto
        // Esempio: Gestisci la proposta di acquisto (accetta, rifiuta, etc.)
    }

    public void gestioneOrdineAcquisto(PropostaAcquisto ordine) {
        // Implementazione del metodo gestioneOrdineAcquisto
        // Esempio: Gestisci l'ordine di acquisto (processa, verifica, etc.)
    }
}

