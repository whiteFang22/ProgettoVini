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

    public void ClientModificaCredenziali(String password) {
        // Implementazione del metodo modificaCredenziali
        // Esempio: Modifica la password del cliente
        Request req = new Request();
        req.set(0, password, null);
        client.message( req);
    }

    // Ha come parametro una lista di CassaVino e/o ConfezioneVini
    public Response acquistaBottiglie(List<Vino> bottiglieList) {
        Request req = new Request();
        req.set(0, bottiglieList, null);

        return client.message( req);
    }

    public boolean confermaPagamento(){
        //invia al sistema la conferma dopo aver inserito le coordinarie bancarie
        Request req = new Request();
        req.set(0,true, null);
        Response res = client.message( req);

        return res.isSuccess();
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
        Request req = new Request();
        req.set(0, bottiglieList, null);
        client.message( req);
    }


}
