package com.example.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.net.URL;

public class FxmlLoader {
    private Pane view;

    public Pane getPage(String fileName) {
        try {
            URL fileURL = MainApplication.class.getResource(fileName +".fxml");
            if (fileURL == null) {
                throw new java.io.FileNotFoundException("FXML file can't be found");
            }
            new FXMLLoader();
            view = FXMLLoader.load(fileURL);
        } catch (Exception e) {
            System.out.println("No page " + fileName + " please check FxmlLoader ");
        }
        return view;
    }
}
