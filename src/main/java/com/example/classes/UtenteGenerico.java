package com.example.classes;

import java.util.List;

public class UtenteGenerico {
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String email;
    private String numeroTelefonico;

    public UtenteGenerico(String nome, String cognome, String codiceFiscale, String email, String numeroTelefonico) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.email = email;
        this.numeroTelefonico = numeroTelefonico;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroTelefonico() {
        return numeroTelefonico;
    }

    public void setNumeroTelefonico(String numeroTelefonico) {
        this.numeroTelefonico = numeroTelefonico;
    }

    public void login(String username, String password) {
        // Implementazione del metodo login
        // Esempio: Verifica delle credenziali e autenticazione
    }

    public List<Vino> cercaVini(String nome, int anno) {
        // Implementazione del metodo cercaVini
        // Esempio: Esegui una ricerca di vini per nome e anno e restituisci una lista di risultati
        return null; // Modificare per restituire la lista effettiva
    }
}
