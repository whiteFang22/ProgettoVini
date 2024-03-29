package com.example.client;

import com.example.classes.Response;
import com.example.classes.UtenteGenerico;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedData {
    private static SharedData instance = new SharedData();
    private UtenteGenerico user = new UtenteGenerico("", "", "", "", "", "",true);
    private List<Object> contenitoriSelezionati = new ArrayList<>();
    private Response res;
    private String userType;
    private BorderPane currentParent;
    private Map<Integer, Integer> viniSelezionati = new HashMap<>();

    private SharedData() {
        // Costruttore privato per impedire l'istanziazione diretta
    }

    public static SharedData getInstance() {
        return instance;
    }
    public void resetInstance(boolean resetClient){
        List<Object> newCont = new ArrayList<>();
        setContenitori(newCont);
        viniSelezionati = new HashMap<>();
        if (resetClient) user = new UtenteGenerico("", "", "", "", "", "",true);
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
    public List<Object> getContenitori(){ return contenitoriSelezionati; } // contiene casse e confezioni

    public void setViniSelezionati(int id, int qnt){
        if (viniSelezionati.get(id)!=null) viniSelezionati.compute(id, (key, value) -> value+=qnt);
        else viniSelezionati.put(id,qnt);
    }
    public Map<Integer, Integer> getVini(){ return viniSelezionati; }

    public void setRes(Response res){ this.res = res;}
    public Response getRes(){ return res;}

    public void setCurrentParent(BorderPane currentParent) {
        this.currentParent = currentParent;
    }

    public BorderPane getCurrentParent() {
        return currentParent;
    }
}

