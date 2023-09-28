package com.example.classes;

public class Amministratore extends Impiegato {
    private String indirizzoResidenza;

    final Connettivity connettivity = new Connettivity("localhost", 12345);


    public Amministratore(String nome, String cognome, String codiceFiscale, String email, String numeroTelefonico,
                          String indirizzoResidenza) {
        super(nome, cognome, codiceFiscale, email, numeroTelefonico, indirizzoResidenza);
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
        Request req = new Request("registrazioneImpiegato", this.getCodiceFiscale());
        req.setPassword(password);
        connettivity.message(req);
    }

    // Salva nel db il report passato
    public void preparazioneReport(ReportMensile report) {
        // Esempio: Prepara un report mensile e lo gestisce
        Object[] data = {report};
        Request req = new Request("preparazioneReport", this.getCodiceFiscale());
        req.setReport(report);
        connettivity.message(req);
    }

    /*
    Ha come parametri codiceFiscale dell'impiegato, la nuova password da associare
    a tale impiegato ed il parametro reset. Quest'ultimo se "true" cancella tutti i
    dati dell'impiegato (lo elimina dal sistema/db)
    */
    public void modificaCredenziali(String codiceFiscale, String password, boolean reset) {
        // Esempio: Modifica le credenziali di un utente (password e reset)
        Request req = new Request("modificaCredenziali", codiceFiscale);
        req.setPassword(password);
        req.setReset(reset);
        connettivity.message(req);
    }
}
