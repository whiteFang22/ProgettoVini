package com.example.classes;

import java.util.List;

public class Amministratore extends Impiegato {
    public Amministratore(String passwordtohash, String nome, String cognome, String codiceFiscale, String email, String numeroTelefonico,
                          String indirizzoResidenza, Boolean isClient) {
        super(passwordtohash, nome, cognome, codiceFiscale, email, numeroTelefonico, indirizzoResidenza,isClient);
    }

    // Salva nel db l'impiegato passato
    public boolean registrazioneImpiegato(Impiegato impiegato, String password) {
        // Esempio: Registra un nuovo impiegato con la password specificata
        Request req = new Request();
        impiegato.setpasswordhash(password);
        req.set(20,impiegato, this.AuthCode);
        Response res = client.message(req);
        return res.isSuccess();
    }

    // TODO: Salva nel db il report passato
    //S: è solo da salvare cosi? o va gestito? se va gestito cosa ci devo fare
    public boolean preparazioneReport(ReportMensile report) {
        Request req = new Request();
        req.set(0,report,this.AuthCode);

        Response res = client.message(req);
        return res.isSuccess();
    }
    public List<String> ricercaImpiegati(){
        Request req = new Request();
        req.set(0,null,this.AuthCode);

        Response res = client.message(req);

        return (List<String>) res.getData();
    }

    /*
    Ha come parametri email dell'impiegato, la nuova password da associare
    a tale impiegato ed il parametro delete. Quest'ultimo se "true" cancella tutti i
    dati dell'impiegato (lo elimina dal sistema/db)
    */
    public boolean AdminModificaCredenziali(String email, String password, boolean delete) {
        // Esempio: Modifica le credenziali di un utente (password e reset)
        Request req = new Request();
        UtenteGenerico user = new UtenteGenerico(null, null, password, null, email, null,false);
        if(delete){
            req.set(21, user, null);
        }
        else{
            req.set(22, user, null);
        }

        Response res = client.message(req);
        return res.isSuccess();
    }
}
