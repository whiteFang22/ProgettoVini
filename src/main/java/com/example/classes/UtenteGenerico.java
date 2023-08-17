package com.example.classes;

import com.example.client.SharedData;

import java.util.List;

public class UtenteGenerico {
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String email;
    private String numeroTelefonico;
    final Connettivity connettivity = new Connettivity("localhost", 12345);

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

    /*
        Il server dovrà verificare che l'utente esista e abbia inserito la password corretta; infine mi
        resituisce un oggetto utente (cliente|amministratore|impiegatp).
        Setta l'oggetto utente associato nell'oggetto della classe SharedData e restituisce
        un boolean per specificare se il login è andato a buon fine o meno
    */
    public boolean login(String username, String password) {
        // Esempio: Verifica delle credenziali e autenticazione
//        Object[] data = {username, password};
//        Response res = connettivity.message("cercaVini", data);
//        if (res.getSuccess()) SharedData.getInstance().setUser(res.getUtente());
//        return res.getSuccess();
        Cliente c = new Cliente("pippo","baudo","","","","");
        SharedData.getInstance().setUser(c);
        return true;
    }

    public List<Vino> cercaVini(String nome, int anno) {
        // Implementazione del metodo cercaVini
        // Esempio: Esegui una ricerca di vini per nome e anno e restituisci una lista di risultati
        Object[] data = {nome, anno};
        Response res = connettivity.message("cercaVini", data);
        return res.getVini(); // Modificare per restituire la lista effettiva
    }
}
