package com.example.classes;

import com.example.client.SharedData;

import java.util.ArrayList;
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
        Cliente c = new Cliente("pippo","baudo","",username,"","");
        SharedData.getInstance().setUser(c);
        return true;
    }

    public List<Vino> cercaVini(String nome, FiltriRicerca filtri) {
        // Esempio: Esegui una ricerca di vini per nome e anno e restituisci una lista di risultati
        /*Request req = new Request("cercaVini", this.codiceFiscale);
        req.setFiltriRicerca(filtri);
        Response res = connettivity.message(req);
        return res.getVini();*/
        Vino v1 = new Vino("Bordeaux",null,null,2020,null,null,23.65f,0,0, "1");
        Vino v2 = new Vino("Martell Millesime",null,null,1944,null,null,12.6f,0,0, "2");
        Vino v3 = new Vino("Martell Biberon",null,null,1944,null,null,20f,0,0, "3");
        List<Vino> vini = new ArrayList<>();
        vini.add(v1);
        vini.add(v2);
        vini.add(v3);
        return vini;
    }
}
