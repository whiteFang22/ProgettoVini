package com.example.classes;

import java.util.ArrayList;
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
        request.set(0,this,this.AuthCode);
        Response res = client.message(request);
        return res.isSuccess();
    }

    // DA DEFINIRE... CREDO CHE NON DOBBA ESSERE IMPLEMENTATO
    public Vino selezionaVino() {
        // Implementazione del metodo selezionaVino
        // Esempio: Restituisci un oggetto Vino selezionato
        return null; // Modificare per restituire un vino effettivo
    }

    public void ClientModificaCredenziali(String password) {
        // Implementazione del metodo modificaCredenziali
        // Esempio: Modifica la password del cliente
        Request req = new Request();
        req.set(0, password, this.AuthCode);
        client.message(req);
    }

    // Ha come parametro una lista di CassaVino e/o ConfezioneVini
    // SERVER: crea l'ordine di Vendita sulla base di bottiglieList e inseriscilo nel DB. È importante
    //         che ad ogni ordine in DB sia associato il campo "completato" per la fase di confermaPagamento
    //         Se non ci sono abbastanza vini in magazzino crea un oggetto PropostaAcquisto contenente
    //         i vini mancanti con le rispettive quantità, e restituiscila al client
    //         (Se il cliente deciderà di proseguire lo stessi, chiamerà il metodo proponiAcquisto)
    public Response acquistaBottiglie(List<Object> bottiglieList) {
        Request req = new Request();
        req.set(0, bottiglieList, this.AuthCode);

        return client.message( req);
    }

    /* SERVER: recupera l'ultimo ordine di vendita associato al cliente nel DB che avrà
               il campo "completato"==false.
               Se conferma==true allora setta il campo "completo" dell'ordine nel DB a ture
               così come, se presente, per l'ultima proposta di Acquisto con "completato"==false

               Altrimenti elimina l'ordine dal DB in quanto l'utente ha scelto di non
               acquistare le bottiglie ed elimina anche, se presente, l'ultima proposta di Acquisto con
               "completato"==false
     */
    public boolean confermaPagamento(Boolean conferma){
        //invio al sistema la conferma dopo aver inserito le coordinarie bancarie
        Request req = new Request();
        req.set(0,conferma, this.AuthCode);
        Response res = client.message( req);

        return res.isSuccess();
    }

     /*
     SERVER: Se conferma==true viene creato l'ordine di Acquisto che verrà utilizzato nelle fasi successive per il rifornimento del magazzino
             Viene restituito al cliente una copia dell'ordine di Vendita aggiunto in precedenza nel DB

             L'ultimo ordine di vendita presente nel DB associato al cliente viene modificato in modo che
             vengano eliminati da esso i vini presenti nell'ulitma proposta di acquisto effettuata.
             Se conferma==false viene eliminata la proposta di acquisto presente nel DB associata all'utente
             sopra citata.
             Infine invio al cliente il nuovo ordine di vendita modificato, che deciderà in seguito se
             acquistare oppure no in confermaPagamento.
     */
    public Response proponiAcquisto(Boolean conferma) {
        Request req = new Request();
        req.set(0, conferma, this.AuthCode);

        return client.message(req);
    }


}
