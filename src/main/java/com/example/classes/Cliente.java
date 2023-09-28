package com.example.classes;

import java.util.List;

public class Cliente extends UtenteGenerico {
    private String indirizzoDiConsegna;

    final Connettivity connettivity = new Connettivity("localhost", 12345);

    public Cliente(String nome, String cognome, String codiceFiscale, String email, String numeroTelefonico,
                   String indirizzoDiConsegna) {
        super(nome, cognome, codiceFiscale, email, numeroTelefonico);
        this.indirizzoDiConsegna = indirizzoDiConsegna;
    }

    public String getIndirizzoDiConsegna() {
        return indirizzoDiConsegna;
    }

    public void setIndirizzoDiConsegna(String indirizzoDiConsegna) {
        this.indirizzoDiConsegna = indirizzoDiConsegna;
    }

    public boolean registrazione(String password) {
        // Esempio: Aggiungi il cliente al sistema o al database
//        Object[] data = {this, password};
//        Response res = connettivity.message("registrazione", data);
//        return res.getSuccess();
        return false;
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
        Request req = new Request("acquista", this.getCodiceFiscale());
        req.setPassword(password);
        connettivity.message( req);
    }

    // Ha come parametro una lista di CassaVino e/o ConfezioneVini
    public Response acquistaBottiglie(List<Vino> bottiglieList) {
        Request req = new Request("acquista", this.getCodiceFiscale());
        req.setVini(bottiglieList);
        Response res = connettivity.message( req);

        return res;
    }

    public boolean confermaPagamento(){
        //invia al sistema la conferma dopo aver inserito le coordinarie bancarie
        Request req = new Request("confermaPagamento", this.getCodiceFiscale());
        req.setConferma(true);
        Response res = connettivity.message( req);

        return res.getSuccess();
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
    public void proponiAcquisto(List<Vino> bottiglieList) {
        Request req = new Request("proponiAcquisto", this.getCodiceFiscale());
        req.setVini(bottiglieList);
        connettivity.message( req);
    }


}
