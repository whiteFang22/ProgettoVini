package com.example.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("log.fxml"));
        AnchorPane root = (AnchorPane) fxmlLoader.load();

        BorderPane bp = (BorderPane) root.lookup("#mainPaneLog");
        fxmlLoader = new FXMLLoader(MainApplication.class.getResource("client-registration.fxml"));
        Pane view = (Pane) fxmlLoader.load();
        bp.setCenter(view);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}