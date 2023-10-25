package com.example.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GetStage {
    public void set(Stage stage, String userType){
        String path1="";
        String parentPath="";
        String path2="";
        System.out.println("value: "+ userType);
        switch (userType){
            case "cliente":
                path1 = "cliente/client-initial-page";
                parentPath = "#mainPaneInitialClient";
                path2 = "cliente/scheda-vini-client";
                break;
            case "impiegato":
                path1 = "impiegato/emp-initial-page";
                parentPath = "#mainBorderImpiegati";
                path2 = "impiegato/center-clienti-registrati";
                break;
            case "amministratore":
                path1 = "amministratore/adm-initial-page";
                parentPath = "#mainBorderImpiegati";
                path2 = "impiegato/center-clienti-registrati";
                break;
        }

        FxmlLoader object = new FxmlLoader();
        Pane root = object.getPage(path1);
        BorderPane bp = (BorderPane) root.lookup(parentPath);

        Pane view = object.getPage(path2);
        bp.setCenter(view);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(757);
    }
}
