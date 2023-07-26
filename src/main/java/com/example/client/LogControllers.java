package com.example.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LogControllers {
    @FXML
    private Button loginButton;

    @FXML
    BorderPane mainPaneLog;

    @FXML
    VBox loginVBox;

    @FXML
    VBox registrationVBox;

    @FXML
    protected void onRegistrationButtonClick() {
        System.out.println("Registration");
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("client-registration");
//      RECUPERO IL mainPaneLog
        BorderPane parent = (BorderPane) loginVBox.getParent().getParent();
        parent.setCenter(view);
//      AGGIUSTO LE DIMENSIONI DELLA WINDOW
        parent.getScene().getWindow().sizeToScene();
    }

    @FXML
    protected void onLoginButtonClick() {
        System.out.println("Login");
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("login-form");
        BorderPane parent = (BorderPane) registrationVBox.getParent().getParent();
        parent.setCenter(view);
        parent.getScene().getWindow().sizeToScene();
    }

    @FXML
    protected void aceesso(){
        System.out.println("Login");
        Stage stage = (Stage) loginVBox.getScene().getWindow();
        GetStage obj = new GetStage();
        obj.set(stage);
    }

}