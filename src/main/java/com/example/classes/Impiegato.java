package com.example.classes;

import java.util.Date;
import java.util.List;

public class Impiegato extends UtenteGenerico {
    private String indirizzoResidenza;

    public Impiegato(String passwordtohash, String nome, String cognome, String codiceFiscale, String email,
                     String numeroTelefonico, String indirizzoResidenza,Boolean isClient) {
        super(nome, cognome, passwordtohash, codiceFiscale, email, numeroTelefonico, isClient);
        this.indirizzoResidenza = indirizzoResidenza;
    }

    public String getIndirizzoResidenza() {
        return indirizzoResidenza;
    }

    public void setIndirizzoResidenza(String indirizzoResidenza) {
        this.indirizzoResidenza = indirizzoResidenza;
    }

    public List<Cliente> ricercaClienti(String cognome){
        // restituisce i clienti nel DB con il cognome indicato
        // se cognome=="" allora restituisci tutti i clienti nel db
        Request req = new Request();
        req.set(10,cognome,this.AuthCode);

        Response res = client.message(req);
        //Server returns a List<Cliente>, if there is no cliente found returns an empty list
        return (List<Cliente>) res.getData();
    }

    // estrai dal metodo message il risultato dell'operazione
    public List<OrdineVendita> ricercaOrdiniVendita(Date dete1, Date dete2) {
        // Esempio: Esegui una ricerca degli ordini di vendita (CREATI?) tra le date specificate
        FiltriRicerca data = new FiltriRicerca(dete1, dete2,null,0);
        Request req = new Request();
        req.set(11, data, this.AuthCode);

        Response res = client.message(req);
        return (List<OrdineVendita>) res.getData();
    }

    public List<OrdineAcquisto> ricercaOrdiniAcquisto(Date dete1, Date dete2) {
        // Esempio: Esegui una ricerca degli ordini di acquisto tra le date specificate
        FiltriRicerca data = new FiltriRicerca(dete1, dete2, null, 0);
        Request req = new Request();
        req.set(12, data, this.AuthCode);

        Response res = client.message(req);
        return (List<OrdineAcquisto>) res.getData();
    }
    public List<PropostaAcquisto> ricercaProposteAcquisto(Date dete1, Date dete2) {
        // Esempio: Esegui una ricerca delle proposte di acquisto tra le date specificate
        FiltriRicerca data = new FiltriRicerca(dete1, dete2, null, null);
        Request req = new Request();
        req.set(15, data, this.AuthCode);

        Response res = client.message(req);
        //res = client.message("ricercaProposteAcquisto", data);
        return (List<PropostaAcquisto>) res.getData();
    }

    /*
        Quando viene generato un ordine di vendita dopo l’acqiusto da parte di un client,
        il campo dataConsegna non verrà generato immediatamente dal server: a ciò provvederà
        questo metodo.
        Il parametro passato conterrà l’ordine di vendita precedentemente creato contenente
        la data di consegna. Il server dovrà aggiornare tale informazione nel db e impostare
        il campo "firmato" a true.
    */

    /*
     * Recupera un ordine vendita dalla queue, se non è presente attende per il tempo specificato poi ritorna
     * 
     * @return OrdineVendita
     */
    public OrdineVendita recuperaOrdineVendita(int time){
        Request req = new Request();
        req.set(34, time, this.AuthCode);

        Response res = client.message(req);
        return (OrdineVendita) res.getData();

    }
    public boolean gestioneOrdineVendita(OrdineVendita ordine) {
        Request req = new Request();
        req.set(14,ordine,this.AuthCode);

        Response res = client.message(req);
        return res.isSuccess();
    }

    // INUTILE
    public void gestionePropostaAcquisto(PropostaAcquisto proposta) {
        // Esempio: Gestisci la proposta di acquisto (accetta, rifiuta, etc.)
    }

    /*
        Dopo ogni ordine di vendita il server controlla se il numero di bottiglie
        dei vini è sceso sotto soglia.
        Se ciò avviene, il sistema informa uno dei dipendenti sulle quantità disponibili
        di questi vini. Ricevuta l’informazione, l’impiegato preparerà un ordine di acquisto
        utilizzando questo metodo.
        BISOGNA QUINDI GESTIRE COME MANDARE QUESTI MESSAGGI AGLI IMPIEGATI, vedi ultimo paragrafo di "Online Wine Shop".pdf
        L'ordine di acquisto sarà inserito nel db con il campo "completato"==false
    */
    public boolean preparaOrdineAcquisto(OrdineAcquisto ordine){
        Request req = new Request();
        // in ordine è importante il campo firmato da checkkare lato server
        req.set(0,ordine,this.AuthCode);

        Response res = client.message(req);
        return res.isSuccess();
    }

    /*
        Con questa funzione l'impiegato completa l'ordine di acquisto
        confermando che i vini sono arrivati dal fornitore.
        A questo punto il server può mettere a true il campo
        "firmato" e, solo adesso, aggiornare le disponiblità dei vini nel DB

        Per identificare gli ordini
    */
    public boolean gestioneOrdineAcquisto(Boolean conferma) {
        Request req = new Request();
        req.set(13,conferma,this.AuthCode);

        Response res = client.message(req);
        return res.isSuccess();
    }
}

