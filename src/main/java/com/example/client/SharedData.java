package com.example.client;

import com.example.classes.UtenteGenerico;

public class SharedData {
    private static SharedData instance = new SharedData();
    private UtenteGenerico user = new UtenteGenerico("","", "", "", "");

    private String userType;

    private SharedData() {
        // Costruttore privato per impedire l'istanziazione diretta
    }

    public static SharedData getInstance() {
        return instance;
    }

    public String getUserType() {
        return userType;
    }
    public void setUserType(String value) {
        userType = value;
    }

    public UtenteGenerico getUser() { return user; }
    public void setUser(UtenteGenerico user) { this.user = user; }
}

