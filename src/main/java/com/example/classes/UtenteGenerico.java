package com.example.classes;

import java.util.ArrayList;
import java.util.List;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UtenteGenerico implements Serializable{
    private static final long serialVersionUID = 1L;
    private String passwordhash = "0000";
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String email;
    private String numeroTelefonico;
    protected String AuthCode;        //given from server when logged

    protected transient static Client client;

    public UtenteGenerico(String nome, String cognome,String passwordtohash, String codiceFiscale, String email, String numeroTelefonico, boolean isClient) {
        if(passwordtohash != null){
            setpasswordhash(passwordtohash);
        }
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.email = email;
        this.numeroTelefonico = numeroTelefonico;

        if(isClient){
            client = new Client("localhost", 4444);
        }
    }
    public void setAuthCode(String inAuthCode){
        this.AuthCode = inAuthCode;
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

    //deve ritornarmi Response contenente l'utente
    //F: Fatto, in res.getData()
    
    public Response login() {
        // Implementazione del metodo login
        // Esempio: Verifica delle credenziali e autenticazione
        Request request = new Request();
        int id = 1;
        request.setId(id);
        request.setData(this);
        Response res = client.message(request);
        //response unpack
        if(res.getId() == 1){
            setAuthCode(res.getAuthCode());
            System.out.println(res.getAuthCode());
        }
        else{
            //throw new UnknownError("Server error");
        }
        return res;
    }

    public List<Vino> cercaVini(FiltriRicerca filtri) {
        // Implementazione del metodo cercaVini
        // Esempio: Esegui una ricerca di vini per nome e anno e restituisci una lista di risultati
        Request request = new Request();
        request.set(9,filtri,this.AuthCode);

        Response res = client.message(request);
        //response unpack
        // Create a list to hold the data
        List<Vino> wineList = new ArrayList<>();
        if(res.getId() == 1 && res.getAuthCode() == this.AuthCode){     //check response id and double check AuthCode
            if (res.getData() != null){
               wineList = (List<Vino>) res.getData();
        }
        }
        else{
            throw new UnknownError("Server error");
        }
        //Debug Only
        for (Vino element : wineList) {
            System.out.println(element.getNome());
            System.out.println(element.getAnno());
        }
        return wineList;
    }

    public String getPasswordhash() {
        return passwordhash;
    }
    /*
     * Sets Password to sha256 hash of input password;
     * 
     * 
     */
    public void setpasswordhash(String passwordtohash){
        //password hashing
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Convert the password string into bytes
            byte[] passwordBytes = passwordtohash.getBytes();
            
            // Update the digest with the password bytes
            byte[] hashedBytes = md.digest(passwordBytes);
            
            // Convert the hashed bytes to a hexadecimal representation
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            
            this.passwordhash = sb.toString(); // Return the hashed password as a hexadecimal string
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception, e.g., by logging or throwing a custom exception
            e.printStackTrace();
            this.passwordhash = null; // Return null in case of an error
        }
    }
}
