package com.example.classes;

import java.util.List;

public class Cliente extends UtenteGenerico {
    private String indirizzoDiConsegna;

    public Cliente(
        String passwordtohash,
        String nome, 
        String cognome, 
        String codiceFiscale, 
        String email, 
        String numeroTelefonico,
        String indirizzoDiConsegna) {
        super(nome, cognome, passwordtohash, codiceFiscale, email, numeroTelefonico);
        this.indirizzoDiConsegna = indirizzoDiConsegna;
    }

    public String getIndirizzoDiConsegna() {
        return indirizzoDiConsegna;
    }

    public void setIndirizzoDiConsegna(String indirizzoDiConsegna) {
        this.indirizzoDiConsegna = indirizzoDiConsegna;
    }

    public boolean registrazione() {
        //Request id for registration -> 0
       //hashing handled by setter method  !!may be unsafe!!
        Request request = new Request();
        request.set(0,this,null);
        Response res = client.message(request);
        return res.isSuccess();
    }

    // DA DEFINIRE... non credo venga coinvolto il server
    public Vino selezionaVino() {
        // Implementazione del metodo selezionaVino
        // Esempio: Restituisci un oggetto Vino selezionato
        return null; // Modificare per restituire un vino effettivo
    }

    public void modificaCredenziali(String password) {
        // Implementazione del metodo modificaCredenziali
        // Esempio: Modifica la password del cliente
        Object[] data = {this.getCodiceFiscale(), password};
        //client.message("modificaCredenziali", data);
    }

    // Ha come parametro una lista di CassaVino e/o ConfezioneVini
    public <T> void acquistaBottiglie(List<T> bottiglieList) {
        Object[] data = {this.getCodiceFiscale(), bottiglieList};
        //client.message("acquistaBottiglie", data);
    }

     /*
     Bisogna scoprire quali bottiglie mancano e acquistarle nel giusto numero per
     soddisfare la richiesta del cliente. Due possibili implementazioni
     1) Lato server: riceve l'ordine del cliente e fa le operazioni descritte sopra e poi avvia
        la porposta di acquisto. In questo caso il server riceverà una lista di oggetti
        CassaVino e/o ConfezioneVini
     2) Lato client: identifico le bottiglie mancanti e in quali quantità e le mando al server
        sotto forma di Map<Vino, quantità(int)> che avvierà direttamente la proposta di acquisto acquisterà
     */
    public <T> void proponiAcquisto(T bottiglie) {
        if (bottiglie instanceof ConfezioneVini confezioneVini) {
            // Esempio: Proponi un acquisto al sistema o all'amministratore per la confezioneVini
            Object[] data = {this.getCodiceFiscale(), confezioneVini};
            //client.message("proponiAcquisto", data);
        }
        else if (bottiglie instanceof CassaVino cassaVino) {
            // Esempio: Proponi un acquisto al sistema o all'amministratore per la cassaVino
            Object[] data = {this.getCodiceFiscale(), cassaVino};
            //client.message("proponiAcquisto", data);
        }
    }
    public <T> void proponiAcquisto(List<T> bottiglieList) {
        Object[] data = {this.getCodiceFiscale(), bottiglieList};
        //client.message("proponiAcquisto", data);
    }


}
