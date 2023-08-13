package com.example.classes;

import java.util.List;

public class Cliente extends UtenteGenerico {
    private String indirizzoDiConsegna;

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

    public void registrazione(Cliente cliente) {
        // Implementazione del metodo registrazione
        // Esempio: Aggiungi il cliente al sistema o al database
    }

    public Vino selezionaVino() {
        // Implementazione del metodo selezionaVino
        // Esempio: Restituisci un oggetto Vino selezionato
        return null; // Modificare per restituire un vino effettivo
    }

    public void modificaCredenziali(String password) {
        // Implementazione del metodo modificaCredenziali
        // Esempio: Modifica la password del cliente
    }

    public void acquistaBottiglie(ConfezioneVini bottiglie) {
        // Implementazione del metodo acquistaBottiglie per ConfezioneVini
        // Esempio: Esegui l'acquisto di bottiglie di vino in una confezione
    }

    public void acquistaBottiglie(CassaVino bottiglie) {
        // Implementazione del metodo acquistaBottiglie per CassaVino
        // Esempio: Esegui l'acquisto di bottiglie di vino in una cassa
    }

    public void proponiAcquisto(ConfezioneVini bottiglie) {
        // Implementazione del metodo proponiAcquisto per ConfezioneVini
        // Esempio: Proponi un acquisto al sistema o all'amministratore
    }

    public void proponiAcquisto(CassaVino bottiglie) {
        // Implementazione del metodo proponiAcquisto per CassaVino
        // Esempio: Proponi un acquisto al sistema o all'amministratore
    }
}
