package com.example.client;

public class SharedData {
    private static SharedData instance = new SharedData();

    private String sharedValue;

    private SharedData() {
        // Costruttore privato per impedire l'istanziazione diretta
    }

    public static SharedData getInstance() {
        return instance;
    }

    public String getSharedValue() {
        return sharedValue;
    }

    public void setSharedValue(String value) {
        sharedValue = value;
    }
}

