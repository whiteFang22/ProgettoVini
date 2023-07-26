package com.example.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GetStage {

    public void set(Stage stage){
        FxmlLoader object = new FxmlLoader();
        Pane root = object.getPage("client-initial-page");
        BorderPane bp = (BorderPane) root.lookup("#mainPaneInitialClient");

        Pane view = object.getPage("scheda-vini-client");
        bp.setCenter(view);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(757);

    }
}
