package com.example.client;

import com.example.classes.UtenteGenerico;

import java.util.ArrayList;
import java.util.List;

public class SharedData {
    private static SharedData instance = new SharedData();
    private UtenteGenerico user = new UtenteGenerico("","", "", "", "");
    private static List<Object> contenitoriSelezionati = new ArrayList<>();
    private String userType;

    private SharedData() {
        // Costruttore privato per impedire l'istanziazione diretta
    }

    public static SharedData getInstance() {
        return instance;
    }
    public void resetInstance(){
        List<Object> newCont = new ArrayList<>();
        setContenitori(newCont);
    }

    public String getUserType() {
        return userType;
    }
    public void setUserType(String value) {
        userType = value;
    }

    public UtenteGenerico getUser() { return user; }
    public void setUser(UtenteGenerico user) { this.user = user; }

    public void setContenitori(List<Object> contenitori){ this.contenitoriSelezionati = contenitori; }
    public List<Object> getContenitori(){ return contenitoriSelezionati; }
}

