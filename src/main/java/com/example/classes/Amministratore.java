package com.example.classes;

public class Amministratore extends Impiegato {
    private String indirizzoResidenza;

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

    public void registrazioneImpiegato(Impiegato impiegato, String password) {
        // Implementazione del metodo registrazione
        // Esempio: Registra un nuovo impiegato con la password specificata
    }

    public void preparazioneReport(ReportMensile report) {
        // Implementazione del metodo preparazioneReport
        // Esempio: Prepara un report mensile e lo gestisce
    }

    public void modificaCredenziali(String username, String password, boolean reset) {
        // Implementazione del metodo modificaCredenziali
        // Esempio: Modifica le credenziali di un utente (password e reset)
    }
}
