package com.example.client;

import com.example.classes.UtenteGenerico;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LogFormControllers {
    @FXML
    private BorderPane mainPaneLog;
    @FXML
    private VBox loginVBox;
    @FXML
    private VBox registrationVBox;
    @FXML
    private Label loginMessageLable;

    @FXML
    protected void onRegistrationButtonClick() throws IOException {
        System.out.println("Registration");
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("cliente/client-registration");
//      RECUPERO IL mainPaneLog
        BorderPane parent = (BorderPane) loginVBox.getParent();
        parent.setCenter(view);
//      AGGIUSTO LE DIMENSIONI DELLA WINDOW
        parent.getScene().getWindow().sizeToScene();
    }

    @FXML
    protected void onLoginButtonClick() {
        System.out.println("Login-form");
        FxmlLoader object = new FxmlLoader();
        Pane view = object.getPage("general/login-form");
        BorderPane parent = (BorderPane) registrationVBox.getParent().getParent();
        parent.setCenter(view);
        parent.getScene().getWindow().sizeToScene();
    }

    /*
        controllo il tipo di utente selezionato col menù per il login
        estrapolo i dati dal form
        chiamo il metodo di login dell'utente specifico
     */
    @FXML
    protected void aceesso() throws IOException {
        System.out.println("Login");
        BorderPane parent = (BorderPane) loginVBox.getParent();
        Stage stage = (Stage) loginVBox.getScene().getWindow();

        String userType = SharedData.getInstance().getUserType();
        UtenteGenerico user = SharedData.getInstance().getUser();
        boolean success = user.login("ashish", "pippo");
        if (success)
        {
            // RESTITUISCE LA SCHERMATA HOME DEL SOFTWARE
            GetStage obj = new GetStage();
            obj.set(stage, userType);
        }
        else
        {
            //stampa un messaggio di errore a schermo
            System.out.println("errore di autenticazione");
            loginMessageLable.setText("errore di autenticazione");
        }
    }
}
