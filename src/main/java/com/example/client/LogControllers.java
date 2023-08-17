package com.example.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LogControllers implements Initializable{
    @FXML
    private BorderPane mainPaneLog;
    @FXML
    public ChoiceBox<String> UserChoiceBox;
    @FXML
    private Button registrationButton;

    private String[] userTypes = {"cliente", "amministratore", "impiegato"};


    // called to inizialize a controller after its root elemnt has been processed
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)  {
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("general/login-form");
        mainPaneLog.setCenter(view);
        UserChoiceBox.getItems().addAll(userTypes);
        UserChoiceBox.setValue("cliente");
        SharedData.getInstance().setUserType("cliente");
        UserChoiceBox.setOnAction(this::getChoice);
    }

    public void getChoice(ActionEvent event){
        String choice = UserChoiceBox.getValue();
        //System.out.println(choice);
        SharedData.getInstance().setUserType(choice);
        BorderPane parent = (BorderPane) UserChoiceBox.getParent().getParent();
        registrationButton = (Button) parent.lookup("#registrationButton");
        registrationButton.setDisable(!choice.equals("cliente"));
    }
}