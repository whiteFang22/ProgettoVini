package com.example.classes;

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
    Ha come parametri codiceFiscale dell'impiegato, la nuova password da associare
    a tale impiegato ed il parametro reset. Quest'ultimo se "true" cancella tutti i
    dati dell'impiegato (lo elimina dal sistema/db)
    */
    public void modificaCredenziali(String codiceFiscale, String password, boolean reset) {
        // Esempio: Modifica le credenziali di un utente (password e reset)
        Object[] data = {codiceFiscale, password, reset};
        //client.message("preparazioneReport", data);
    }
}
