package com.example.classes;

import java.util.ArrayList;
import java.util.List;

public class Amministratore extends Impiegato {
    private String indirizzoResidenza;


    public Amministratore(String username, String passwordtohash, String nome, String cognome, String codiceFiscale, String email, String numeroTelefonico,
                          String indirizzoResidenza) {
        super(username, passwordtohash, nome, cognome, codiceFiscale, email, numeroTelefonico, indirizzoResidenza);
    }

    public String getIndirizzoResidenza() {
        return indirizzoResidenza;
    }

    public void setIndirizzoResidenza(String indirizzoResidenza) {
        this.indirizzoResidenza = indirizzoResidenza;
    }

    // Salva nel db l'impiegato passato
    public void registrazioneImpiegato(Impiegato impiegato, String password) {
        // Esempio: Registra un nuovo impiegato con la password specificata
        Object[] data = {impiegato, password};
        //client.message("registrazioneImpiegato", data);
    }

    // Salva nel db il report passato
    public void preparazioneReport(ReportMensile report) {
        // Esempio: Prepara un report mensile e lo gestisce
        Object[] data = {report};
        //client.message("preparazioneReport", data);
    }

    /*
    Ha come parametri email dell'impiegato, la nuova password da associare
    a tale impiegato ed il parametro reset. Quest'ultimo se "true" cancella tutti i
    dati dell'impiegato (lo elimina dal sistema/db)
    */
    public boolean AdminModificaCredenziali(String email, String password, boolean reset) {
        // Esempio: Modifica le credenziali di un utente (password e reset)
        Request req = new Request();

        ArrayList<Object> data = new ArrayList<>();
        data.add(email);
        data.add(password);
        data.add(reset);
        req.set(0, data, null);

        Response res = client.message(req);
        return res.isSuccess();
    }
}
